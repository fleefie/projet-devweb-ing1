import React, {useState} from 'react';
import { Link } from 'react-router-dom';
import { searchUsers } from '../services/api';

const ControlPanel = ({ isAuthenticated, onLogout, navLinksStyle, linkStyle, buttonStyle }) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [error, setError] = useState('');

  const handleSearch = async () => {
    try {
      const token = localStorage.getItem('token');
      console.log('Token récupéré:', token);
      if(!token){
        setError("Il faut etre auth avec un token !");
        return;
      }
      // Appel de la fonction searchUsers
      const response = await searchUsers(searchQuery, token);
      console.log('Réponse de la recherche:', response); // Affiche les résultats dans la console
      setSearchResults(response.data); // Mettre à jour les résultats de recherche
      setError(''); // Réinitialiser l'erreur si la recherche est réussie
    } catch (err) {
      console.error('Erreur lors de la recherche:', err);
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
              {user.username} - {user.name}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ControlPanel;
