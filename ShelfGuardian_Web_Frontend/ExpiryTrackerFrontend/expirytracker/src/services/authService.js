import axios from "axios";

const BASE_URL = "http://127.0.0.1:8000";

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// ✅ Attach token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// ✅ Login
export async function login(username, password) {
  console.log(`[Auth Login] Starting login for user: ${username}`);
  const body = new URLSearchParams();
  body.append("username", username);
  body.append("password", password);

  console.log(`[Auth Login] Sending request to /api/auth/login`);
  const resp = await api.post("/api/auth/login", body.toString(), {
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
  });

  console.log(`[Auth Login] Response status: ${resp.status}`);
  console.log(`[Auth Login] Response data:`, resp.data);

  const token = resp.data?.access_token;
  if (!token) {
    console.error(`[Auth Login] No access token in response`);
    throw new Error("No access token returned");
  }

  console.log(`[Auth Login] Storing token and user_id`);
  localStorage.setItem("token", resp.data.access_token);
  localStorage.setItem("user_id", resp.data.user_id);
  localStorage.setItem("username", username); // Store username for welcome message

  console.log(`[Auth Login] Login successful`);
  return resp.data;
}

// ✅ Register
export async function register(payload) {
  const resp = await api.post("/api/auth/register", payload);
  return resp.data;
}

// ✅ Logout
export function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("user_id");
  localStorage.removeItem("username");
}

// ✅ Helper functions (fixes your error)
export function getToken() {
  return localStorage.getItem("token");
}

export function getUserId() {
  return localStorage.getItem("user_id");
}

// ✅ Get current user info from API
export async function getCurrentUser() {
  try {
    const resp = await api.get("/api/auth/me");
    return resp.data;
  } catch (error) {
    console.error("Failed to get current user:", error);
    return null;
  }
}

export function getUsername() {
  return localStorage.getItem("username");
}
