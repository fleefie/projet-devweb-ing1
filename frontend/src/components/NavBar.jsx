import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import './NavBar.css';

const NavBar = () => {
  const navigate = useNavigate();
  const token = localStorage.getItem('jwtToken');
  let roles = [];
  if (token) {
    try {
      const payload = jwtDecode(token);
      roles = payload.roles || [];
    } catch (e) {
      e == e;
    }
  }

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    navigate('/login');
  };

  return (
    <nav>
      <div className="nav-left">
        <Link to="/">Home</Link>
      </div>
      <ul className="nav-right">
        {token ? (
          <>
            <li><Link to="/users">Users</Link></li>
            <li><button onClick={handleLogout}>Logout</button></li>
            {roles.includes('ADMIN') && (
              <li><Link to="/admin">Admin Panel</Link></li>
            )}
          </>
        ) : (
          <>
            <li><Link to="/login">Login</Link></li>
            <li><Link to="/register">Register</Link></li>
          </>
        )}
      </ul>
    </nav>
  );
};

export default NavBar;
