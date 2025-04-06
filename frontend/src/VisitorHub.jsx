import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { searchDevices } from '../api/apiClient';
import './VisitorHub.css';

const VisitorHub = () => {
    const [results, setResults] = useState([]);
    const [criteria, setCriteria] = useState('');

    // Annonces fictives pour Ygrec
    const fakeAnnouncements = [
        {
            id: 1,
            title: "Festival des Lumières de Ygrec",
            body: "Venez découvrir la magie des lumières qui illuminent toute la ville de Ygrec ce week-end !",
            tags: ["festival", "lumières", "Ygrec"]
        },
        {
            id: 2,
            title: "Marché Artisanal de Printemps",
            body: "Artisans locaux et producteurs se retrouvent au centre-ville de Ygrec pour une journée festive.",
            tags: ["marché", "printemps", "Ygrec"]
        },
        {
            id: 3,
            title: "Conférence sur l'Histoire de Ygrec",
            body: "Rejoignez-nous à la médiathèque pour une conférence captivante sur le passé de notre belle ville.",
            tags: ["conférence", "histoire", "Ygrec"]
        },
        {
            id: 4,
            title: "Concert Gratuit au Parc Central",
            body: "Le groupe Les Étoiles de Ygrec vous offre un concert exceptionnel en plein air ce samedi soir !",
            tags: ["concert", "musique", "Ygrec"]
        }
    ];

    const handleSearch = async (e) => {
        e.preventDefault();
        try {
            const res = await searchDevices({ query: criteria });
            console.log("Response:", res);
            setResults(res.data.devices);
        } catch (error) {
            console.error("Error searching devices:", error);
            if (error.response) {
                console.error("Response data:", error.response.data);
                console.error("Response status:", error.response.status);
            }
        }
    };

    const PropertyRenderer = ({ propName, propValue }) => {
        if (typeof propValue === 'object' && propValue !== null) {
            return (
                <div>
                    <span>{propName}:</span>
                    <ul>
                        {Object.entries(propValue).map(([key, val], editedId) => (
                            <li key={editedId}>
                                <PropertyRenderer propName={key} propValue={val} />
                            </li>
                        ))}
                    </ul>
                </div>
            );
        }
        return (
            <span>
                {propName}: {propValue}
            </span>
        );
    };

    return (
        <div>
            <p style={{ textAlign: 'center', paddingTop: 60, fontSize: '24px', fontWeight: 'bold', color:'white'}}>Welcome to the Ygrec website, you are currently in guest mode.</p>
            <p style={{ textAlign: 'center', paddingTop: 40 , fontSize: '24px', fontWeight: 'bold' , color :'white'}}>You still can search local informations about the city !</p>
            <div>
                <form onSubmit={handleSearch}>
                    <input
                        value={criteria}
                        onChange={(e) => setCriteria(e.target.value)}
                        placeholder="Search devices"
                    />
                    <button type="submit" className='Search'>Search</button>
                </form>
            </div>

            <div className="announcements">
                <h2>Actualités à Ygrec</h2>
                <div className="announcement-list">
                    {fakeAnnouncements.map(a => (
                        <div key={a.id} className="announcement-card">
                            <h3>{a.title}</h3>
                            <p>{a.body}</p>
                            <small>Tags: {a.tags.join(', ')}</small>
                        </div>
                    ))}
                </div>
            </div>

            <div className="footer">
                <footer className="site-footer">
                    <div className="footer-content">
                        <p>&copy; Ygrec. All rights reserved.</p>
                        <ul className="footer-links">
                            <li><a href="#">Home</a></li>
                            <li><a href="#">About us</a></li>
                            <li><a href="#">Contact</a></li>
                            <li><a href="#">Legal mentions</a></li>
                            <li><a href="#">ygrec@gmail.com</a></li>
                        </ul>
                    </div>
                </footer>
            </div>

            <ul>
                {results.map((device) => (
                    <li key={device.id}>
                        <h3>{device.name}</h3>
                        <div className="device-properties">
                            {device.properties && Object.entries(device.properties).map(
                                ([propName, propValue]) => (
                                    <div key={propName}>
                                        <PropertyRenderer
                                            propName={propName}
                                            propValue={propValue}
                                        />
                                    </div>
                                )
                            )}
                        </div>
                        <button onClick={() => alert("You have to be logged in !")}>Modifier</button>
                    </li>
                ))}
            </ul>
        </div>
    );

};

export default VisitorHub;
