import React, { useEffect, useState } from "react";
import ItemList from "../components/ItemList";
import ItemForm from "../components/ItemForm";
import AlertBanner from "../components/AlertBanner";
import { ProductService } from "../services/ProductService";

const DashboardPage = () => {
  const [items, setItems] = useState([]);
  const [editingItem, setEditingItem] = useState(null);
  const [showAlert, setShowAlert] = useState(true);

  const loadItems = async () => {
    const products = await ProductService.getAll();
    setItems(products);
  };

  useEffect(() => {
    loadItems();
  }, []);

  const handleAddItem = async (data) => {
    await ProductService.create(data);
    await loadItems();
  };

  const handleUpdateItem = async (id, data) => {
    await ProductService.update(id, data);
    await loadItems();
    setEditingItem(null);
  };

  const handleDeleteItem = async (id) => {
    await ProductService.delete(id);
    setItems(items.filter(item => item.id !== id));
  };

  return (
    <div className="p-6 max-w-4xl mx-auto space-y-4">

      {showAlert && (
        <AlertBanner 
          items={items} 
          onDismiss={() => setShowAlert(false)} 
        />
      )}

      <ItemForm
        onAddItem={handleAddItem}
        onUpdateItem={handleUpdateItem}
        editingItem={editingItem}
      />

      <ItemList
        items={items}
        onDeleteItem={handleDeleteItem}
        onEditItem={setEditingItem}
      />
    </div>
  );
};

export default DashboardPage;
