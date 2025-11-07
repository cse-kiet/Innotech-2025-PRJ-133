export const getUserIdFromToken = () => {
  const token = localStorage.getItem("accessToken");
  if (!token) return null;

  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.sub || payload.user_id || payload.id;
  } catch (e) {
    console.error("Invalid token", e);
    return null;
  }
};
