import React from 'react';
import ApiForm from '../components/ApiForm';
import { acceptUser } from '../api/apiClient';

const AdminVerify = () => {
  const handleVerify = async (data) => {
    await acceptUser({ username: data.username });
  };

  const fields = [
    { name: 'username', label: 'Username to verify' },
  ];

  return <ApiForm fields={fields} onSubmit={handleVerify} submitLabel="Verify User" />;
};

export default AdminVerify;
