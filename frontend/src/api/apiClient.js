import axiosInstance from './axiosInstance';

export const login = (credentials) => axiosInstance.post('/auth/login', credentials);
export const register = (data) => axiosInstance.post('/auth/register', data);
export const searchUsers = (criteria) => axiosInstance.post('/users/search-users', criteria);
export const searchUsersAdmin = (criteria) => axiosInstance.post('/users/search-users-admin', criteria);
export const acceptUser = (data) => axiosInstance.post('/users/accept-user', data);
export const searchDevices = (criteria) => axiosInstance.post('/devices/search', criteria);
//export const searchInfos = (data) => axiosInstance.post(PATH_TO_OBJECTS')
