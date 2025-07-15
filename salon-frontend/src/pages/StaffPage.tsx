// src/pages/StaffPage.tsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import API from '../api/axios';
import toast from 'react-hot-toast';

interface StaffSummaryDTO {
  staffId: number;
  firstName: string;
  lastName: string;
  phone: string;
  specialization?: string;
  totalAppointments: number;
  completed: number;
  scheduled: number;
}

export default function StaffPage() {
  const navigate = useNavigate();
  const token = localStorage.getItem('token') || '';
  const role = (localStorage.getItem('role') ?? '').toUpperCase();

  const [booted, setBooted] = useState(false);
  const [loading, setLoading] = useState(true);
  const [staff, setStaff] = useState<StaffSummaryDTO[]>([]);

  useEffect(() => {
    if (role !== 'ADMIN') {
      navigate(
        role === 'STAFF'
          ? '/staff-dashboard'
          : role === 'CUSTOMER'
            ? '/customer-dashboard'
            : '/login',
        { replace: true }
      );
      return;
    }
    setBooted(true);
  }, [role, navigate]);

  const loadStaff = () => {
    if (!token) return;
    setLoading(true);
    API.get<StaffSummaryDTO[]>('/api/staff/summary', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(({ data }) => setStaff(Array.isArray(data) ? data : []))
      .catch(() => toast.error('Failed to fetch staff'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    if (!booted || !token) return;
    loadStaff();
  }, [booted, token]);

  const deleteStaff = (id: number) => {
    if (!window.confirm('Are you sure you want to delete this staff member?')) return;
    API.delete(`/api/staff/delete/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(() => {
        toast.success('Staff deleted');
        setStaff(prev => prev.filter(s => s.staffId !== id));
      })
      .catch(() => toast.error('Failed to delete staff'));
  };

  if (!booted) return null;

  return (
    <div style={{ minHeight: '100vh', background: '#f0f4f8' }}>
      <Navbar />

      <div style={{
        padding: 32, maxWidth: 900, margin: '0 auto',
        fontFamily: 'Segoe UI, sans-serif', color: '#1e293b',
      }}>
        <button
          onClick={() => navigate('/admin-dashboard')}
          style={{
            marginBottom: 16, background: 'transparent', border: 'none',
            color: '#3b82f6', fontSize: 16, cursor: 'pointer',
          }}
        >
          ← Back to Admin Dashboard
        </button>

        <h2 style={{ fontSize: 28, fontWeight: 700, marginBottom: 8 }}>
          👷 Staff Management
        </h2>
        <p style={{ marginBottom: 24 }}>
          {loading ? 'Loading…' : `Total Staff: ${staff.length}`}
        </p>

        {loading ? (
          <p>Fetching staff list…</p>
        ) : staff.length === 0 ? (
          <p>No staff records found.</p>
        ) : (
          staff.map(s => (
            <div
              key={s.staffId}
              style={{
                background: '#fff', borderRadius: 12, padding: 20,
                marginBottom: 16, boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontSize: 20, fontWeight: 700 }}>
                    {s.firstName} {s.lastName}
                  </div>
                  <div style={{ fontSize: 14, color: '#555', marginBottom: 6 }}>
                    📞 {s.phone || 'N/A'} &nbsp;|&nbsp;
                    🔧 {s.specialization || 'Unassigned'}
                  </div>
                  <div style={{ fontSize: 14 }}>
                    Appointments {s.totalAppointments} &nbsp;
                    (✅ {s.completed} | ⏳ {s.scheduled})
                  </div>
                </div>

                {/* Only delete button */}
                <button
                  onClick={() => deleteStaff(s.staffId)}
                  style={{
                    background: 'transparent',
                    border: 'none',
                    fontSize: 18,
                    color: '#ef4444',
                    cursor: 'pointer',
                  }}
                  title="Delete staff"
                >
                  🗑️
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
