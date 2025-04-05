import React, { useState } from 'react';
import { searchUsers } from '../api/apiClient';
import { useNavigate } from 'react-router-dom';
import './UserSearch.css'; // Import du CSS

const UserSearch = () => {
  const [results, setResults] = useState([]);
  const [criteria, setCriteria] = useState('');
  const navigate = useNavigate();

  const handleClick = (user) => {
    navigate(`/users/${user.username}`);
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    const res = await searchUsers({ username: criteria });
    setResults(res.data);
  };

  return (
    <div>
      <form onSubmit={handleSearch}>
        <input
          value={criteria}
          onChange={(e) => setCriteria(e.target.value)}
          placeholder="Search username"
        />
        <button type="submit">Search</button>
      </form>
      <ul className="user-search-container">
        {results.map((user) => (
          <div
            key={user.username}
            className={`user-card ${user.roleNames.includes('ADMIN') ? 'admin' : 'user'}`}
            onClick={() => handleClick(user)}
          >
            <h3 className="username">{user.username}</h3>
            <div className="user-details">
              - Score: {user.score} - {user.roleNames.join(', ')} - Birthdate: {user.birthdate}
            </div>
          </div>
        ))}
      </ul>
    </div>
  );
};

export default UserSearch;
