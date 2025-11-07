import axios from "axios";
import { refreshAccess, logout } from "../services/authService";

const BASE_URL = "http://127.0.0.1:8000";

const api = axios.create({
    baseURL: BASE_URL,
    headers: { 
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
        "Access-Control-Allow-Headers": "Origin, Content-Type, Accept, Authorization"
    },
    withCredentials: true
});

let isRefreshing = false;
let pendingRequests = [];

function onRefreshed(token) {
    pendingRequests.forEach(cb => cb(token));
    pendingRequests = [];
}

api.interceptors.request.use(config => {
    config.headers = config.headers || {};
    const token = localStorage.getItem("accessToken");
    if (token) config.headers.Authorization = `Bearer ${token}`;
    console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, { headers: config.headers, data: config.data });
     return config;
});

api.interceptors.response.use(
    response => {
        console.log(`[API Response] ${response.status} ${response.config.method?.toUpperCase()} ${response.config.url}`, response.data);
        return response;
    },
    async error => {
        console.error(`[API Error] ${error.response?.status || 'Network'} ${error.config?.method?.toUpperCase()} ${error.config?.url}`, error.response?.data || error.message);
        const originalRequest = error?.config;
        if (!originalRequest) return Promise.reject(error); // network/CORS error
         if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            const refreshToken = localStorage.getItem("refreshToken");
            if (!refreshToken) {
                logout();
                return Promise.reject(error);
            }
            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    pendingRequests.push(token => {
                        originalRequest.headers.Authorization = `Bearer ${token}`;
                        resolve(api(originalRequest));
                    });
                });
            }
            isRefreshing = true;
            try {
                // use authService.refreshAccess which throws on failure
                const newAccess = await refreshAccess();
                api.defaults.headers.common = api.defaults.headers.common || {};
                api.defaults.headers.common.Authorization = `Bearer ${newAccess}`;
                onRefreshed(newAccess);
                return api(originalRequest);
            } catch (refreshErr) {
                logout();
                return Promise.reject(refreshErr);
            } finally {
                isRefreshing = false;
            }
        }
        return Promise.reject(error);
    }
);

export default api;