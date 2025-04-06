import React, { useState } from 'react';
import { searchUsersAdmin } from '../api/apiClient';
import { updateUser } from '../api/apiClient';

const AdminUserSearch = () => {
  const [results, setResults] = useState([]);
  const [criteria, setCriteria] = useState('');

  const [userData, setUserData] = useState(null);
  const [error, setError] = useState(null);
  const [editingUser, setEditingUser] = useState(null);
  const [editedUser, setEditedUser] = useState({});

  const handleActionButton = (user) => {
    // Initialiser l'état d'édition avec les valeurs actuelles de l'utilisateur
    setEditingUser(user);
    setEditedUser({
        username: user.username,
        email: user.email,
        gender: user.gender,
        role: user.roleNames,
        score: user.score
    });
};

const handleSaveChanges = async () => {
    try {
        console.log("editingUser avant mise à jour:", editingUser);
        console.log("editedUser:", editedUser);
        
        // Vérifier si editingUser est null ou undefined
        if (!editingUser) {
            console.error("editingUser est null ou undefined");
            return;
        }

        // Création d'un objet avec les valeurs modifiées
        const updatedUser = {
            ...editingUser,
            ...editedUser
        };

        // Appel API pour mettre à jour l'utilisateur
        const response = await updateUser(updatedUser);

        // Mettre à jour les données utilisateur
        setUserData(response.data);
        setEditingUser(null); // Fermer le mode édition
    } catch (error) {
        console.error('Erreur lors de la mise à jour:', error);
    }
};

const handleInputChange = (field, value) => {
    setEditedUser(prev => ({
        ...prev,
        [field]: values
    }));
};


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
            {user.username} - {user.email} - Score: {user.score} - Roles: {user.roleNames.join(', ')}
            <button onClick={editUser(user)}>Edit</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default AdminUserSearch;
