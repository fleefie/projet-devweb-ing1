import React, {useState} from 'react';
import { Link } from 'react-router-dom';
import { userAPI } from '../services/api';

const ControlPanel = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [error, setError] = useState('');

  const handleSearch = async () => {
    try {
      // Appel de la fonction searchUsers
      const response = await userAPI.searchUsers(searchQuery);
      setSearchResults(response.data); // Mettre à jour les résultats de recherche
      setError(''); // Réinitialiser l'erreur si la recherche est réussie
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la recherche des utilisateurs.');
    }
  };
  return (
    <div className="card">
      <h1>Control Panel</h1>

      <div>
        <input
          type="text"
          placeholder="Search users by name"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        <button onClick={handleSearch}>Search</button>
      </div>

      {error && <div style={{ color: 'red' }}>{error}</div>}

      {searchResults.length > 0 && (
        <ul>
          {searchResults.map((user, index) => (
            <li key={index}>
              {user.username} - {user.roleNames}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ControlPanel;
