const BASE_URL = "http://127.0.0.1:8000/api/products";

export const ProductService = {
  getAll: async () => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("user_id"); // 👈 you stored this at login, right?
    console.log(`[ProductService] Fetching products for user ${userId} with token: ${token ? 'present' : 'missing'}`);
    console.log(`[ProductService] Token value: ${token ? token.substring(0, 20) + '...' : 'null'}`);
    console.log(`[ProductService] User ID: ${userId}`);

    const res = await fetch(`http://127.0.0.1:8000/api/products/user/${userId}`, {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    console.log(`[ProductService] Response status: ${res.status} ${res.statusText}`);
    if (!res.ok) {
      const errorText = await res.text();
      console.error(`[ProductService] Failed to fetch products: ${res.status} ${res.statusText}, body: ${errorText}`);
      throw new Error("Failed to fetch user's products");
    }
    const data = await res.json();
    console.log(`[ProductService] Fetched ${data.length || 0} products`);
    return data;
  },


  create: async (data) => {
    const token = localStorage.getItem("token");
    console.log(`[ProductService] Creating product with token: ${token ? 'present' : 'missing'}`);

    const res = await fetch(BASE_URL + "/", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify(data)
    });

    console.log(`[ProductService] Create response status: ${res.status} ${res.statusText}`);
    if (!res.ok) {
      const errorText = await res.text();
      console.error(`[ProductService] Failed to create product: ${res.status} ${res.statusText}, body: ${errorText}`);
      throw new Error("Failed to create product");
    }
    return res.json();
  },

  update: async (id, data) => {
    const token = localStorage.getItem("token");
    console.log(`[ProductService] Updating product ${id} with token: ${token ? 'present' : 'missing'}`);

    const res = await fetch(`${BASE_URL}/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify(data)
    });

    console.log(`[ProductService] Update response status: ${res.status} ${res.statusText}`);
    if (!res.ok) {
      const errorText = await res.text();
      console.error(`[ProductService] Failed to update product: ${res.status} ${res.statusText}, body: ${errorText}`);
      throw new Error("Failed to update product");
    }
    return res.json();
  },

  delete: async (id) => {
    const token = localStorage.getItem("token");
    console.log(`[ProductService] Deleting product ${id} with token: ${token ? 'present' : 'missing'}`);

    const res = await fetch(`${BASE_URL}/${id}`, {
      method: "DELETE",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    console.log(`[ProductService] Delete response status: ${res.status} ${res.statusText}`);
    if (!res.ok) {
      const errorText = await res.text();
      console.error(`[ProductService] Failed to delete product: ${res.status} ${res.statusText}, body: ${errorText}`);
      throw new Error("Failed to delete product");
    }
  },

  expiringSoon: async () => {
    const res = await fetch(`${BASE_URL}/expiring/soon`);
    if (!res.ok) throw new Error("Failed to fetch expiring");
    return res.json();
  }
};
