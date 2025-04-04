import axiosInstance from './axiosInstance';

export const login = (credentials) => axiosInstance.post('/auth/login', credentials);
export const register = (data) => axiosInstance.post('/auth/register', data);
export const searchUsers = (criteria) => axiosInstance.post('/users/search-users', criteria);
export const searchUsersAdmin = (criteria) => axiosInstance.post('/users/search-users-admin', criteria);
export const acceptUser = (data) => axiosInstance.post('/users/accept-user', data);
export const updateUser = (data) => axiosInstance.post('/users.updateUser',data); //N'existe pas pour le moment
export const searchDevices = (criteria) => {
    return axiosInstance.post('/devices/search', criteria);
  };
export const searchDevicesPublic = (criteria) => {return axiosInstance.post('/devices/public-search')};
  // Création d'un nouveau device
export const createDevice = (deviceDto) => axiosInstance.post('/devices/create', deviceDto);
export const updateDevice = (deviceDto) => axiosInstance.post('/devices/update', deviceDto);
// Récupération de son propre profil
export const me = () => {
  console.log("Token in localStorage:", localStorage.getItem('jwtToken'));
  return axiosInstance.post('/auth/me');
};
//export const searchInfos = (data) => axiosInstance.post(PATH_TO_OBJECTS')
