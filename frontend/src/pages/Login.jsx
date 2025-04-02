import React from 'react';
import { useNavigate } from 'react-router-dom';
import ApiForm from '../components/ApiForm';
import { login } from '../api/apiClient';
import './Login.css';

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

  return (
    <div className="login-container">
      <ApiForm fields={fields} onSubmit={handleLogin} submitLabel="Login" />
    </div>
  );
};

export default Login;
