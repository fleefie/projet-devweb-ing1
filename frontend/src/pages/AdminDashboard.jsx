import React from 'react';
import AdminVerify from './AdminVerify';
import AdminUserSearch from './AdminUserSearch';
import AdminDevicesControl from './AdminDevicesControl';
import './AdminDashboard.css';

const AdminDashboard = () => {
  return (
    <div className="admin-dashboard">
      <div className="user-management">
        <h2>User Management</h2>
        <AdminVerify />
        <AdminUserSearch />
      </div>

      <div className="device-management">
        <h2>Device Management</h2>
        <AdminDevicesControl />
      </div>
    </div>
  );
};

export default AdminDashboard;