import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';

export default function AddService() {
  const navigate = useNavigate();
  const role = localStorage.getItem('role');
  const token = localStorage.getItem('token');

  // Redirect non‑admins away:
  useEffect(() => {
    if (role !== 'ADMIN') {
      toast.error('Unauthorized');
      navigate('/dashboard');
    }
  }, [role, navigate]);

  const [form, setForm] = useState({
    name: '',
    description: '',
    price: '',
    duration: '',
    category: '',
  });

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await axios.post(
        'http://localhost:7070/api/admin/service/create',
        {
          ...form,
          price: parseFloat(form.price),
          duration: parseInt(form.duration, 10),
        },
        { headers: { Authorization: `Bearer ${token}` } },
      );
      toast.success('Service added!');
      navigate('/services');
    } catch (err: any) {
      toast.error(err?.response?.data || 'Failed to add service');
    }
  };

  const inputStyle: React.CSSProperties = {
    width: '100%',
    padding: '10px 12px',
    borderRadius: 8,
    border: '1px solid #cbd5e0',
    fontSize: 14,
  };

  const buttonStyle: React.CSSProperties = {
    width: '100%',
    padding: '12px 16px',
    border: 'none',
    color: '#fff',
    fontWeight: 600,
    borderRadius: 8,
    cursor: 'pointer',
    backgroundColor: '#4f46e5',
  };

  return (
    <div style={{ padding: '2rem', maxWidth: 480, margin: 'auto' }}>
      <h2 style={{ fontSize: 24, fontWeight: 700, marginBottom: '1.5rem' }}>
        ➕ Add New Service
      </h2>

      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 16 }}>
        <input
          name="name"
          placeholder="Service Name"
          value={form.name}
          onChange={handleChange}
          required
          style={inputStyle}
        />
        <input
          name="description"
          placeholder="Description"
          value={form.description}
          onChange={handleChange}
          required
          style={inputStyle}
        />
        <input
          name="price"
          placeholder="Price"
          type="number"
          min="0"
          step="0.01"
          value={form.price}
          onChange={handleChange}
          required
          style={inputStyle}
        />
        <input
          name="duration"
          placeholder="Duration (minutes)"
          type="number"
          min="1"
          value={form.duration}
          onChange={handleChange}
          required
          style={inputStyle}
        />
        <select
          name="category"
          value={form.category}
          onChange={handleChange}
          required
          style={inputStyle}
        >
          <option value="">-- Select Category --</option>
          <option value="HAIR">Hair</option>
          <option value="SKIN">Skin</option>
          <option value="NAIL">Nail</option>
        </select>

        <button type="submit" style={buttonStyle}>
          Add Service
        </button>
      </form>
    </div>
  );
}
