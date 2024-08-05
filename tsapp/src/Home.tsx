import React from 'react';
import './App.css';
import AppNavbar from './AppNavbar';
import {Link, Route} from 'react-router-dom';
import { Button, Container } from 'reactstrap';
import StoreItemScanEventsTable from "./components/StoreItemScanEventsTable";

const Home = () => {
    return (
        <div>
            <AppNavbar/>
            <Container fluid>
                <Button color="link"><Link to="/events">Scan Events</Link></Button>
                <Button color="link"><Link to="/store-inventory">Store Inventory</Link></Button>
                <Button color="link"><Link to="/upc-inventory">UPC Inventory Count</Link></Button>
                <Button color="link"><Link to="/upc-chart">UPC Inventory Chart</Link></Button>
            </Container>
        </div>
    );
}

export default Home;