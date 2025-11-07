import axios from "axios";

const BASE_URL = "http://127.0.0.1:8000";

(async () => {
  let token = null;

  // First try to login
  try {
    console.log("Attempting login...");
    const body = new URLSearchParams();
    body.append("username", "testuser");
    body.append("password", "testpass");

    const resp = await axios.post(`${BASE_URL}/api/auth/login`, body.toString(), {
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
    });

    token = resp.data?.access_token;
    if (token) {
      console.log("Login successful, token received");
    } else {
      console.log("Login failed: No token returned");
    }
  } catch (err) {
    console.error("Login failed:", err.response?.data || err.message || err);
  }

  // Test GET user products
  try {
    console.log("Testing GET user products...");
    const r = await axios.get(`${BASE_URL}/api/products/user/1`, {
      headers: token ? { "Authorization": `Bearer ${token}` } : {}
    });
    console.log("GET user products response:", r.data);
  } catch (err) {
    console.error("GET user products failed:", err.response?.data || err.message || err);
  }

  // Test POST product
  try {
    console.log("Testing POST product...");
    const postData = {
      name: "Test Product",
      expiry_date: "2025-12-31",
      category: "Test",
      user_id: 1
    };
    const r = await axios.post(`${BASE_URL}/api/products/`, postData, {
      headers: {
        "Content-Type": "application/json",
        ...(token ? { "Authorization": `Bearer ${token}` } : {})
      }
    });
    console.log("POST response:", r.data);
  } catch (err) {
    console.error("POST failed:", err.response?.data || err.message || err);
  }

  // Test PUT product (update existing)
  try {
    console.log("Testing PUT product...");
    const putData = {
      name: "Updated Test Product",
      expiry_date: "2025-12-31",
      category: "Test",
      user_id: 1
    };
    const r = await axios.put(`${BASE_URL}/api/products/1`, putData, {
      headers: {
        "Content-Type": "application/json",
        ...(token ? { "Authorization": `Bearer ${token}` } : {})
      }
    });
    console.log("PUT response:", r.data);
  } catch (err) {
    console.error("PUT failed:", err.response?.data || err.message || err);
  }

  // Test DELETE product
  try {
    console.log("Testing DELETE product...");
    const r = await axios.delete(`${BASE_URL}/api/products/1`, {
      headers: token ? { "Authorization": `Bearer ${token}` } : {}
    });
    console.log("DELETE response:", r.status);
  } catch (err) {
    console.error("DELETE failed:", err.response?.data || err.message || err);
  }
})();