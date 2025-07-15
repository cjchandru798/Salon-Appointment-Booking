import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import toast from 'react-hot-toast';
import {
  containerStyle,
  formStyle,
  titleStyle,
  inputStyle,
  buttonStyle,
  roleTabContainer,
  roleTab,
} from '../styles/shared';

const roles = ['CUSTOMER', 'STAFF'];

export default function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    role: 'CUSTOMER',
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await axios.post('http://localhost:7070/api/auth/register', form);
      localStorage.setItem('role', form.role); // ✅ set role
      toast.success('Account created! Please log in.');
      navigate('/login');
    } catch (err: any) {
      toast.error(err?.response?.data || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={containerStyle}>
      <form onSubmit={handleSubmit} style={formStyle}>
        <h2 style={titleStyle}>Create account</h2>

        <div style={roleTabContainer}>
          {roles.map((r) => (
            <button
              type="button"
              key={r}
              onClick={() => setForm({ ...form, role: r })}
              style={{
                ...roleTab,
                backgroundColor: form.role === r ? '#4f46e5' : '#f0f0f0',
                color: form.role === r ? '#fff' : '#333',
              }}
            >
              {r}
            </button>
          ))}
        </div>

        <input
          type="text"
          name="username"
          value={form.username}
          onChange={handleChange}
          placeholder="Username"
          required
          style={inputStyle}
        />
        <input
          type="email"
          name="email"
          value={form.email}
          onChange={handleChange}
          placeholder="Email"
          required
          style={inputStyle}
        />
        <input
          type="password"
          name="password"
          value={form.password}
          onChange={handleChange}
          placeholder="Password"
          required
          style={inputStyle}
        />

        <button
          type="submit"
          disabled={loading}
          style={{ ...buttonStyle, backgroundColor: '#4f46e5' }}
        >
          {loading ? 'Registering…' : 'Register'}
        </button>

        <p style={{ textAlign: 'center', fontSize: 14 }}>
          Already have an account?{' '}
          <a href="/login" style={{ color: '#4f46e5' }}>
            Sign in
          </a>
        </p>
      </form>
    </div>
  );
}
