import { useEffect, useState } from 'react';
import { me } from './apiClient';

const useCurrentUser = () => {
  const [currentUser, setCurrentUser] = useState(null);

  useEffect(() => {
    const fetch = async () => {
      try {
        const res = await me(); // Pas besoin de passer le token si géré par l'axiosInstance
        setCurrentUser(res.data);
      } catch (err) {
        console.error('Erreur lors de la récupération de l’utilisateur connecté :', err);
      }
    };
    fetch();
  }, []);

  return currentUser;
};

export default useCurrentUser;
