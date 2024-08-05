import React from 'react';
import './App.css';
import Home from './Home';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import ScanEventTable from "./components/ScanEventTable";
import ItemScanEventsTable from "./components/ItemScanEventsTable";
import StoreItemScanEventsTable from "./components/StoreItemScanEventsTable";
import ItemInventoryLineChart from "./components/ItemInventoryLineChart";

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path='/events' element={<ScanEventTable/>}/>
                <Route path='/store-inventory' element={<StoreItemScanEventsTable />}/>
                <Route path='/upc-inventory' element={<ItemScanEventsTable />}/>
                <Route path='/upc-chart' element={<ItemInventoryLineChart />}/>
            </Routes>
        </Router>
    )
}

export default App;
