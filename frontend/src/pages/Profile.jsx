import React from 'react';
import { me } from '../api/apiClient';

const Profile = () => {
    const [userData, setUserData] = React.useState(null);
    const [error, setError] = React.useState(null);

    React.useEffect(() => {
        const fetchProfile = async () => {
            try {
                const response = await me(); // Appelle la méthode me() avec le token
                setUserData(response.data); // Met à jour les données utilisateur
                console.log("Mon Profil: ", response.data);
            } catch (error) {
                setError("Erreur lors de la récupération du profil.");
                console.error("Erreur lors de la récupération du profil :", error);
            }
        };
        fetchProfile();
    }, []);

    if (error) return <p>{error}</p>;
    if (!userData) return <p>Chargement...</p>;

    return (
        <div>
            <h2>Mon profil</h2>
            {/* Affichage dynamique
            {Object.entries(userData)
            .filter(([key]) => key !== 'password' && key !== 'id' && key!='roles')
            .map(([key, value]) => (
                
                <div key={key} className="profile-item">
                    <strong>{key}:</strong>{" "}
                    <span>
                        {typeof value === 'object' && value !== null
                            ? JSON.stringify(value)
                            : value}
                    </span>
                </div>
            ))}
                */}
            <div>
                <ul>
                    <li><strong>Unsername:</strong>{userData.username}</li>
                    <li><strong>Mail:</strong>{userData.email}</li>
                    <li><strong>First Name:</strong>{userData.firstName}</li>
                    <li><strong>Name:</strong>{userData.name}</li>
                    <li><strong>Birthdate:</strong>{userData.birthdate}</li>
                    <li><strong>Gender:</strong>{userData.gender}</li>
                    <li><strong>Role:</strong>{userData.roles[0].name}</li>
                </ul>
            </div>
            <button>Modifier</button>
        </div>
    );
};

export default Profile;
