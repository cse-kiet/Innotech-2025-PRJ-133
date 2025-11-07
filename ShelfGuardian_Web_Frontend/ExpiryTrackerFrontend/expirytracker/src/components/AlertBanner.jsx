import React from "react";
import { AlertTriangle, Clock, X } from "lucide-react";
import { isExpired, isExpiringSoon } from "../utils/expiryUtils";

const AlertBanner = ({ items, onDismiss, onViewExpired, onViewExpiringSoon }) => {
  if (!items || items.length === 0) return null;

  const expiredItems = items.filter(isExpired).length;
  const expiringSoonItems = items.filter(isExpiringSoon).length;

  // No alerts? Don't show anything.
  if (expiredItems === 0 && expiringSoonItems === 0) return null;

  return (
    <div className="space-y-2">
      
      {/* Expired Items Banner */}
      {expiredItems > 0 && (
        <div className="rounded-lg border p-4 bg-red-50 border-red-200 text-red-800 cursor-pointer hover:bg-red-100 transition-colors duration-200" onClick={onViewExpired}>
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <AlertTriangle className="w-5 h-5" />
              <div>
                <h3 className="font-semibold">
                  {expiredItems} item{expiredItems !== 1 ? "s" : ""} expired
                </h3>
                <p className="text-sm mt-1">
                  Please dispose expired items safely. Click to view.
                </p>
              </div>
            </div>
            <button
              onClick={onDismiss}
              className="hover:opacity-75 transition-opacity duration-200"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}

      {/* Expiring Soon Banner */}
      {expiringSoonItems > 0 && (
        <div className="rounded-lg border p-4 bg-yellow-50 border-yellow-200 text-yellow-800 cursor-pointer hover:bg-yellow-100 transition-colors duration-200" onClick={onViewExpiringSoon}>
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <Clock className="w-5 h-5" />
              <div>
                <h3 className="font-semibold">
                  {expiringSoonItems} item
                  {expiringSoonItems !== 1 ? "s" : ""} expiring soon
                </h3>
                <p className="text-sm mt-1">
                  Use them soon to avoid waste. Click to view.
                </p>
              </div>
            </div>
            <button
              onClick={onDismiss}
              className="hover:opacity-75 transition-opacity duration-200"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default AlertBanner;
