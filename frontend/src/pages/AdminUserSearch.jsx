import React, { useState } from 'react';
import { searchUsersAdmin } from '../api/apiClient';
import { updateUser } from '../api/apiClient';
import { redirect } from 'react-router-dom';

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
        <button onClick={() => redirect('/users')}>Search (Admin)</button>
      </form>
      <ul>
        {results.map(user => (
          <li key={user.username}>
            {user.username} - {user.email} - Score: {user.score} - Roles: {user.roleNames.join(', ')}
            <button onClick={() => window.location.assign(`/users/${user.username}`)}>Edit</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default AdminUserSearch;
