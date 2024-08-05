
type ServiceProtocol = 'http' | 'https' | 'ftp' | 'ftps' | 'ble';

const HOST_NAME_DEVELOPMENT = '127.0.0.1:8080';
const HOST_NAME_TESTING = '172.206.129.70:80';

const STORE_ID_DEVELOPMENT = 'rd_dev.ryan';
const STORE_ID_TESTING = 'lab';

const STORE_ID = STORE_ID_TESTING;
const HOST_NAME = HOST_NAME_TESTING;
const UPC_NBR = '4007385009144';
const SERVICE_PROTOCOL: ServiceProtocol = "http";

export { STORE_ID, HOST_NAME, UPC_NBR, SERVICE_PROTOCOL }
export type { ServiceProtocol }