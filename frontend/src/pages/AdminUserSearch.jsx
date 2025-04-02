import React, { useState } from 'react';
import { searchUsersAdmin } from '../api/apiClient';

const AdminUserSearch = () => {
  const [results, setResults] = useState([]);
  const [criteria, setCriteria] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    const res = await searchUsersAdmin({ username: criteria });
    setResults(res.data);
  };

  return (
    <div>
      <form onSubmit={handleSearch}>
        <input value={criteria} onChange={(e) => setCriteria(e.target.value)} placeholder="Search username" />
        <button type="submit">Search (Admin)</button>
      </form>
      <ul>
        {results.map(user => (
          <li key={user.username}>
            {user.username} - {user.email} - Score: {user.points} - Roles: {user.roleNames.join(', ')}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default AdminUserSearch;
