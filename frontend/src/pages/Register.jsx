import React from 'react';
import { useNavigate } from 'react-router-dom';
import ApiForm from '../components/ApiForm';
import { register } from '../api/apiClient';

const Register = () => {
  const navigate = useNavigate();

  const handleRegister = async (data) => {
    const response = await register(data);
    if (response && response.data && response.data.message) {
      navigate('/account-pending');
    }
    return response;
  };

  const fields = [
    { name: 'username', label: 'Username' },
    { name: 'email', label: 'Email' },
    { name: 'password', label: 'Password', type: 'password' },
    { name: 'passwordConfirm', label: 'Confirm Password', type: 'password' },
    { name: 'name', label: 'Name' },
  ];

  return <ApiForm fields={fields} onSubmit={handleRegister} submitLabel="Register" />;
};

export default Register;

