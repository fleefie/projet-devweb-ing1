import React, { useState } from 'react';
import { searchDevices } from '../api/apiClient';

const DeviceSearch = () => {
    const [results, setResults] = useState([]);
    const [criteria, setCriteria] = useState('');

    const handleSearch = async (e) => {
        e.preventDefault();
        const res = await searchDevices({ query: criteria });
        setResults(res.data);
    };

    const renderProperties = (properties) => {
        return Object.entries(properties).map(([key, value]) => {
            if (typeof value === 'object') {
                return (
                    <li key={key}>
                        {key}:
                        <ul>
                            {renderProperties(value)}
                        </ul>
                    </li>
                );
            } else {
                return (
                    <li key={key}>
                        {key}: {value}
                    </li>
                );
            }
        });
    };

    return (
        <div>
            <form onSubmit={handleSearch}>
                <input value={criteria} onChange={(e) => setCriteria(e.target.value)} placeholder="Search devices" />
                <button type="submit">Search</button>
            </form>
            <ul>
                {results.map(device => (
                    <li key={device.id}>
                        {device.name}
                        <ul>
                            {renderProperties(device.properties)}
                        </ul>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default DeviceSearch;
