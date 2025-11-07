import React, { useState, useEffect } from 'react';
import { Calendar, Plus, Search, LogOut, User } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import ItemForm from './ItemForm';
import ItemList from './ItemList';
import AlertBanner from './AlertBanner';
import CategoryPieChart from './CategoryPieChart';
import { logout, getUserId, getUsername, getCurrentUser } from '../services/authService';
import { getExpiryStatus } from '../utils/expiryUtils';
import Chatbot from './Chatbot';

import './mainapp.css';

// Notification utility functions
const requestNotificationPermission = async () => {
  if ('Notification' in window) {
    const permission = await Notification.requestPermission();
    return permission === 'granted';
  }
  return false;
};

const showNotification = (title, body, icon = '/favicon.ico', duration = 10000) => {
  if ('Notification' in window && Notification.permission === 'granted') {
    const notification = new Notification(title, {
      body,
      icon,
      badge: '/favicon.ico',
      tag: 'expiry-tracker',
      requireInteraction: false,
      silent: false
    });

    // Auto close after specified duration
    setTimeout(() => {
      notification.close();
    }, duration);

    return notification;
  }
  return null;
};

function MainApp() {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterCategory, setFilterCategory] = useState('all');
  const [filterStatus, setFilterStatus] = useState('all');
  const [alertDismissed, setAlertDismissed] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);

  const handleViewExpired = () => {
    setFilterStatus('expired');
    // Scroll to item list
    const itemListElement = document.querySelector('.filters-container');
    if (itemListElement) {
      itemListElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  };

  const handleViewExpiringSoon = () => {
    setFilterStatus('expiring-soon');
    // Scroll to item list
    const itemListElement = document.querySelector('.filters-container');
    if (itemListElement) {
      itemListElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  };

  const handleViewAllItems = () => {
    setSearchTerm('');
    setFilterCategory('all');
    setFilterStatus('all');
    // Scroll to item list
    const itemListElement = document.querySelector('.filters-container');
    if (itemListElement) {
      itemListElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  };

  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("user_id"); // ✅ fixed keys to match authService
  const username = currentUser?.username || currentUser?.email || "User"; // Get username from API response
  const BASE_URL = "http://127.0.0.1:8000/api/products/";

  const fetchItems = async () => {
    try {
      console.log(`[MainApp] Fetching items for user ${userId}`);
      const res = await fetch(`${BASE_URL}user/${userId}`, { // ✅ Changed to use user-specific endpoint
        headers: { Authorization: `Bearer ${token}` }
      });

      console.log(`[MainApp] Fetch response status: ${res.status} ${res.statusText}`);

      if (!res.ok) {
        const errorText = await res.text();
        console.error(`[MainApp] Failed to fetch products: ${res.status} ${res.statusText}, body: ${errorText}`);
        throw new Error("Failed to fetch products");
      }

      const data = await res.json();
      console.log(`[MainApp] Fetched ${data.length} items for user ${userId}`);

      const normalized = data.map(p => ({
        ...p,
        expiryDate: p.expiry_date
      }));

      setItems(normalized);
    } catch (err) {
      console.error("[MainApp] Fetch items error:", err);
      navigate("/login");
    }
  };

  useEffect(() => {
    fetchItems();
    // Request notification permission on app load
    requestNotificationPermission();

    // Fetch current user info
    const fetchCurrentUser = async () => {
      const user = await getCurrentUser();
      if (user) {
        setCurrentUser(user);
      }
    };
    fetchCurrentUser();

    // Show login notification
    setTimeout(() => {
      showNotification(
        '🎉 Welcome back!',
        'You have successfully logged in to ShelfGuardian.',
        '/logo.png',
        10000
      );
    }, 3000);

    // Set up periodic expiry check (every 1 minute for testing, change to 5 minutes in production)
    const expiryCheckInterval = setInterval(() => {
      console.log('Running periodic expiry check...');
      if (items.length > 0) {
        const expiredItems = items.filter(i => new Date(i.expiryDate) < new Date());
        const expiringSoonItems = items.filter(i => {
          const d = Math.ceil((new Date(i.expiryDate) - new Date()) / (1000 * 60 * 60 * 24));
          return d >= 0 && d <= 7;
        });

        console.log(`Found ${expiredItems.length} expired items and ${expiringSoonItems.length} expiring soon items`);

        // Show notification for expired items
        if (expiredItems.length > 0) {
          showNotification(
            '🚨 Items Expired!',
            `${expiredItems.length} item${expiredItems.length > 1 ? 's' : ''} in your inventory ${expiredItems.length > 1 ? 'have' : 'has'} expired. Please check and dispose safely.`,
            '/logo.png'
          );
        }

        // Show notification for items expiring soon
        if (expiringSoonItems.length > 0) {
          showNotification(
            '⚠️ Items Expiring Soon!',
            `${expiringSoonItems.length} item${expiringSoonItems.length > 1 ? 's' : ''} ${expiringSoonItems.length > 1 ? 'are' : 'is'} expiring within 7 days. Use them soon!`,
            '/logo.png'
          );
        }
      }
    }, 2 * 60 * 60 * 1000); // Check every 2 hours

    // Cleanup interval on unmount
    return () => clearInterval(expiryCheckInterval);
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const addItem = async (newItem) => {
    try {
      console.log(`[MainApp] addItem called with:`, newItem);

      // Make API call to add item
      const res = await fetch(`${BASE_URL}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(newItem)
      });

      console.log(`[MainApp] Add API response status: ${res.status} ${res.statusText}`);

      if (!res.ok) {
        const errorText = await res.text();
        console.error(`[MainApp] Add failed: ${res.status} ${res.statusText}, body: ${errorText}`);
        alert(`Add failed: ${res.status} ${res.statusText}`);
        return;
      }

      const addedItem = await res.json();
      console.log(`[MainApp] Item added successfully:`, addedItem);

      // Update local state
      setItems(prev => [...prev, addedItem]);
      setShowForm(false);

      // Show success notification
      showNotification(
        '✅ Item Added Successfully!',
        `${newItem.name} has been added to your inventory.`,
        '/logo.png',
        5000
      );

    } catch (err) {
      console.error("Add item failed with network error:", err);
      alert("Network error occurred while adding item");
    }
  };

  const deleteItem = async (id) => {
    try {
      console.log(`[MainApp] Deleting item ${id} with token: ${token ? 'present' : 'missing'}`);
      console.log(`[MainApp] Token value: ${token ? token.substring(0, 20) + '...' : 'null'}`);
      console.log(`[MainApp] Making DELETE request to: ${BASE_URL}${id}`);

      const res = await fetch(`${BASE_URL}${id}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      console.log(`[MainApp] Delete response status: ${res.status} ${res.statusText}`);

      if (!res.ok) {
        const errorText = await res.text();
        console.error(`[MainApp] Delete failed: ${res.status} ${res.statusText}, body: ${errorText}`);
        alert(`Delete failed: ${res.status} ${res.statusText}`);
        return;
      }

      console.log(`[MainApp] Delete successful, removing item ${id} from state`);
      setItems(prev => prev.filter(item => item.id !== id));

      // Show delete notification
      showNotification(
        '🗑️ Item Removed',
        'Item has been successfully removed from your inventory.',
        '/logo.png',
        5000
      );
    } catch (err) {
      console.error("Delete failed with network error:", err);
      alert("Network error occurred while deleting item");
    }
  };

  const editItem = async (updatedItem) => {
    try {
      console.log(`[MainApp] Updating item ${updatedItem.id}`);
      console.log(`[MainApp] Update payload:`, updatedItem);

      updatedItem.user_id = userId; // ✅ ensure user id on update
      const res = await fetch(`${BASE_URL}${updatedItem.id}`, { // ✅ Removed trailing slash
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(updatedItem)
      });

      console.log(`[MainApp] Update response status: ${res.status} ${res.statusText}`);

      if (!res.ok) {
        const errorText = await res.text();
        console.error(`[MainApp] Update failed: ${res.status} ${res.statusText}, body: ${errorText}`);
        alert(`Update failed: ${res.status} ${res.statusText}`);
        throw new Error("Failed to update");
      }

      console.log(`[MainApp] Update successful, refreshing items`);
      await fetchItems(); // ✅ await the fetch
      setEditingItem(null);
      setShowForm(false);

      // Show update notification
      showNotification(
        '🔄 Item Updated Successfully!',
        'Your item details have been updated.',
        '/logo.png',
        5000
      );
    } catch (err) {
      console.error("Update failed", err);
    }
  };

  const filteredItems = items.filter(item => {
    const nameLower = item.name?.toLowerCase() || "";
    const matchesSearch = nameLower.includes(searchTerm.toLowerCase());
    const matchesCategory = filterCategory === 'all' || item.category === filterCategory;

    let matchesStatus = true;
    if (filterStatus !== 'all') {
      const expiryStatus = getExpiryStatus(item.expiryDate);
      matchesStatus = expiryStatus.status === filterStatus;
    }

    return matchesSearch && matchesCategory && matchesStatus;
  });

const today = new Date();
today.setHours(0, 0, 0, 0); // remove time for accurate date comparison

const stats = {
  total: items.length,
  expired: items.filter(i => {
    const exp = new Date(i.expiryDate);
    exp.setHours(0, 0, 0, 0);
    return exp < today; // strictly before today
  }).length,
  expiringSoon: items.filter(i => {
    const exp = new Date(i.expiryDate);
    exp.setHours(0, 0, 0, 0);
    const diffDays = Math.ceil((exp - today) / (1000 * 60 * 60 * 24));
    return diffDays >= 0 && diffDays <= 7;
  }).length
};


  // Initial expiry check on items load - removed to avoid duplicate notifications

  // Test notification function for debugging
  const testNotification = () => {
    console.log('Testing notification...');
    console.log('Notification permission:', Notification.permission);

    if (Notification.permission === 'granted') {
      showNotification(
        '🔔 Test Notification',
        'This is a test notification to verify desktop notifications are working!',
        '/favicon.ico'
      );
    } else if (Notification.permission === 'denied') {
      alert('Notifications are blocked. Please enable them in your browser settings.');
    } else {
      // Try to request permission
      requestNotificationPermission().then(granted => {
        if (granted) {
          showNotification(
            '🔔 Test Notification',
            'This is a test notification to verify desktop notifications are working!',
            '/favicon.ico'
          );
        } else {
          alert('Notification permission denied.');
        }
      });
    }
  };

  // Expose test function to window for debugging
  useEffect(() => {
    window.testNotification = testNotification;
  }, []);

  return (
    <div className="app-container">
      <header className="app-header">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between h-16">
          <div className="app-header-logo">
            <img src="/logo.png" alt="ShelfGuardian Logo" className="w-8 h-8" />
            <h1>ShelfGuardian</h1>
          </div>

          <div className="flex items-center space-x-4">
            <button
              onClick={() => {
                setEditingItem(null);
                setShowForm(true);
              }}
              className="add-item-button"
            >
              <Plus className="w-4 h-4" />
              <span>Add Item</span>
            </button>
            <button onClick={handleLogout} className="logout-button">
              <LogOut className="w-4 h-4" />
              <span>Logout</span>
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Welcome Section */}
        <div className="welcome-section">
          <div className="welcome-content">
            <User className="welcome-icon" />
            <div className="welcome-text">
              <h1 className="welcome-title">Welcome back, {username}!</h1>
              <p className="welcome-subtitle">Manage your expiry dates and stay organized</p>
            </div>
          </div>
        </div>

        {/* Dashboard Section */}
        <div className="bg-gradient-to-br from-blue-50 to-indigo-100 rounded-2xl p-8 mb-8 shadow-lg border border-blue-200">
          {/* Category Pie Chart - Commented out */}
          {/* {items.length > 0 && (
            <div className="flex justify-center items-center mb-8">
              <CategoryPieChart items={items} />
            </div>
          )} */}

          {/* Quick Stats */}
          <div className="stats-container-linear">
            <div className="stats-card" onClick={handleViewAllItems}>
              <div className="stats-value">{stats.total}</div>
              <div className="stats-label">Total Items</div>
            </div>
            <div className="stats-card" onClick={handleViewExpired}>
              <div className="stats-value">{stats.expired}</div>
              <div className="stats-label">Expired</div>
            </div>
            <div className="stats-card" onClick={handleViewExpiringSoon}>
              <div className="stats-value">{stats.expiringSoon}</div>
              <div className="stats-label">Expiring Soon</div>
            </div>
          </div>
        </div>

        {!alertDismissed && items.length > 0 && (
          <div className="mb-6">
            <AlertBanner
              items={items}
              onDismiss={() => setAlertDismissed(true)}
              onViewExpired={handleViewExpired}
              onViewExpiringSoon={handleViewExpiringSoon}
            />
          </div>
        )}

        <ItemForm
          open={showForm}
          onClose={() => {
            setShowForm(false);
            setEditingItem(null);
          }}
          editItem={editingItem}
          onAddItem={addItem}
          onUpdateItem={editItem}
        />


        {items.length > 0 && (
          <div className="filters-container">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Search Items</label>
                <div className="relative">
                  <Search className="absolute left-3 top-2.5 w-4 h-4 text-gray-400" />
                  <input
                    type="text"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    placeholder="Search by name..."
                    className="search-input"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Category</label>
                <select value={filterCategory} onChange={(e) => setFilterCategory(e.target.value)} className="filter-select">
                  <option value="all">All</option>
                  <option value="food">Food</option>
                  <option value="medicine">Medicine</option>
                  <option value="miscellaneous">Miscellaneous</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Status</label>
                <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)} className="filter-select">
                  <option value="all">All</option>
                  <option value="expired">Expired</option>
                  <option value="expiring-soon">Expiring Soon</option>
                  <option value="good">Good</option>
                </select>
              </div>
            </div>
          </div>
        )}

        <ItemList
          items={filteredItems}
          onDeleteItem={deleteItem}
          onEditItem={(item) => {
            console.log('[MainApp] onEditItem called with item:', item);
            setEditingItem(item);
            setShowForm(true);
            console.log('[MainApp] editingItem set to:', item);
          }}
        />
      </main>
      <Chatbot />
    </div>
  );
}

export default MainApp;
