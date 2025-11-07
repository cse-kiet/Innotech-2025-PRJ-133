import { differenceInDays, startOfDay, isBefore } from "date-fns";

// ✅ Check if expired
export const isExpired = (item) => {
  const today = startOfDay(new Date());
  const expiry = startOfDay(new Date(item.expiryDate));
  return isBefore(expiry, today);
};

// ✅ Check if expiring in next 7 days (including today)
export const isExpiringSoon = (item) => {
  const today = startOfDay(new Date());
  const expiry = startOfDay(new Date(item.expiryDate));
  const days = differenceInDays(expiry, today);
  return days >= 0 && days <= 7;
};

// ✅ Status meta for each item (for table badges/cards)
export const getExpiryStatus = (expiryDate) => {
  const today = startOfDay(new Date());
  const expiry = startOfDay(new Date(expiryDate));
  const daysUntilExpiry = differenceInDays(expiry, today);

  if (isBefore(expiry, today)) {
    // Already expired
    return {
      status: "expired",
      message: "Expired",
      days: Math.abs(daysUntilExpiry),
      color: "danger",
    };
  }

  if (daysUntilExpiry <= 3) {
    return {
      status: "expiring-soon",
      message:
        daysUntilExpiry === 0
          ? "Expires today"
          : `Expires in ${daysUntilExpiry} day${daysUntilExpiry === 1 ? "" : "s"}`,
      days: daysUntilExpiry,
      color: "warning",
    };
  }

  if (daysUntilExpiry <= 7) {
    return {
      status: "expiring-soon",
      message: `Expires in ${daysUntilExpiry} days`,
      days: daysUntilExpiry,
      color: "warning",
    };
  }

  return {
    status: "good",
    message: `Expires in ${daysUntilExpiry} days`,
    days: daysUntilExpiry,
    color: "success",
  };
};

// ✅ Tailwind color classes for UI badges
export const getStatusClass = (status) => {
  switch (status) {
    case "expired":
      return "bg-red-100 text-red-800 border-red-200";
    case "expiring-soon":
      return "bg-yellow-100 text-yellow-800 border-yellow-200";
    case "good":
      return "bg-green-100 text-green-800 border-green-200";
    default:
      return "bg-green-100 text-green-800 border-green-200";
  }
};
