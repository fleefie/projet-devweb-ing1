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
import DeviceSearch from './pages/DeviceSearch';
import VisitorHub from './pages/VisitorHub';
import ControlPanelComplexUser from './pages/ControlPanelComplexUser';
import AdminDevicesControl from './pages/AdminDevicesControl';

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
      <Route path="/controlpanelcomplexuser" element={
        <ProtectedRoute>
          <ControlPanelComplexUser/>
        </ProtectedRoute>
      }/>
      <Route path="/admin" element={
        <ProtectedRoute roles={['ADMIN']}>
          <AdminUserSearch />
          <AdminVerify />
          <AdminDevicesControl/>
        </ProtectedRoute>
      } />
      <Route path="/admin/verify" element={
        <ProtectedRoute roles={['ADMIN']}>
          <AdminVerify />
        </ProtectedRoute>
      } />
      <Route path="/devices" element={
          <DeviceSearch />
      } />
    </Routes>
  </Router>
);

export default App;

