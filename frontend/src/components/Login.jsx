import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { login } from '../services/api';

const Login = ({ onLogin }) => {
  const [formData, setFormData] = useState({
    usernameOrEmail: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await login(formData.usernameOrEmail, formData.password);
      localStorage.setItem('token', response.data.token);
      onLogin(response.data.token);
      console.log(response.data.token);
      console.log(localStorage.getItem('token'));
    } catch (err) {
      setError(
        err.response?.data?.message || 
        'Login failed. Please check your credentials and try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <h1>Login</h1>
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="usernameOrEmail"
          placeholder="Username or Email"
          value={formData.usernameOrEmail}
          onChange={handleChange}
          required
        />
        
        <input
          type="password"
          name="password"
          placeholder="Password"
          value={formData.password}
          onChange={handleChange}
          required
        />
        
        <button type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
      
      <div className="form-footer">
        <p>Don't have an account? <Link to="/register">Register</Link></p>
      </div>
    </div>
  );
};

export default Login;
