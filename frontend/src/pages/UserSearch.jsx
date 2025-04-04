import React, { useState } from 'react';
import { searchUsers } from '../api/apiClient';
import { useNavigate } from 'react-router-dom';

const UserSearch = () => {
  const [results, setResults] = useState([]);
  const [criteria, setCriteria] = useState('');
  const navigate = useNavigate();

//functions
  const handleClick = (user) => {
    console.log(user);
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
      <ul>
        {results.map((user) => (
          <div
            key={user.username}
            style={{
              backgroundColor: 'beige',
              margin: '30px',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
            }}
            onClick={() => handleClick(user)} // Pass the user as a parameter
            className="userSearch cursor-pointer p-4 border"
          >
            <h3>{user.username}</h3>
            <li>
              - Score: {user.points} - {user.roleNames.join(', ')} - Birthdate: {user.birthdate}
            </li>
          </div>
        ))}
      </ul>
    </div>
  );
};

export default UserSearch;
