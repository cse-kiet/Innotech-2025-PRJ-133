import axios from "axios";

const BASE_URL = "http://127.0.0.1:8000";

// Configure axios defaults for auth service
axios.defaults.withCredentials = true;
axios.defaults.headers.common = {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
    "Access-Control-Allow-Headers": "Origin, Content-Type, Accept, Authorization"
};

/**
 * Login using application/x-www-form-urlencoded (matches your example).
 * Throws Error with server message on failure.
 */
export async function login(username, password) {
    try {
        console.log(`[Auth Login] Attempting login for user: ${username}`);
        const body = new URLSearchParams();
        body.append("username", username);
        body.append("password", password);

        const resp = await axios.post(`${BASE_URL}/api/auth/login`, body.toString(), {
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
        });

        if (!(resp.status === 200 || resp.status === 201)) {
            throw new Error(resp.data?.detail || "Login failed");
        }

        // adjust field names if backend returns different keys
        const access = resp.data?.access || resp.data?.access_token;
        const refresh = resp.data?.refresh || resp.data?.refresh_token;

        if (!access) throw new Error(resp.data?.detail || "No access token returned");

        localStorage.setItem("accessToken", access);
        if (refresh) localStorage.setItem("refreshToken", refresh);
        console.log(`[Auth Login] Success - tokens stored`);

        return resp.data;
    } catch (err) {
        console.error(`[Auth Login] Failed:`, err.response?.data || err.message);
        const msg = err.response?.data?.detail || err.response?.data?.error || err.response?.data || err.message || "Login failed";
        throw new Error(typeof msg === "string" ? msg : JSON.stringify(msg));
    }
}

/**
 * Refresh access token (expects JSON body; change if backend requires different)
 */
export async function refreshAccess() {
    const refresh = localStorage.getItem("refreshToken");
    if (!refresh) throw new Error("No refresh token available");
    try {
        const resp = await axios.post(`${BASE_URL}/api/auth/refresh`, { refresh }, {
            headers: { "Content-Type": "application/json" },
        });
        const newAccess = resp.data?.access || resp.data?.access_token;
        if (!newAccess) throw new Error(resp.data?.detail || "Refresh failed");
        localStorage.setItem("accessToken", newAccess);
        return newAccess;
    } catch (err) {
        const msg = err.response?.data?.detail || err.response?.data?.error || err.message || "Refresh failed";
        throw new Error(msg);
    }
}

export function logout() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
}

/**
 * Register a new user against /api/auth/register
 * Payload is an object with username, email, password (adjust keys if backend expects different)
 * Throws Error on failure.
 */
export async function register(payload) {
    try {
        const resp = await axios.post(`${BASE_URL}/api/auth/register`, payload, {
            headers: { "Content-Type": "application/json" },
        });
        if (!(resp.status === 200 || resp.status === 201)) {
            throw new Error(resp.data?.detail || "Register failed");
        }
        return resp.data;
    } catch (err) {
        const msg = err.response?.data?.detail || err.response?.data?.error || err.message || "Register failed";
        throw new Error(typeof msg === "string" ? msg : JSON.stringify(msg));
    }
}