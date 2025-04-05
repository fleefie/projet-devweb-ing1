import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { searchUsers } from '../api/apiClient';
import useCurrentUser from '../api/hookUseCurrentUser';

const UserProfile = () => {
    const { username } = useParams();
    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [message, setMessage] = useState('');
    const currentUser = useCurrentUser();
    const isAdmin = currentUser?.roles?.some(roles => roles.name==='ADMIN');


    const reportButton = () => {
        setMessage('Utilisateur signalé.');
        setTimeout(() => {
            setMessage('');
        }, 3000);
    };

    useEffect(() => {
        const fetchUserData = async () => {
            console.log('Fetching user data for username:', username);
            try {
                const response = await searchUsers({ username });
                console.log('API response:', response);

                if (response.data && response.data.length > 0) {
                    console.log('User data found:', response.data);
                    setUserData(response.data[0]); // Suppose que l'API retourne un tableau
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
        <div style={{backgroundColor: 'beige', borderRadius: 4}}>
            <h2>{userData.username}</h2>
            
            <p>Name : {userData.name}</p>
            <p>First name: {userData.firstName}</p>
            <p>Score : {userData.points}</p>
            <p>Birthdate: {userData.birthdate}</p>
            <p>Gender: {userData.gender}</p>
            <button onClick={() => reportButton()}>Report</button>
            {isAdmin &&(
                <>
                <button onClick={(console.log("admin clicked"))}>Edit profile</button>
                </>
            )}
            
        </div>
    );
};

export default UserProfile;
