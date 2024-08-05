
import {HOST_NAME, SERVICE_PROTOCOL} from "../constants";

import {Store, getStoreStatusFromString} from "../model";


const fetchStoreInfo = async (storeId: string) => {

    const url = `${SERVICE_PROTOCOL}://${HOST_NAME}/stores/${storeId}`;

    console.debug(`Calling store info service: ${url}`);
    const response = await fetch(url);
    const payload = await response.json();
    let store = undefined;
    if (payload !== undefined) {
        console.log(JSON.stringify(payload));
        store = {
                storeId: payload.storeId,
                name: payload.name,
                status: getStoreStatusFromString(payload.status)
            };
        console.log(`Store is: ${JSON.stringify(store)}`);
    }
    return store;
};

export default fetchStoreInfo;