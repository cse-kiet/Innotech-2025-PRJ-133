import React, { useState, useEffect } from 'react';
import { Calendar, Plus, X } from 'lucide-react';
import CategorySelector from './CategorySelector';
import ImageUpload from './ImageUpload';

const ItemForm = ({ open, onClose, onAddItem, editItem, onUpdateItem }) => {
  const [formData, setFormData] = useState({
    name: '',
    category: '',
    expiryDate: '',
    image: null
  });

  const [inputMode, setInputMode] = useState("date");
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  // ✅ Prefill form when editItem passed
  useEffect(() => {
    console.log(`[ItemForm] useEffect triggered with editItem:`, editItem);
    if (editItem) {
      console.log(`[ItemForm] Prefilling form with item:`, editItem);
      setFormData({
        name: editItem.name || '',
        category: editItem.category || 'food', // Default to food
        expiryDate: editItem.expiry_date || '',
        image: null, // backend doesn't send image currently
      });

      setInputMode(editItem.expiry_date ? "date" : "image");
      console.log(`[ItemForm] Form prefilled successfully`);
    } else {
      console.log(`[ItemForm] No editItem, resetting form`);
      setFormData({
        name: '',
        category: 'food', // Default to food
        expiryDate: '',
        image: null
      });
      setInputMode("date");
    }
  }, [editItem]);

  // Debug: Log when editItem changes
  useEffect(() => {
    console.log(`[ItemForm] editItem prop changed:`, editItem);
  }, [editItem]);

  if (!open) return null;

  const validateForm = () => {
    const newErrors = {};
    if (!formData.name.trim()) newErrors.name = 'Product name is required';
    if (!formData.category) newErrors.category = 'Please select a category';
    if (inputMode === "date" && !formData.expiryDate) newErrors.expiryDate = 'Expiry date is required';
    if (inputMode === "image" && !formData.image && !editItem) newErrors.image = 'Please upload expiry image';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
   e.preventDefault();
   console.log(`[ItemForm] Form submitted via:`, e.type);

   if (loading) {
     console.log(`[ItemForm] Ignoring submit - already loading`);
     return;
   }

   if (!validateForm()) return;
   setLoading(true);

   const userId = localStorage.getItem("user_id");

   console.log(`[ItemForm] Submitting`);
   console.log(`[ItemForm] User ID: ${userId}`);
   console.log(`[ItemForm] editItem prop at submit time:`, editItem);

   if (!userId) {
     alert("User ID not found. Please login again.");
     setLoading(false);
     return;
   }

   const payload = {
     name: formData.name.trim(),
     category: formData.category || 'food', // Default to food if not selected
     expiry_date: formData.expiryDate || null,
     quantity: editItem?.quantity || 1,
     description: editItem?.description || null,
     user_id: parseInt(userId), // ✅ Add user_id from login
     ...(editItem && { id: editItem.id }) // Include id for updates
   };

   console.log(`[ItemForm] Payload:`, payload);

   try {
     if (editItem) {
       console.log(`[ItemForm] UPDATING item with ID: ${editItem.id}`);
       onUpdateItem(payload);
     } else {
       console.log(`[ItemForm] CREATING new item`);
       onAddItem(payload);
     }

     // Reset form immediately after successful submission
     setFormData({ name: "", category: "food", expiryDate: "", image: null });
     setInputMode("date");
     setErrors({});
     onClose();

   } catch (err) {
     alert(err.message);
     console.error("Submit error:", err);
   } finally {
     setLoading(false);
   }
 };


  const handleInputChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) setErrors(prev => ({ ...prev, [field]: '' }));
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white w-[50%] max-w-2xl p-6 rounded-xl shadow-xl relative animate-scaleIn">

        <button onClick={onClose} className="absolute top-3 right-3 text-gray-500 hover:text-black">
          <X className="w-5 h-5" />
        </button>

        <div className="flex items-center space-x-2 mb-4">
          <Plus className="w-5 h-5 text-blue-600" />
          <h2 className="text-xl font-semibold text-gray-900">
            {editItem ? "Edit Item" : "Add New Item"}
          </h2>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">

          <div>
            <label className="font-medium text-gray-700 text-sm">Name *</label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => handleInputChange("name", e.target.value)}
              className="w-full px-3 py-2 border rounded-lg"
            />
            {errors.name && <p className="text-red-600 text-sm">{errors.name}</p>}
          </div>

          <CategorySelector
            selectedCategory={formData.category}
            onCategoryChange={(c) => handleInputChange("category", c)}
          />
          {errors.category && <p className="text-red-600 text-sm">{errors.category}</p>}

          <div className="flex gap-2">
            <button
              type="button"
              onClick={() => setInputMode("date")}
              className={`px-4 py-2 rounded-lg ${inputMode === "date" ? "bg-blue-600 text-white" : "bg-gray-200"}`}
            >
              Enter Date
            </button>
            <button
              type="button"
              onClick={() => setInputMode("image")}
              className={`px-4 py-2 rounded-lg ${inputMode === "image" ? "bg-blue-600 text-white" : "bg-gray-200"}`}
            >
              Upload Image
            </button>
          </div>

          {inputMode === "date" && (
            <div>
              <label className="font-medium text-gray-700 text-sm">Expiry Date *</label>
              <div className="relative">
                <input
                  type="date"
                  value={formData.expiryDate}
                  onChange={(e) => handleInputChange('expiryDate', e.target.value)}
                  className="w-full px-3 py-2 border rounded-lg"
                />
                <Calendar className="absolute right-3 top-2.5 w-5 h-5 text-gray-400" />
              </div>
              {errors.expiryDate && <p className="text-red-600 text-sm">{errors.expiryDate}</p>}
            </div>
          )}

          {inputMode === "image" && (
            <div>
              <ImageUpload
                image={formData.image}
                onImageChange={(img) => handleInputChange("image", img)}
                onImageRemove={() => handleInputChange("image", null)}
                onExpiryDateExtracted={(date) => handleInputChange("expiryDate", date)}
              />
              {errors.image && <p className="text-red-600 text-sm">{errors.image}</p>}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg font-medium"
          >
            {loading ? "Saving..." : editItem ? "Update Item" : "Add Item"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default ItemForm;
