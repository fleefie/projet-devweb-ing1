import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import searchUsers from '../services/api';

const VisitorHub = () => {
    const setResults = useState([]);
    const [criteria, setCriteria] = useState('');

    const handleSearch = async (e) => {
        e.preventDefault();
        const res = await searchUsers({ username: criteria });
        setResults(res.data);
    };

    return (
        <div>
            <p>Salut jeune visiteur, tu n'es pas connecté</p>
            <p>You still can search local informations about the city!</p>
            <div>
                <form onSubmit={handleSearch}>
                    <input value={criteria} onChange={(e) => setCriteria(e.target.value)} placeholder="Search local informations" />
                    <button type="submit">Search</button>
                </form>
            </div>

            <ul>
               <p>Ici les résultats quand on aura fait les objets</p>
            </ul>
        </div>
    );
};

export default VisitorHub;
