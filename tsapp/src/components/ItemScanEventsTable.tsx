import React, {useState, useEffect} from 'react';
import DataTable, {ConditionalStyles} from 'react-data-table-component';
import {ItemInventory, SerializedUPC, SerializedGTIN, StoreItemInventory} from "../model";
import {HOST_NAME, STORE_ID, SERVICE_PROTOCOL} from "../constants";

const columns = [
    {name: 'UPC', selector: (row: ItemInventory) => row.upc, sortable: true},
    {name: 'Count', selector: (row: ItemInventory) => row.count, sortable: true},
]

const rowStyles: ConditionalStyles<ItemInventory>[] = [
    {
        when: inventory => inventory.count < 3,
        style: {
            backgroundColor: 'red',
            color: 'white',
            '&:hover': {
                cursor: 'pointer',
            },
        },
    },
    {
        when: inventory => inventory.count < 5 && inventory.count > 2,
        style: inventory => ({
            // backgroundColor: 'yellow',
            color: 'yellow'
        }),
    },
    {
        when: inventory => inventory.count > 4,
        style: inventory => ({
            // backgroundColor: 'green',
            color: 'green'
        }),
    },
];



const ScanEventTable = () => {
    const [itemInventory, setItemInventoru] = React.useState<ItemInventory[]>([]);
    const [loading, setLoading] = useState(false);


    //const [globalFilterText, setGlobalFilterText] = useState('');

    useEffect(() => {
        setLoading(true);
        const url = `${SERVICE_PROTOCOL}://${HOST_NAME}/inventory/stores/${STORE_ID}/items`;
        console.info(`Calling item inventory service: ${url}`);
        fetch(url)
                .then(response => {
                    const responseJSON = response.json();
                    console.info("Retrieved data back from service. " + JSON.stringify(responseJSON));
                    return responseJSON;
                })
                .then(payload => {
                    let itemInventory: ItemInventory[] = [];
                    if (payload !== undefined) {
                        console.log(JSON.stringify(payload));
                        payload.map((sii: StoreItemInventory) => {
                            const inv: ItemInventory = {
                                upc: sii.upc,
                                count: (sii.items ?? []).length
                            };
                            itemInventory.push(inv);
                        });
                    }
                    setItemInventoru(itemInventory);
                    setLoading(false);
                })
                .catch(error => {
                    console.error('Error:', error);
                })
    }, []);

    if (loading) {
        return <p>Loading...</p>;
    }

    console.log('Rendering the datatable');
    return (
        <DataTable
            columns={columns}
            data={itemInventory}
            pagination
            highlightOnHover
            responsive
            paginationPerPage={7}
            paginationRowsPerPageOptions={[5,10,20,50]}
            conditionalRowStyles={rowStyles}
        />
    );
}

export default ScanEventTable;