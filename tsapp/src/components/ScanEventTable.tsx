import React, { useState, useEffect } from 'react';
import DataTable from 'react-data-table-component';
import {EPCScanEvent} from "../model";
import {HOST_NAME, STORE_ID, UPC_NBR, SERVICE_PROTOCOL} from "../constants";


const columns = [
    {name:'Store', selector: (event: EPCScanEvent) => event.storeId, sortable: true},
    {name:'Sensor', selector: (event: EPCScanEvent) => event.sensorId, sortable: true},
    {name:'Timestamp', selector: (event: EPCScanEvent) => (event.timestamp ?? "").toString(), sortable: true},
    {name:'UPC', selector: (event: EPCScanEvent) => event.upc, sortable: true},
    {name:'Serial', selector: (event: EPCScanEvent) => toDefaultString(event.serial), sortable: true},
    {name:'Data', selector: (event: EPCScanEvent) => event.data, sortable: true},
    {name:'RSSI', selector: (event: EPCScanEvent) => event.rssi ?? 0, sortable: true},
    {name:'CorrelationId', selector: (event: EPCScanEvent) => event.correlationId === undefined ? "" : event.correlationId, sortable: true},
]

const toDefaultString = (val: any, ifUndefined = ""): string => val === undefined || val === null ? ifUndefined : val.toString();


const ScanEventTable = () => {
    const [scanEvents, setScanEvents] = useState([]);
    const [loading, setLoading] = useState(false);


    const [globalFilterText, setGlobalFilterText] = useState('');

    useEffect(() => {
        setLoading(true);
        const url = `${SERVICE_PROTOCOL}://${HOST_NAME}/epc/stores/${STORE_ID}/readings`;

        console.info(`Calling scan events service: ${url}`);
        fetch(url)
                .then(response => {
                    const responseJSON = response.json();
                    console.info("Retrieved data back from service. " + JSON.stringify(responseJSON));
                    return responseJSON;
                })
                .then(data => {
                    setScanEvents(data);
                    setLoading(false);
                })
                .catch(error => {
                    console.error('Error:', error);
                })
    }, []);

    if (loading) {
        return <p>Loading...</p>;
    }

    // debugger;
    console.log('Rendering the datatable');
    // @ts-ignore
    return (
        <DataTable
            columns={columns}
            data={scanEvents}
            pagination
            highlightOnHover
            responsive
            paginationPerPage={7}
            paginationRowsPerPageOptions={[5,10,20,50]}
            />
    );
}

export default ScanEventTable;