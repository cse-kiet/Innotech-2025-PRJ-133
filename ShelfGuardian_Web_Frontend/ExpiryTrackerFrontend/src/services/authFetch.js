const BASE = "http://127.0.0.1:8000";

export async function loginFetch(username, password) {
    const form = new URLSearchParams();
    form.append("username", username);
    form.append("password", password);
    form.append("grant_type", "");
    const r = await fetch(`${BASE}/token`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: form.toString(),
    });
    if (!r.ok) throw new Error("Login failed");
    const data = await r.json(); // { access_token, token_type }
    localStorage.setItem("accessToken", data.access_token);
    return data;
}