import React, { useState } from 'react';
import { searchDevices,createDevice } from '../api/apiClient';

const AdminDevicesControl = () => {
  const [results, setResults] = useState([]);
  const [criteria, setCriteria] = useState('');

  // Create new device states
  const [newDevice, setNewDevice] = useState({
    name: '',
    properties: {}
  });

  // Search function
  const handleSearch = async (e) => {
    e.preventDefault();
    try {
      const res = await searchDevices({ criteria });
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

  // Handle input changes for new device
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewDevice({
      ...newDevice,
      [name]: value
    });
  };
  const [propertiesString, setPropertiesString] = useState('{}');
    // Handle properties changes
    const handlePropertiesChange = (e) => {
        setPropertiesString(e.target.value);
        try {
          // Essayer de parser les propriétés JSON
          const properties = JSON.parse(e.target.value);
          setNewDevice({
            ...newDevice,
            properties: properties
          });
        } catch (error) {
          // Si le JSON n'est pas valide, on ne met pas à jour les propriétés
          console.error("Invalid JSON for properties:", error);
        }
      };
  // Create device function
  const handleCreateDevice = async (e) => {
    e.preventDefault();
    try {
      //verif que les proprs sont un json
      let deviceToSend={...newDevice};
      try{
        deviceToSend.properties = JSON.parse(propertiesString);
      } catch (error){
        alert("Properties format is invalid, check JSON syntax");
        return;
      }
      const response = await createDevice(deviceToSend);
      alert(`Device created successfully: ${response.data.device.name}`);
      // Reset form
      setNewDevice({
        name: '',
        properties: {}
      });
      setPropertiesString('{}');
      // Refresh device list
      if (criteria) {
        handleSearch(e);
      }
    } catch (error) {
      console.error("Error creating device:", error);
      alert(`Failed to create device: ${error.message}`);
    }
  };

  return (
    <div className="admin-devices-container">
      {/* Search Section */}
      <div className="search-section">
        <h2>Search Devices</h2>
        <form onSubmit={handleSearch}>
          <input 
            value={criteria} 
            onChange={(e) => setCriteria(e.target.value)} 
            placeholder="Search device by name" 
          />
          <button type="submit">Search</button>
        </form>
        <div className="results">
          {results.length > 0 ? (
            <ul>
              {results.map(device => (
                <li key={device.id}>
                  {device.name} - Type: {device.type} - Status: {device.status}
                </li>
              ))}
            </ul>
          ) : criteria ? <p>No devices found</p> : null}
        </div>
      </div>

      {/* Create Device Section */}
      <div className="create-section">
        <h2>Create New Device</h2>
        <form onSubmit={handleCreateDevice}>
          
          <div className="form-group">
            <label>Name:</label>
            <input
              type="text"
              name="name"
              value={newDevice.name}
              onChange={handleInputChange}
              required
            />
          </div>
          
          <div className="form-group">
            <label>Properties (JSON Format):</label>
            <input
                type="text"
                name="properties"
                values={propertiesString}
                onChange={handlePropertiesChange}
                rows="5"
                placeholder='{"prop1": "value1",...}'
                required
                />
                <small>Enter properties in valid JSON Format</small>
          </div>
          
          <button type="submit">Create Device</button>
        </form>
      </div>
    </div>
  );
};

export default AdminDevicesControl;