import React from 'react';
import axios from 'axios';
//import { getProfile } from '../api/apiClient';

const Profile = () => {
    const getProfile = async () => {
        const token = localStorage.getItem('token');  // Récupère le token depuis le stockage
        return await axios.post('/api/users/me', {}, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
      };
    const [userData, setUserData] = React.useState(null);

    React.useEffect(() => {
        const fetchProfile = async () => {
            try {
                const response = await getProfile();
                setUserData(response.data);
                console.log("Mon Profile: ", response.data);
            } catch (error) {
                console.error("Erreur lors de la récupération du profil :", error);
            }
        };
        fetchProfile();
    }, []);

    return (
        <div>
            <h2>Mon profil</h2>
            {userData ? (
                Object.entries(userData).map(([key, value]) => (
                    <div key={key} className="profile-item">
                        <strong>{key}:</strong> <span>{value}</span>
                    </div>
                ))
            ) : (
                <p>Chargement...</p>
            )}
        </div>
    );
};

export default Profile;
