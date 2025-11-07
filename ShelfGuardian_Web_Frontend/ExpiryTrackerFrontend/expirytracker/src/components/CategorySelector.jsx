import React from 'react';
import { Utensils, Pill, Package } from 'lucide-react';

const categories = [
  { id: 'food', name: 'Food', icon: Utensils, color: 'text-orange-600' },
  { id: 'medicine', name: 'Medicine', icon: Pill, color: 'text-red-600' },
  { id: 'miscellaneous', name: 'Miscellaneous', icon: Package, color: 'text-blue-600' }
];

const CategorySelector = ({ selectedCategory, onCategoryChange }) => {
  return (
    <div className="space-y-4">
      <h3 className="text-lg font-semibold text-gray-900">Select Category</h3>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {categories.map((category) => {
          const Icon = category.icon;
          const isSelected = selectedCategory === category.id;
          
          return (
            <button
              key={category.id}
              onClick={() => onCategoryChange(category.id)}
              className={`
                p-4 rounded-lg border-2 transition-all duration-200 text-left
                ${isSelected 
                  ? 'border-blue-500 bg-blue-50 shadow-md' 
                  : 'border-gray-200 hover:border-gray-300 hover:shadow-sm'
                }
              `}
            >
              <div className="flex items-center space-x-3">
                <Icon className={`w-6 h-6 ${isSelected ? 'text-blue-600' : category.color}`} />
                <span className={`font-medium ${isSelected ? 'text-blue-900' : 'text-gray-700'}`}>
                  {category.name}
                </span>
              </div>
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default CategorySelector;
