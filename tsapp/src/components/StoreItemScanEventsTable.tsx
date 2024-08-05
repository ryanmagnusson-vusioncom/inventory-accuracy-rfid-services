import React, {useState, useEffect} from 'react';
import DataTable, {ConditionalStyles} from 'react-data-table-component';
import {
    StoreItemInventory,
    SerializedUPC,
    SerializedGTIN,
    SizableStoreItemInventory,
    ItemInventory,
    Store
} from "../model";
import {HOST_NAME, STORE_ID, SERVICE_PROTOCOL, STORE_NAMES} from "../constants";
import fetchStoreInfo from "./Stores";

const columns = [
    {name: 'Store', selector: (row: StoreItemInventory) => row.store.name ?? row.store.storeId, sortable: true},
    {name: 'UPC', selector: (row: StoreItemInventory) => row.upc, sortable: true},
    {name: 'Count', selector: (row: StoreItemInventory) => (row.items ?? []).length, sortable: true},
]

const rowStyles: ConditionalStyles<StoreItemInventory>[] = [
    {
        when: inventory => (inventory.items ?? []).length < 3,
        style: {
            backgroundColor: 'red',
            color: 'white',
            '&:hover': {
                cursor: 'pointer',
            },
        },
    },
    {
        when: inventory => (inventory.items ?? []).length < 5 && (inventory.items ?? []).length > 2,
        style: inventory => ({
            // backgroundColor: 'yellow',
            color: 'yellow'
        }),
    },
    {
        when: inventory => (inventory.items ?? []).length > 4,
        style: inventory => ({
            // backgroundColor: 'green',
            color: 'green'
        }),
    },
];

const fetchAndSetStoreName = (inventory: StoreItemInventory[]): Map<string, Store> => {
    const storeInfo: Map<string, Store> = new Map();
    const storeIds = new Set(inventory.map((inv) => inv.store.storeId))
    storeIds.forEach(id => {
        fetchStoreInfo(id).then(store => {
            if (store) {
                storeInfo.set(id, store)
            }
        });
    });
    return storeInfo;
}

const ScanEventTable = () => {
    const [itemInventory, setItemInventoru] = React.useState<StoreItemInventory[]>([]);
    const [loading, setLoading] = useState(false);
    const [storeInfo, setStoreInfo] = useState<Map<string, Store>>(new Map());
    const [globalFilterText, setGlobalFilterText] = useState('');

    useEffect(() => {
        setLoading(true);

        const url = `${SERVICE_PROTOCOL}://${HOST_NAME}/inventory/stores/${STORE_ID}/items`;

        console.info(`Calling item inventory service: ${url}`);
        fetch(url)
                .then(response => response.json())
                .then(payload => {
                    console.info("Retrieved data back from service. " + JSON.stringify(payload));
                    let itemInventory: StoreItemInventory[] = [];
                    if (payload !== undefined) {
                        itemInventory = payload.map((i: {
                            storeId: string;
                            upc: string;
                            items: SerializedUPC[] | SerializedGTIN[] | undefined;
                        }) => new SizableStoreItemInventory(i.storeId, i.upc, i.items ?? []))
                    }
                    itemInventory.forEach(inv => inv.store.name = STORE_NAMES.get(inv.store.storeId))
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
        />
    );
}

export default ScanEventTable;