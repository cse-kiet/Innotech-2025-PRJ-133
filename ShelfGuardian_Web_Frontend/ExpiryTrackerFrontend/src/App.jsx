import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Register from "./components/Register";
import LoginPage from "./components/LoginPage";
import MainApp from "./components/MainApp";
import LandingPage from "./components/LandingPage";
import PrivateRoute from "./components/PrivateRoute";
import Dashboard from "./components/Dashboard";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<Register />} />

        {/* ✅ Dashboard Route */}
        <Route
          path="/dashboard"
          element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          }
        />

        {/* ✅ Main App */}
        <Route
          path="/app"
          element={
            <PrivateRoute>
              <MainApp />
            </PrivateRoute>
          }
        />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
