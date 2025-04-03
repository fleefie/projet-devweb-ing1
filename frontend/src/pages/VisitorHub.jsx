import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {searchDevices} from '../api/apiClient';
import DeviceSearch from './DeviceSearch';

const VisitorHub = () => {
    const [results, setResults] = useState([]);
    const [criteria, setCriteria] = useState('');

    const handleSearch = async (e) => {
        e.preventDefault();
        try {
            const res = await searchDevices({ query: criteria });
            console.log("Response:", res);
            setResults(res.data.devices || []);
        } catch (error) {
            console.error("Error searching devices:", error);
            if (error.response) {
                console.error("Response data:", error.response.data);
                console.error("Response status:", error.response.status);
            }
        }
    };

    return (
        <div>
            <p>Salut jeune visiteur, tu n'es pas connecté</p>
            <p>You still can search local informations about the city!</p>
            <div>
                <DeviceSearch/>
            </div>
        </div>
    );
};

export default VisitorHub;
