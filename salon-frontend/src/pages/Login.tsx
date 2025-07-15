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

const roles = ['CUSTOMER', 'STAFF', 'ADMIN'];

export default function Login() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
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
      const res = await axios.post('http://localhost:7070/api/auth/login', form);
      const token = res.data;

      // ✅ Save token and role
      localStorage.setItem('token', token);
      localStorage.setItem('role', form.role);

      // ✅ Decode userId from JWT token
      const decoded = JSON.parse(atob(token.split('.')[1]));
      localStorage.setItem('userId', decoded.uid.toString());

      // ✅ For STAFF, fetch and save staffId
      if (form.role === 'STAFF') {
        const staffRes = await axios.get(
          `http://localhost:7070/api/staff/user/${decoded.uid}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        localStorage.setItem('staffId', staffRes.data.staffId.toString());
      }

      toast.success('Login successful!');

      // ✅ Navigate to appropriate dashboard
      if (form.role === 'ADMIN') navigate('/admin-dashboard');
      else if (form.role === 'STAFF') navigate('/staff-dashboard');
      else navigate('/customer-dashboard');

    } catch (err: any) {
      toast.error(err?.response?.data || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={containerStyle}>
      <form onSubmit={handleSubmit} style={formStyle}>
        <h2 style={titleStyle}>Sign in</h2>

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
          {loading ? 'Signing in…' : 'Sign in'}
        </button>

        <p style={{ textAlign: 'center', fontSize: 14 }}>
          Don’t have an account?{' '}
          <a href="/register" style={{ color: '#4f46e5' }}>
            Register
          </a>
        </p>
      </form>
    </div>
  );
}
