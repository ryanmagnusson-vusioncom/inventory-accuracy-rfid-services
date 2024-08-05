import {DateTime} from 'luxon';
import {IllegalArgumentException} from "@js-joda/core";

type EPCScan = {
    sensorId: string;
    data: string;
    rssi?: number;
    timestamp: DateTime;
}

//     constructor(sensorId: string, data: string);
//     constructor(sensorId: string, data: string, rssi?: string);
//     constructor(sensorId: string, data: string, rssi = undefined, timestamp = DateTime.now()) {
//         this.sensorId = sensorId;
//         this.data = data;
//         this.rssi = rssi;
//         this.timestamp = timestamp;
//     }
// }

type EPCScanEvent = EPCScan & {
    storeId: string;
    upc: string;
    serial?: number;
    correlationId?: string;
}

//     constructor(storeId: string,
//                 upc: string,
//                 serial: number = -1,
//                 sensorId: string,
//                 correlationId = undefined,
//                 data: string, rssi = undefined, timestamp = DateTime.now()) {
//         // @ts-ignore
//         super(sensorId, data, rssi, timestamp);
//         this.storeId = storeId;
//         this.upc = upc;
//         this.serial = serial;
//         this.correlationId = correlationId;
//     }
// }

type SerializedUPC = {
    upc: string;
    serialNumber: number;
}

type SerializedGTIN = SerializedUPC & {
    epc: string;
}

type StoreItemInventory = {
    store: Store;
    upc: string;
    items: SerializedUPC[];
}

class SizableStoreItemInventory implements StoreItemInventory {
    store: Store;
    upc: string;
    items: SerializedUPC[];

    size(): number {
        return this.items === undefined ? 0 : this.items.length;
    }

    constructor(storeId: string, upc: string);
    constructor(storeId: string, upc: string, items: SerializedUPC[]);
    constructor(storeId: string, upc: string, items: SerializedUPC[], storeName: string);
    constructor(storeId: string, upc: string, items: SerializedUPC[] = [], storeName = "") {
        this.store = { storeId, name: storeName };
        this.upc = upc;
        this.items = items;
    }
}

type ActiveStoreStatus = 'ACTIVE' | 'active' | 'Active';
type DisableStoreStatus = 'DISABLE' | 'disable' | 'Disable';
type StoreStatusString =  ActiveStoreStatus | DisableStoreStatus;

enum StoreStatus {
    Active,
    Disable
}

const getStoreStatusFromString = (text: string) => {
    if (text === undefined || text === null) {
        return undefined;
    }

    const trimmed = text.trim();
    if (trimmed === '') {
        return undefined;
    }

    if (['ACTIVE', 'active', 'Active'].includes(trimmed)) {
        return StoreStatus.Active;
    }
    if (['DISABLE', 'disable', 'Disable'].includes(trimmed)) {
        return StoreStatus.Disable;
    }
    throw new IllegalArgumentException(`No matching StoreStatus found for '${trimmed}'`);
}

type Store = {
    storeId: string;
    name?: string;
    status?: StoreStatus;
}

type ItemInventory = {
    upc: string;
    count: number;
}

type ItemInventorySnapshot = ItemInventory & {
    timestamp: DateTime;
    upc: string;
    count: number;
}

type MultiItemInventorySnapshot = {
    timestamp: DateTime;
    inventory: ItemInventory[]
}

const countItemInventoryEPCTags = (inventory: StoreItemInventory): number =>
    inventory === undefined || inventory.items === undefined ? 0 : inventory.items.length;

const mapScanEventsToItemInventory = (events: EPCScanEvent[]): StoreItemInventory[] => {
    if (events === undefined || events.length === 0) {
        return []
    }

    const inventories: StoreItemInventory[] = [];

    events.forEach(event => {
        // @ts-ignore
        let itemInv = inventories.find(inventory => inventory.storeId === event.storeId && inventory.upc === event.upc).f
        if (itemInv === undefined) {
            itemInv = new SizableStoreItemInventory(event.storeId, event.upc)
            inventories.push(itemInv);
        }
        if (itemInv.epcs.find(event.data) === undefined) {
            itemInv.epcs.push(event.data);
        }
    });
    return inventories;
}

export {mapScanEventsToItemInventory, SizableStoreItemInventory, getStoreStatusFromString};
export type { Store, ItemInventory, StoreItemInventory, EPCScan, EPCScanEvent, SerializedGTIN, SerializedUPC, ItemInventorySnapshot, MultiItemInventorySnapshot };
