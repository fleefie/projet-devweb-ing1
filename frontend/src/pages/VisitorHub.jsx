import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {searchDevices} from '../api/apiClient';

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
            <p style={{ textAlign: 'center', paddingTop: 60}}>Welcome to the Ygrec website, you are currently in guest mode.</p>
            <p style={{ textAlign: 'center', paddingTop: 40}}>You still can search local informations about the city !</p>
            <div>
            <form onSubmit={handleSearch}>
                <input
                value={criteria}
                onChange={(e) => setCriteria(e.target.value)}
                placeholder="Search devices"
                />
                <button type="submit">Search</button>
            </form>
            </div>
        </div>
    );
};

export default VisitorHub;
