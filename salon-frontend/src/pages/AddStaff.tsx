import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import API from '../api/axios';
import toast from 'react-hot-toast';

interface UserDTO {
  userId: number;
  email: string;
}

export default function AddStaff() {
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const [users, setUsers] = useState<UserDTO[]>([]);
  const [form, setForm] = useState({
    userId: '',
    firstName: '',
    lastName: '',
    phone: '',
    specialization: '',
  });

  useEffect(() => {
    if (!token) return;
    API.get<UserDTO[]>('/api/users/without-staff', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(({ data }) => setUsers(data))
      .catch(() => toast.error('Failed to load users'));
  }, [token]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.userId) return toast.error('Select a user');

    try {
      await API.post('/api/staff/create', form, {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success('Staff added');
      navigate('/staff');
    } catch (err) {
      toast.error('Error creating staff');
    }
  };

  return (
    <div>
      <Navbar />
      <div style={{ padding: 32, maxWidth: 600, margin: '0 auto' }}>
        <h2 style={{ fontSize: 24, fontWeight: 700 }}>➕ Add Staff</h2>
        <form onSubmit={handleSubmit}>
          <label>User</label>
          <select name="userId" value={form.userId} onChange={handleChange} required>
            <option value="">Select user</option>
            {users.map(u => (
              <option key={u.userId} value={u.userId}>{u.email}</option>
            ))}
          </select>

          <input name="firstName" placeholder="First Name" value={form.firstName} onChange={handleChange} required />
          <input name="lastName" placeholder="Last Name" value={form.lastName} onChange={handleChange} required />
          <input name="phone" placeholder="Phone" value={form.phone} onChange={handleChange} required />
          <input name="specialization" placeholder="Specialization" value={form.specialization} onChange={handleChange} />

          <button type="submit">Add Staff</button>
        </form>
      </div>
    </div>
  );
}
