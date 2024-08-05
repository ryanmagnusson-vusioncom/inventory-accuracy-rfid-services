import React,
{ useEffect } from 'react';
import axios from 'axios';

const AxiosGetEvents = () => {
    useEffect(() => {

        const storeId = 'lab';
        const baseURL = '127.0.0.1:8080';
        const url = `https://${baseURL}/epc/stores/${storeId}/readings`;

        axios.get(url)
                .then(response => {
                    console.log('Response data:', response.data);
                })
                .catch(error => {
                    console.error('Error:', error);
                });
    }, []);

    return (
            <>
                <h1>Get ScanEvents using Axios</h1>
            </>
    );
}


