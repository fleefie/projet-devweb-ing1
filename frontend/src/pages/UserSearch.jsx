import React, { useState } from 'react';
import { searchUsers } from '../api/apiClient';

const UserSearch = () => {
  const [results, setResults] = useState([]);
  const [criteria, setCriteria] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    const res = await searchUsers({ username: criteria });
    setResults(res.data);
  };

  return (
    <div>
      <form onSubmit={handleSearch}>
        <input value={criteria} onChange={(e) => setCriteria(e.target.value)} placeholder="Search username" />
        <button type="submit">Search</button>
      </form>
      <ul>
        {results.map(user => (
          <div>
            <h3>{user.username}</h3>
          <li key={user.username}> - Score: {user.points} - {user.roleNames.join(', ')} - Birthdate: {user.birthdate}</li>
          </div>
        ))}
      </ul>
    </div>
  );
};

export default UserSearch;
