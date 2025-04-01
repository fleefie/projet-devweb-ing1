import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import NavBar from './components/NavBar';
import Login from './pages/Login';
import Register from './pages/Register';
import AccountPending from './pages/AccountPending';
import UserSearch from './pages/UserSearch';
import AdminUserSearch from './pages/AdminUserSearch';
import AdminVerify from './pages/AdminVerify';
import ProtectedRoute from './components/ProtectedRoute';
import Home from './pages/Home';
import VisitorHub from './pages/VisitorHub';

const App = () => (
  <Router>
    <NavBar />
    <Routes>
      <Route path="/" element={
          <ProtectedRoute>
            <Home />
          </ProtectedRoute>
      } />
      <Route path="visitorhub" element={<VisitorHub/>}/>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/account-pending" element={<AccountPending />} />
      <Route path="/users" element={
        <ProtectedRoute>
          <UserSearch />
        </ProtectedRoute>
      } />
      <Route path="/admin" element={
        <ProtectedRoute roles={['ADMIN']}>
          <AdminUserSearch />
          <AdminVerify />
        </ProtectedRoute>
      } />
      <Route path="/admin/verify" element={
        <ProtectedRoute roles={['ADMIN']}>
          <AdminVerify />
        </ProtectedRoute>
      } />
    </Routes>
  </Router>
);

export default App;

