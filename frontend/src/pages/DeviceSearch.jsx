import React, { useState } from 'react';
import { searchDevices, updateDevice } from '../api/apiClient';

const DeviceSearch = () => {
  const [results, setResults] = useState([]);
  const [criteria, setCriteria] = useState('');
  const [editedId, setEditedId] = useState('');
  const [editingDevice, setEditingDevice] = useState(null);
  const [editedName, setEditedName] = useState('');
  const [editedProperties, setEditedProperties] = useState({});

  const handleActionButton = (device) => {
    // Initialiser l'état d'édition avec les valeurs actuelles du device
    setEditingDevice(device);
    setEditedId(device.id);
    setEditedName(device.name);
    setEditedProperties({ ...device.properties });
  };

  const handleSaveChanges = async () => {
    try {
      console.log("editingDevice avant mise à jour:", editingDevice);
      console.log("editedId: ",editedId);
      console.log("editedName:", editedName);
      console.log("editedProperties:", editedProperties);
      
      // Vérifier si editingDevice est null ou undefined
      if (!editingDevice) {
        console.error("editingDevice est null ou undefined");
        return;
      }
      //Création d'un objet avec les valeurs modifiées
      const updatedDevice = {
        ...editingDevice,
        name: editedName,
        properties: editedProperties,
        id: editedId
      };
      // Appel API pour mettre à jour le device
      const response = await updateDevice(updatedDevice);

      // Mettre à jour la liste des résultats
      const updatedResults = results.map((device) =>
        device.id === editingDevice.id ? response.data : device
      );

      setResults(updatedResults);
      setEditingDevice(null); // Fermer le mode édition
    } catch (error) {
      console.error('Erreur lors de la mise à jour:', error);
    }
    window.location.reload();
  };

  const handlePropertyChange = (propName, value) => {
    setEditedProperties((prev) => ({
      ...prev,
      [propName]: value,
    }));
  };

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
            {Object.entries(propValue).map(([key, val], editedId) => (
              <li key={editedId}>
                <PropertyRenderer propName={key} propValue={val} />
              </li>
            ))}
          </ul>
        </div>
      );
    }
    return (
      <span>
        {propName}: {propValue}
      </span>
    );
  };

  return (
    <div>
      <form onSubmit={handleSearch}>
        <input
          value={criteria}
          onChange={(e) => setCriteria(e.target.value)}
          placeholder="Search devices"
        />
        <button type="submit">Search</button>
      </form>
      
      {editingDevice ? (
        <div className="edit-form">
          <h3>Modifier l'appareil</h3>
          <input 
            value={editedName} 
            onChange={(e) => setEditedName(e.target.value)} 
            placeholder="Nom de l'appareil" 
          />
          
          {Object.entries(editedProperties).map(([propName, propValue]) => (
            <div key={editedId}>
              <label>{propName}:</label>
              {typeof propValue === 'object' && propValue !== null ? (
                <div>Propriété complexe (édition non prise en charge)</div>
              ) : (
                <input 
                  value={propValue} 
                  onChange={(e) => handlePropertyChange(propName, e.target.value)} 
                />
              )}
            </div>
          ))}
          
          <div className="edit-actions">
            <button type="button" onClick={handleSaveChanges}>Enregistrer</button>
            <button type="button" onClick={() => setEditingDevice(null)}>Annuler</button>
          </div>
        </div>
      ) : (
        <ul>
          {results.map((device) => (
            <li key={device.id}>
              <h3>{device.name}</h3>
              <div className="device-properties">
                {/* Ligne problématique, probablement autour de 130 */}
                {device.properties && Object.entries(device.properties).map(
                  ([propName, propValue]) => (
                    <div key={propName}>
                      <PropertyRenderer
                        propName={propName}
                        propValue={propValue}
                      />
              </div>
                  )
                )}
              </div>
              <button onClick={() => handleActionButton(device)}>Modifier</button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default DeviceSearch;
