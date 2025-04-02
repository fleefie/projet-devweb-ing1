import React, { useState } from 'react';
import { searchDevices } from '../api/apiClient';

const AdminUserSearch = () => {
  const [results, setResults] = useState([]);
  const [criteria, setCriteria] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    const res = await searchDevices({ username: criteria });
    setResults(res.data);
  };

  return (
    <div>
      <form onSubmit={handleSearch}>
        <input value={criteria} onChange={(e) => setCriteria(e.target.value)} placeholder="Search device" />
        <button type="submit">Search (Admin)</button>
      </form>
      <ul>
        {results.map(user => (
          <li key={device.name}>
            {user.username} - {user.email} - Roles: {user.roleNames.join(', ')}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default AdminUserSearch;
