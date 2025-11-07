import React from 'react';
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';

const CategoryPieChart = ({ items }) => {
  // Calculate category distribution
  const categoryData = items.reduce((acc, item) => {
    const category = item.category || 'miscellaneous';
    acc[category] = (acc[category] || 0) + 1;
    return acc;
  }, {});

  // Convert to chart data format
  const data = Object.entries(categoryData).map(([category, count]) => ({
    name: category.charAt(0).toUpperCase() + category.slice(1),
    value: count,
    percentage: ((count / items.length) * 100).toFixed(1)
  }));

  // Color palette for categories
  const COLORS = {
    food: '#10B981', // green
    medicine: '#EF4444', // red
    miscellaneous: '#6B7280', // gray
  };

  const getColor = (category) => {
    return COLORS[category.toLowerCase()] || '#8B5CF6'; // purple for unknown categories
  };

  if (items.length === 0) {
    return (
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Product Categories</h3>
        <div className="flex items-center justify-center h-64 text-gray-500">
          <div className="text-center">
            <div className="text-4xl mb-2">📊</div>
            <p>No items to display</p>
            <p className="text-sm">Add some items to see category distribution</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Product Categories</h3>
      <div className="h-64" style={{ minWidth: '200px', minHeight: '200px' }}>
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              labelLine={false}
              label={({ name, percentage }) => `${name}: ${percentage}%`}
              outerRadius={80}
              fill="#8884d8"
              dataKey="value"
            >
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={getColor(entry.name.toLowerCase())} />
              ))}
            </Pie>
            <Tooltip
              formatter={(value, name) => [`${value} items (${data.find(d => d.name === name)?.percentage}%)`, name]}
            />
            <Legend />
          </PieChart>
        </ResponsiveContainer>
      </div>
      <div className="mt-4 grid grid-cols-3 gap-2 text-sm">
        {data.map((item, index) => (
          <div key={index} className="flex items-center space-x-2">
            <div
              className="w-3 h-3 rounded-full"
              style={{ backgroundColor: getColor(item.name.toLowerCase()) }}
            ></div>
            <span className="text-gray-600">{item.name}: {item.value}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CategoryPieChart;