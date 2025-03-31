import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = ({ isAuthenticated, onLogout }) => {
  const navStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '1rem 2rem',
    backgroundColor: '#ffffff',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
  };
  
  const logoStyle = {
    fontSize: '1.5rem',
    fontWeight: 'bold',
    color: '#4a6cf7',
    textDecoration: 'none'
  };
  
  const navLinksStyle = {
    display: 'flex',
    gap: '1rem'
  };
  
  const linkStyle = {
    color: '#333',
    textDecoration: 'none',
    padding: '0.5rem',
    borderRadius: '4px'
  };
  
  const buttonStyle = {
    backgroundColor: 'transparent',
    border: 'none',
    color: '#333',
    cursor: 'pointer',
    fontSize: '1rem',
    padding: '0.5rem'
  };

  return (
    <nav style={navStyle}>
      <Link to="/" style={logoStyle}>Auth SPA</Link>
      <div style={navLinksStyle}>
        {isAuthenticated ? (
          <>
            <Link to="/dashboard" style={linkStyle}>Dashboard</Link>
            <Link to="/controlpanel" style={linkStyle}>API</Link>
            <button onClick={onLogout} style={buttonStyle}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/login" style={linkStyle}>Login</Link>
            <Link to="/register" style={linkStyle}>Register</Link>
          </>
        )}
      </div>
    </nav>
  );
  };
  
  export default Navbar;
  
