import React, { useState } from 'react';
import { format } from 'date-fns';
import { Calendar, Trash2, Edit, AlertTriangle, CheckCircle, Clock, PackageCheck } from 'lucide-react';
import { getExpiryStatus, getStatusClass } from '../utils/expiryUtils';

const formatExpiryDate = (date) => {
  try {
    if (!date) return "No Expiry Date";

    const d = new Date(date);
    if (isNaN(d)) return "Invalid Date";

    return format(d, "MMM dd, yyyy");
  } catch {
    return "Invalid Date";
  }
};

const ItemList = ({ items, onDeleteItem, onEditItem }) => {
  const [deletingItems, setDeletingItems] = useState(new Set());
  const [slidingItems, setSlidingItems] = useState(new Set());

  const handleDelete = async (itemId) => {
    setDeletingItems(prev => new Set(prev).add(itemId));

    // First phase: fade out (500ms)
    setTimeout(() => {
      setSlidingItems(prev => new Set(prev).add(itemId));
      // Second phase: slide out (500ms)
      setTimeout(() => {
        onDeleteItem(itemId);
        // Clean up states after animation completes
        setTimeout(() => {
          setDeletingItems(prev => {
            const newSet = new Set(prev);
            newSet.delete(itemId);
            return newSet;
          });
          setSlidingItems(prev => {
            const newSet = new Set(prev);
            newSet.delete(itemId);
            return newSet;
          });
        }, 500); // Match slide animation duration
      }, 500); // Match fade animation duration
    }, 100);
  };
  const getStatusIcon = (status) => {
    switch (status) {
      case 'expired':
        return <AlertTriangle className="w-4 h-4" />;
      case 'expiring-soon':
        return <Clock className="w-4 h-4" />;
      case 'good':
        return <CheckCircle className="w-4 h-4" />;
      default:
        return <CheckCircle className="w-4 h-4" />;
    }
  };

  const getCategoryIcon = (category) => {
    switch (category) {
      case 'food':
        return '🍽️';
      case 'medicine':
        return '💊';
      case 'miscellaneous':
        return '📦';
      default:
        return '📦';
    }
  };

  if (items.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">
          <Calendar className="w-8 h-8 text-gray-400" />
        </div>
        <h3 className="text-lg font-medium text-gray-900 mb-2">No items yet</h3>
        <p className="text-gray-600">Add your first item to start tracking expiry dates</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold text-gray-900">Your Items</h2>
        <span className="text-sm text-gray-600">{items.length} item{items.length === 1 ? '' : 's'}</span>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {items.map((item) => {
          const expiryStatus = getExpiryStatus(item.expiry_date);
          const statusClass = getStatusClass(expiryStatus.status);
          
          return (
            <div
              key={item.id}
              className={`bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-all duration-500 ${
                deletingItems.has(item.id) && !slidingItems.has(item.id) ? 'opacity-0 scale-100' :
                slidingItems.has(item.id) ? 'opacity-0 scale-95 translate-x-4' :
                'opacity-100 scale-100 translate-x-0'
              }`}
            >
              
              {item.image && (
                <div className="mb-4">
                  <img
                    src={item.image}
                    alt={item.name}
                    className="w-full h-32 object-cover rounded-lg"
                  />
                </div>
              )}
              
              <div className="space-y-3">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <h3 className="font-semibold text-gray-900 truncate">{item.name}</h3>
                    <div className="flex items-center space-x-1 mt-1">
                      <span className="text-lg">{getCategoryIcon(item.category)}</span>
                      <span className="text-sm text-gray-600 capitalize">{item.category}</span>
                    </div>
                  </div>
                </div>
                
                <div className={`inline-flex items-center space-x-2 px-3 py-1 rounded-full text-sm font-medium border ${statusClass}`}>
                  {getStatusIcon(expiryStatus.status)}
                  <span>{expiryStatus.message}</span>
                </div>
                
                <div className="flex items-center space-x-2 text-sm text-gray-600">
                  <Calendar className="w-4 h-4" />
                  <span>Expires: {formatExpiryDate(item.expiry_date)}</span>
                </div>
                
                <div className="flex space-x-2 pt-2">
                  {expiryStatus.status === 'expired' ? (
                    <button
                      onClick={() => {
                        const blinkitUrl = `https://blinkit.com/s/?q=${encodeURIComponent(item.name)}`;
                        window.open(blinkitUrl, '_blank');
                      }}
                      className="flex-1 bg-orange-500 hover:bg-orange-600 text-white text-sm py-1.5 rounded-lg flex items-center justify-center gap-1 transition-all duration-200"
                    >
                      <span>🛒 Order on Blinkit</span>
                    </button>
                  ) : (
                    <button
                      onClick={() => {
                        console.log('[ItemList] Edit button clicked for item:', item);
                        onEditItem(item);
                      }}
                      className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium py-1.5 px-4 rounded-lg transition-colors duration-200 text-sm flex items-center justify-center space-x-1"
                    >
                      <Edit className="w-3 h-3" />
                      <span>Edit</span>
                    </button>
                  )}

                  <button
                    onClick={() => handleDelete(item.id)}
                    className="flex-1 bg-green-100 hover:bg-green-200 text-green-700 text-sm py-1.5 rounded-lg flex items-center justify-center gap-1 transition-all duration-200"
                  >
                    <PackageCheck className="w-3 h-3" />
                    <span>Finished</span>
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default ItemList;
