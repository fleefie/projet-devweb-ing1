import React, { useState } from 'react';
import { searchDevices } from '../api/apiClient';

const DeviceSearch = () => {
    const [results, setResults] = useState([]);
    const [criteria, setCriteria] = useState('');

    const handleSearch = async (e) => {
        e.preventDefault();
        const res = await searchDevices({ query: criteria });
        setResults(res.data.devices);
    };

    const PropertyRenderer = ({ propName, propValue }) => {
        if (typeof propValue === 'object' && propValue !== null) {
            return (
                <div>
                <span>{propName}:</span>
                <ul>
                {Object.entries(propValue).map(([key, val], idx) => (
                    <li key={idx}>
                    <PropertyRenderer propName={key} propValue={val} />
                    </li>
                ))}
                </ul>
                </div>
            );
        }
        return <span>{propName}: {propValue}</span>;
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
              <h3>{device.name}</h3>
              <div className="device-properties">
                {Object.entries(device.properties).map(([propName, propValue], idx) => (
                  <div key={idx}>
                    <PropertyRenderer propName={propName} propValue={propValue} />
                  </div>
                ))}
              </div>
            </li>
          ))}
        </ul>
        </div>
    );
};

export default DeviceSearch;
