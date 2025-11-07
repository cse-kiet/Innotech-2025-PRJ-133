import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LandingPage from './components/LandingPage';
import LoginPage from './components/LoginPage';
import MainApp from './components/MainApp';
import Register from './components/Register';
import DashBoard from './components/DashBoard';

const ProtectedRoute = ({ element }) => {
  const isAuthenticated = localStorage.getItem('token');
  return isAuthenticated ? element : <Navigate to="/login" />;
};

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<Register />} />

        {/* ✅ Dashboard Route */}
        <Route path="/dashboard" element={<ProtectedRoute element={<DashBoard />} />} />

        {/* ✅ Main App */}
        <Route path="/app" element={<ProtectedRoute element={<MainApp />} />} />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
