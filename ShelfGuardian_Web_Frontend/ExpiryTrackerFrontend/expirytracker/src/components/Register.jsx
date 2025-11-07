import React, { useState } from "react";
import { useNavigate, Link } from 'react-router-dom';
import { register } from "../services/authService";
import './register.css';

export default function Register() {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirm, setConfirm] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        if (password !== confirm) {
            setError("Passwords do not match");
            return;
        }
        setLoading(true);
        try {
            await register({ username, email, password });
            // navigate back to login
            navigate('/login');
        } catch (err) {
            setError(err.message || "Registration failed");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="login-container">
          <div className="login-background">
            <div className="login-gradient-orb login-orb-1"></div>
            <div className="login-gradient-orb login-orb-2"></div>
            <div className="login-gradient-orb login-orb-3"></div>
          </div>

          <div className="login-content">
            <div className="login-card">
              <h2 style={{ marginBottom: 12 }}>Create your account</h2>
              {error && <div style={{ color: "red", marginBottom: 12 }}>{error}</div>}
              <form onSubmit={handleSubmit} className="login-form">
                <div className="form-group">
                  <label className="form-label">Username</label>
                  <input className="form-input" value={username} onChange={e => setUsername(e.target.value)} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Email</label>
                  <input className="form-input" type="email" value={email} onChange={e => setEmail(e.target.value)} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Password</label>
                  <input className="form-input" type="password" value={password} onChange={e => setPassword(e.target.value)} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Confirm Password</label>
                  <input className="form-input" type="password" value={confirm} onChange={e => setConfirm(e.target.value)} required />
                </div>
                <button className="login-button" type="submit" disabled={loading}>{loading ? "Signing up..." : "Sign up"}</button>
              </form>

              <p className="signup-link" style={{ marginTop: 12 }}>
                Already have an account? <Link to="/login">Log in</Link>
              </p>
            </div>
          </div>
        </div>
    );
}