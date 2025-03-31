import React from 'react';
import { useNavigate } from 'react-router-dom';
import ApiForm from '../components/ApiForm';
import { login } from '../api/apiClient';

const Login = () => {
  const navigate = useNavigate();
  const handleLogin = async (data) => {
    const res = await login({
      usernameOrEmail: data.usernameOrEmail,
      password: data.password,
    });
    localStorage.setItem('jwtToken', res.data.accessToken);
    navigate('/');
  };

  const fields = [
    { name: 'usernameOrEmail', label: 'Username or Email' },
    { name: 'password', label: 'Password', type: 'password' },
  ];

  return <ApiForm fields={fields} onSubmit={handleLogin} submitLabel="Login" />;
};

export default Login;
