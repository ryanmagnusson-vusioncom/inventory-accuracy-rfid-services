
type ServiceProtocol = 'http' | 'https' | 'ftp' | 'ftps' | 'ble';

const HOST_NAME_DEVELOPMENT = '127.0.0.1:8080';
const HOST_NAME_TESTING = '172.206.129.70:80';

const STORE_ID_DEVELOPMENT = 'rd_dev.ryan';
const STORE_ID_TESTING = 'dallas.showroom';

const STORE_ID = STORE_ID_TESTING;
const HOST_NAME = HOST_NAME_DEVELOPMENT;
const UPC_NBR = '4007385009144';
const SERVICE_PROTOCOL: ServiceProtocol = "http";

const STORE_NAMES: Map<string, string> = new Map();
STORE_NAMES.set('dallas.showroom', 'Dallas Showroom');
STORE_NAMES.set('rd_dev.ryan', "Ryan's Playground");
STORE_NAMES.set('lab', 'Dallas Lab');

export { STORE_ID, HOST_NAME, UPC_NBR, SERVICE_PROTOCOL, STORE_NAMES}
export type { ServiceProtocol }