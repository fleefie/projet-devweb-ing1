import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { searchUsers } from '../api/apiClient';
import { updateUser } from '../api/apiClient';
import useCurrentUser from '../api/hookUseCurrentUser';

const UserProfile = () => {
    const { username } = useParams();
    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [message, setMessage] = useState('');
    const currentUser = useCurrentUser();

    const [editingUser, setEditingUser] = useState(null);
    const [editedUser, setEditedUser] = useState({});
    const isAdmin = currentUser?.roles?.some(role => role.name === 'ADMIN'); // jsp si ca marche

    const reportButton = () => {
        setMessage('Utilisateur signalé.');
        setTimeout(() => {
            setMessage('');
        }, 3000);
    };

    const handleActionButton = (user) => {
        // Initialiser l'état d'édition avec les valeurs actuelles de l'utilisateur
        setEditingUser(user);
        setEditedUser({
            username: user.username,
            email: user.email,
            firstName: user.firstName,
            name: user.name,
            birthdate: user.birthdate,
            gender: user.gender
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
            [field]: value
        }));
    };

    useEffect(() => {
        const fetchUserData = async () => {
            console.log('Fetching user data for username:', username);
            try {
                const response = await searchUsers({ username });
                console.log('API response:', response);

                if (response.data && response.data.length > 0) {
                    console.log('User data found:', response.data);
                    setUserData(response.data[0]);
                } else {
                    console.log('No user data found.');
                    setUserData(null);
                }
            } catch (error) {
                console.error('Error fetching user data:', error);
                setError('Erreur lors de la récupération des données utilisateur.');
            } finally {
                console.log('Finished fetching user data.');
                setLoading(false);
            }
        };
        fetchUserData();
    }, [username]);

    if (loading) {
        console.log('Loading...');
        return <p>Chargement...</p>;
    }
    if (error) {
        console.log('Error:', error);
        return <p>{error}</p>;
    }
    if (!userData) {
        console.log('No user data to display.');
        return <p>Aucun utilisateur trouvé.</p>;
    }

    console.log('Rendering user profile:', userData);
    return (
        <div style={{ backgroundColor: 'beige', borderRadius: 4 }}>
            <h2>{userData.username}</h2>
            {editingUser ? (
                <div className="edit-form">
                    <h3>Edit {userData.username} profile</h3>
                    {Object.keys(editedUser).map((field) => ( //Affiche dynamiquement chaque attribut
                        <div key={field}>
                            <label>{field.charAt(0).toUpperCase() + field.slice(1)}:</label>
                            <input
                                value={editedUser[field]}
                                onChange={(e) => handleInputChange(field, e.target.value)}
                            />
                        </div>
                    ))}
                    <div className="edit-actions">
                        <button type="button" onClick={handleSaveChanges}>Enregistrer</button>
                        <button type="button" onClick={() => setEditingUser(null)}>Annuler</button>
                    </div>
                </div>
            ) : (
                <>
                    <p>Name: {userData.name}</p>
                    <p>First name: {userData.firstName}</p>
                    <p>Score: {userData.score}</p>
                    <p>Birthdate: {userData.birthdate}</p>
                    <p>Gender: {userData.gender}</p>
                    <button onClick={reportButton}>Report</button>
                    {isAdmin && (
                        <>
                            <button onClick={() => handleActionButton(userData)}>Edit profile</button>
                        </>
                    )}
                </>
            )}
        </div>
    );
};

export default UserProfile;