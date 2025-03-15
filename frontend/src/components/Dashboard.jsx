import React from 'react';

const Dashboard = () => {
  return (
    <div className="card">
      <h1>Dashboard</h1>
      <p>You are successfully logged in!</p>
      <p>This is a protected route that can only be accessed when authenticated.</p>
    </div>
  );
};

export default Dashboard;
