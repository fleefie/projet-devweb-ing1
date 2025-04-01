import React from 'react';
import { Navigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

const getTokenPayload = () => {
  const token = localStorage.getItem('jwtToken');
  if (!token) return null;
  try {
    return jwtDecode(token);
  } catch {
    return null;
  }
};

const ProtectedRoute = ({ children, roles = [] }) => {
  const payload = getTokenPayload();
  if (!payload) {
    return <Navigate to="/visitorhub" replace />;
  }

  if (roles.length && !roles.some(role => payload.roles?.includes(role))) {
    return <Navigate to="/unauthorized" replace />;
  }
  return children;
};

export default ProtectedRoute;

