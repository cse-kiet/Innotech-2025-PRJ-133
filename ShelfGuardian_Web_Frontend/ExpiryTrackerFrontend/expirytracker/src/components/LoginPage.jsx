import React, { useState } from 'react';
import { Calendar } from 'lucide-react';
import './landing.css';
import { login } from "../services/authService";
import { Link, useNavigate } from 'react-router-dom';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

 const handleSubmit = async (e) => {
  e.preventDefault();
  setError('');
  setIsLoading(true);

  try {
    const res = await login(email, password);  // ✅ WAIT for login

    if (res && res.access_token) {
      localStorage.setItem("token", res.access_token);
    }

    setIsLoading(false);
    navigate('/app');  // ✅ Navigate AFTER token saved
  } catch (err) {
    setIsLoading(false);
    setError(err.message || 'Login failed');
  }
};

  return (
    <div className="login-container">
      <div className="login-background">
        <div className="login-gradient-orb login-orb-1"></div>
        <div className="login-gradient-orb login-orb-2"></div>
        <div className="login-gradient-orb login-orb-3"></div>
      </div>

      <div className="login-content">
        <div className="login-card">
          <div className="login-header">
            <div className="login-icon-wrapper">
              <Calendar className="login-icon" />
            </div>
            <h1 className="login-title">ShelfGuardian</h1>
            <p className="login-subtitle">Sign in to continue</p>
          </div>

          <form onSubmit={handleSubmit} className="login-form">
            {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}

            <div className="form-group">
              <label htmlFor="email" className="form-label">Email</label>
              <input
                type="email"
                id="email"
                className="form-input"
                placeholder="Enter your email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="password" className="form-label">Password</label>
              <input
                type="password"
                id="password"
                className="form-input"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <div className="form-options">
              <label className="checkbox-wrapper">
                <input type="checkbox" className="checkbox-input" />
                <span className="checkbox-label">Remember me</span>
              </label>
              <a href="#" className="forgot-link">Forgot password?</a>
            </div>

            <button
              type="submit"
              className={`login-button ${isLoading ? 'loading' : ''}`}
              disabled={isLoading}
            >
              {isLoading ? (
                <span className="button-loader">
                  <span></span><span></span><span></span>
                </span>
              ) : (
                'Sign In'
              )}
            </button>

            <div className="login-divider"><span>or</span></div>

            <div className="social-login">
              <button type="button" className="social-button">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
                  <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                  <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
                  <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
                </svg>
                Continue with Google
              </button>
            </div>

            <p className="signup-link">
              Don't have an account? <Link to="/register">Sign up</Link>
            </p>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
