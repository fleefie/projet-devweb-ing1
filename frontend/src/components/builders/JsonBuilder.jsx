import React, { useState, useEffect } from 'react';
import './JsonBuilder.css';

/**
 * A visual JSON builder component with a flat, visible hierarchy
 * 
 * @param {Object} props
 * @param {Function} props.onChange - Callback function when JSON changes
 * @param {Object} props.initialValue - Initial JSON value (optional)
 * @param {String} props.title - Title for the builder (optional)
 */
const JsonBuilder = ({ onChange, initialValue = {}, title = "JSON Builder" }) => {
  // Main state for the JSON data
  const [jsonData, setJsonData] = useState(initialValue);
  // Toggle for preview visibility
  const [showPreview, setShowPreview] = useState(false);
  // Toggle for pretty vs raw JSON in preview
  const [isPrettyPreview, setIsPrettyPreview] = useState(true);
  // New key input state
  const [newKey, setNewKey] = useState('');
  // Track validation errors
  const [keyErrors, setKeyErrors] = useState({});
  // Track empty key attempts
  const [emptyKeyAttempt, setEmptyKeyAttempt] = useState(false);

  // Update the JSON data and trigger onChange callback
  const updateJson = (newData) => {
    setJsonData(newData);
  };

  // Effect to notify parent component of changes
  useEffect(() => {
    if (onChange) {
      onChange(jsonData);
    }
  }, [jsonData, onChange]);

  // Check if a key already exists at a given path
  const keyExistsAtPath = (path, key) => {
    const keys = path ? path.split('.') : [];
    let current = jsonData;
    
    // Navigate to the target object
    for (let i = 0; i < keys.length; i++) {
      if (keys[i].includes('[')) {
        // Handle array index
        const arrayKey = keys[i].split('[')[0];
        const index = parseInt(keys[i].split('[')[1].replace(']', ''));
        current = current[arrayKey][index];
      } else {
        current = current[keys[i]];
      }
    }
    
    return Object.keys(current).includes(key);
  };

  // Add a new top-level string key/value
  const addStringKey = () => {
    if (!newKey.trim()) {
      setEmptyKeyAttempt(true);
      setTimeout(() => setEmptyKeyAttempt(false), 2000);
      return;
    }
    
    if (keyExistsAtPath('', newKey)) {
      setKeyErrors({ '': newKey });
      setTimeout(() => setKeyErrors({}), 2000);
      return;
    }
    
    const updatedJson = { ...jsonData };
    updatedJson[newKey] = "";
    updateJson(updatedJson);
    setNewKey('');
  };

  // Add a new top-level object
  const addObjectKey = () => {
    if (!newKey.trim()) {
      setEmptyKeyAttempt(true);
      setTimeout(() => setEmptyKeyAttempt(false), 2000);
      return;
    }
    
    if (keyExistsAtPath('', newKey)) {
      setKeyErrors({ '': newKey });
      setTimeout(() => setKeyErrors({}), 2000);
      return;
    }
    
    const updatedJson = { ...jsonData };
    updatedJson[newKey] = {};
    updateJson(updatedJson);
    setNewKey('');
  };

  // Add a new top-level array/list
  const addListKey = () => {
    if (!newKey.trim()) {
      setEmptyKeyAttempt(true);
      setTimeout(() => setEmptyKeyAttempt(false), 2000);
      return;
    }
    
    if (keyExistsAtPath('', newKey)) {
      setKeyErrors({ '': newKey });
      setTimeout(() => setKeyErrors({}), 2000);
      return;
    }
    
    const updatedJson = { ...jsonData };
    updatedJson[newKey] = [];
    updateJson(updatedJson);
    setNewKey('');
  };

  // Update a string value
  const updateValue = (path, value) => {
    const keys = path.split('.');
    const updatedJson = { ...jsonData };
    let current = updatedJson;
    
    // Navigate to the parent object
    for (let i = 0; i < keys.length - 1; i++) {
      if (keys[i].includes('[')) {
        // Handle array index
        const arrayKey = keys[i].split('[')[0];
        const index = parseInt(keys[i].split('[')[1].replace(']', ''));
        current = current[arrayKey][index];
      } else {
        current = current[keys[i]];
      }
    }
    
    // Update the value
    const lastKey = keys[keys.length - 1];
    if (lastKey.includes('[')) {
      // Handle array index
      const arrayKey = lastKey.split('[')[0];
      const index = parseInt(lastKey.split('[')[1].replace(']', ''));
      current[arrayKey][index] = value;
    } else {
      current[lastKey] = value;
    }
    
    updateJson(updatedJson);
  };

  // Add a key to an object at the given path
  const addToObject = (path, keyName, type) => {
    if (!keyName.trim()) {
      // Set error for this specific path
      setKeyErrors(prev => ({ ...prev, [path]: true }));
      setTimeout(() => setKeyErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[path];
        return newErrors;
      }), 2000);
      return;
    }
    
    if (keyExistsAtPath(path, keyName)) {
      // Set duplicate key error for this path
      setKeyErrors(prev => ({ ...prev, [path]: keyName }));
      setTimeout(() => setKeyErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[path];
        return newErrors;
      }), 2000);
      return;
    }
    
    const keys = path.split('.');
    const updatedJson = { ...jsonData };
    let current = updatedJson;
    
    // Navigate to the target object
    for (let i = 0; i < keys.length; i++) {
      if (keys[i].includes('[')) {
        // Handle array index
        const arrayKey = keys[i].split('[')[0];
        const index = parseInt(keys[i].split('[')[1].replace(']', ''));
        current = current[arrayKey][index];
      } else {
        current = current[keys[i]];
      }
    }
    
    // Add the new key with appropriate type
    if (type === 'string') {
      current[keyName] = '';
    } else if (type === 'object') {
      current[keyName] = {};
    } else if (type === 'array') {
      current[keyName] = [];
    }
    
    updateJson(updatedJson);
  };

  // Add an item to an array at the given path
  const addToArray = (path, type) => {
    const keys = path.split('.');
    const updatedJson = { ...jsonData };
    let current = updatedJson;
    
    // Navigate to the target array
    for (let i = 0; i < keys.length; i++) {
      if (keys[i].includes('[')) {
        // Handle array index
        const arrayKey = keys[i].split('[')[0];
        const index = parseInt(keys[i].split('[')[1].replace(']', ''));
        current = current[arrayKey][index];
      } else {
        current = current[keys[i]];
      }
    }
    
    // Add new item with appropriate type
    if (type === 'string') {
      current.push('');
    } else if (type === 'object') {
      current.push({});
    } else if (type === 'array') {
      current.push([]);
    }
    
    updateJson(updatedJson);
  };

  // Delete a key or array item
  const deleteItem = (path) => {
    const keys = path.split('.');
    const updatedJson = { ...jsonData };
    let current = updatedJson;
    
    // Special case for top-level keys
    if (keys.length === 1) {
      delete updatedJson[keys[0]];
      updateJson(updatedJson);
      return;
    }
    
    // Navigate to the parent
    for (let i = 0; i < keys.length - 2; i++) {
      if (keys[i].includes('[')) {
        // Handle array index
        const arrayKey = keys[i].split('[')[0];
        const index = parseInt(keys[i].split('[')[1].replace(']', ''));
        current = current[arrayKey][index];
      } else {
        current = current[keys[i]];
      }
    }
    
    // Get the parent key/index and the item to delete
    const parentKey = keys[keys.length - 2];
    const itemKey = keys[keys.length - 1];
    
    if (parentKey.includes('[')) {
      // Parent is an array item
      const arrayKey = parentKey.split('[')[0];
      const index = parseInt(parentKey.split('[')[1].replace(']', ''));
      if (itemKey.includes('[')) {
        // Delete from array in array
        const childArrayKey = itemKey.split('[')[0];
        const childIndex = parseInt(itemKey.split('[')[1].replace(']', ''));
        current[arrayKey][index][childArrayKey].splice(childIndex, 1);
      } else {
        // Delete property from object in array
        delete current[arrayKey][index][itemKey];
      }
    } else if (itemKey.includes('[')) {
      // Delete from array
      const arrayKey = itemKey.split('[')[0];
      const index = parseInt(itemKey.split('[')[1].replace(']', ''));
      current[parentKey][arrayKey].splice(index, 1);
    } else {
      // Delete regular property
      delete current[parentKey][itemKey];
    }
    
    updateJson(updatedJson);
  };
  
  // Render object add controls
  const renderObjectAddControls = (path) => {
    const hasError = keyErrors[path] === true;
    const duplicateKey = typeof keyErrors[path] === 'string' ? keyErrors[path] : null;
    
    return (
      <div className="json-value-controls">
        <input
          type="text"
          placeholder="New key"
          className={`new-key-input ${hasError || duplicateKey ? 'input-error' : ''}`}
          onKeyDown={(e) => {
            if (e.key === 'Enter' && e.target.value) {
              addToObject(path, e.target.value, 'string');
              if (!keyExistsAtPath(path, e.target.value)) {
                e.target.value = '';
              }
            }
          }}
        />
        {duplicateKey && <div className="error-tooltip">Key "{duplicateKey}" already exists</div>}
        <button onClick={(e) => {
          const input = e.target.previousSibling;
          if (input.value) {
            addToObject(path, input.value, 'string');
            if (!keyExistsAtPath(path, input.value)) {
              input.value = '';
            }
          } else {
            setKeyErrors(prev => ({ ...prev, [path]: true }));
            setTimeout(() => setKeyErrors(prev => {
              const newErrors = { ...prev };
              delete newErrors[path];
              return newErrors;
            }), 2000);
          }
        }} className="add-btn">+ String</button>
        <button onClick={(e) => {
          const input = e.target.previousSibling.previousSibling;
          if (input.value) {
            addToObject(path, input.value, 'object');
            if (!keyExistsAtPath(path, input.value)) {
              input.value = '';
            }
          } else {
            setKeyErrors(prev => ({ ...prev, [path]: true }));
            setTimeout(() => setKeyErrors(prev => {
              const newErrors = { ...prev };
              delete newErrors[path];
              return newErrors;
            }), 2000);
          }
        }} className="add-btn">+ Object</button>
        <button onClick={(e) => {
          const input = e.target.previousSibling.previousSibling.previousSibling;
          if (input.value) {
            addToObject(path, input.value, 'array');
            if (!keyExistsAtPath(path, input.value)) {
              input.value = '';
            }
          } else {
            setKeyErrors(prev => ({ ...prev, [path]: true }));
            setTimeout(() => setKeyErrors(prev => {
              const newErrors = { ...prev };
              delete newErrors[path];
              return newErrors;
            }), 2000);
          }
        }} className="add-btn">+ List</button>
        <button onClick={() => deleteItem(path)} className="delete-btn">Delete</button>
      </div>
    );
  };

  // Render the JSON builder UI recursively
  const renderJsonItems = (data, path = '', depth = 0) => {
    if (typeof data !== 'object' || data === null) {
      return null;
    }

    return Object.keys(data).map(key => {
      const currentPath = path ? `${path}.${key}` : key;
      const value = data[key];
      const indentStyle = { paddingLeft: `${depth * 20}px` };
      
      if (Array.isArray(value)) {
        // Render array
        return (
          <div key={currentPath} className="json-item-container">
            <div className="json-item" style={indentStyle}>
              <div className="json-key-wrapper">
                <span className="json-key">{key}</span>
                <span className="json-type">List</span>
              </div>
              <div className="json-value-controls">
                <button onClick={() => addToArray(currentPath, 'string')} className="add-btn">+ String</button>
                <button onClick={() => addToArray(currentPath, 'object')} className="add-btn">+ Object</button>
                <button onClick={() => addToArray(currentPath, 'array')} className="add-btn">+ List</button>
                <button onClick={() => deleteItem(currentPath)} className="delete-btn">Delete</button>
              </div>
            </div>
            {value.length > 0 && (
              <div className="json-children">
                {value.map((item, index) => {
                  const itemPath = `${currentPath}[${index}]`;
                  if (typeof item === 'object' && item !== null) {
                    // Render nested object or array
                    return (
                      <div key={itemPath} className="json-item-container">
                        <div className="json-item" style={{ ...indentStyle, paddingLeft: `${(depth + 1) * 20}px` }}>
                          <div className="json-key-wrapper">
                            <span className="json-key">[{index}]</span>
                            <span className="json-type">{Array.isArray(item) ? 'List' : 'Object'}</span>
                          </div>
                          {Array.isArray(item) ? (
                            <div className="json-value-controls">
                              <button onClick={() => addToArray(itemPath, 'string')} className="add-btn">+ String</button>
                              <button onClick={() => addToArray(itemPath, 'object')} className="add-btn">+ Object</button>
                              <button onClick={() => addToArray(itemPath, 'array')} className="add-btn">+ List</button>
                              <button onClick={() => deleteItem(itemPath)} className="delete-btn">Delete</button>
                            </div>
                          ) : (
                            renderObjectAddControls(itemPath)
                          )}
                        </div>
                        {renderJsonItems(item, itemPath, depth + 2)}
                      </div>
                    );
                  } else {
                    // Render primitive value
                    return (
                      <div key={itemPath} className="json-item" style={{ ...indentStyle, paddingLeft: `${(depth + 1) * 20}px` }}>
                        <div className="json-key-wrapper">
                          <span className="json-key">[{index}]</span>
                        </div>
                        <div className="json-value-controls">
                          <input
                            type="text"
                            value={item || ''}
                            onChange={(e) => updateValue(itemPath, e.target.value)}
                            className="json-value-input"
                          />
                          <button onClick={() => deleteItem(itemPath)} className="delete-btn">Delete</button>
                        </div>
                      </div>
                    );
                  }
                })}
              </div>
            )}
          </div>
        );
      } else if (typeof value === 'object' && value !== null) {
        // Render object
        return (
          <div key={currentPath} className="json-item-container">
            <div className="json-item" style={indentStyle}>
              <div className="json-key-wrapper">
                <span className="json-key">{key}</span>
                <span className="json-type">Object</span>
              </div>
              {renderObjectAddControls(currentPath)}
            </div>
            {renderJsonItems(value, currentPath, depth + 1)}
          </div>
        );
      } else {
        // Render string/primitive value
        return (
          <div key={currentPath} className="json-item" style={indentStyle}>
            <div className="json-key-wrapper">
              <span className="json-key">{key}</span>
            </div>
            <div className="json-value-controls">
              <input
                type="text"
                value={value || ''}
                onChange={(e) => updateValue(currentPath, e.target.value)}
                className="json-value-input"
              />
              <button onClick={() => deleteItem(currentPath)} className="delete-btn">Delete</button>
            </div>
          </div>
        );
      }
    });
  };

  // Preview the JSON as formatted text
  const renderPreview = () => {
    if (!showPreview) return null;
    
    return (
      <div className="json-preview">
        <div className="preview-header">
          <h3>Preview</h3>
          <button 
            onClick={() => setIsPrettyPreview(!isPrettyPreview)}
            className="toggle-format-btn"
          >
            {isPrettyPreview ? 'View Raw' : 'View Pretty'}
          </button>
        </div>
        <pre>
          {isPrettyPreview 
            ? JSON.stringify(jsonData, null, 2) 
            : JSON.stringify(jsonData)}
        </pre>
      </div>
    );
  };

  return (
    <div className="json-builder">
      <div className="builder-header">
        <h2>{title}</h2>
      </div>
      
      <div className="builder-controls">
        <button 
          onClick={() => setShowPreview(!showPreview)}
          className="toggle-preview-btn"
        >
          {showPreview ? 'Hide Preview' : 'Show Preview'}
        </button>
        
        <div className="add-controls">
          <input
            type="text"
            placeholder="New key"
            value={newKey}
            onChange={(e) => setNewKey(e.target.value)}
            className={`new-key-input ${emptyKeyAttempt || keyErrors[''] ? 'input-error' : ''}`}
          />
          {keyErrors[''] && <div className="error-tooltip">Key "{keyErrors['']}" already exists</div>}
          <button onClick={addStringKey} className="add-btn">+ String</button>
          <button onClick={addObjectKey} className="add-btn">+ Object</button>
          <button onClick={addListKey} className="add-btn">+ List</button>
        </div>
      </div>
      
      <div className="json-builder-content">
        {renderJsonItems(jsonData)}
      </div>
      
      {renderPreview()}
    </div>
  );
};

export default JsonBuilder;

