import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import RoleGate from '../components/RoleGate';
import API from '../api/axios';

interface ApptDTO {
  appointmentId: number;
  appointmentDate: string;
  startTime: string;
  endTime: string;
  customers?: { firstName?: string; lastName?: string };
  services?: { name?: string };
  staff?: { staffId?: number };
}

export default function StaffDashboard() {
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const userId = Number(localStorage.getItem('userId')); // ✅ JWT user ID
  const staffId = Number(localStorage.getItem('staffId')); // for filtering, optional
  const first = (localStorage.getItem('firstName') ?? 'Staff').replace(/^./, c => c.toUpperCase());

  const [nextAppt, setNextAppt] = useState<ApptDTO | null>(null);

  // ✅ Fetch staff's upcoming appointments
  useEffect(() => {
    if (!token || !userId) return;

    API.get<ApptDTO[]>(`/api/appointment/staff/${userId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(({ data }) => {
        const now = Date.now();
        const upcoming = data
          .filter(a =>
            new Date(`${a.appointmentDate}T${a.startTime}`).getTime() >= now
          )
          .sort(
            (a, b) =>
              new Date(`${a.appointmentDate}T${a.startTime}`).getTime() -
              new Date(`${b.appointmentDate}T${b.startTime}`).getTime()
          );
        setNextAppt(upcoming[0] ?? null);
      })
      .catch(() => {
        setNextAppt(null);
      });
  }, [token, userId]);

  return (
    <div style={{ minHeight: '100vh', background: '#f0f4f8' }}>
      <Navbar />

      <div
        style={{
          padding: 32,
          maxWidth: 800,
          margin: '0 auto',
          fontFamily: 'Segoe UI, sans-serif',
          color: '#1e293b',
        }}
      >
        <h2 style={{ fontSize: 30, fontWeight: 700, marginBottom: 24 }}>
          ✂️ Hello, {first}!
        </h2>

        {nextAppt ? (
          <div
            style={{
              background: '#fff',
              borderRadius: 12,
              padding: 24,
              boxShadow: '0 2px 10px rgba(0,0,0,0.06)',
              marginBottom: 24,
            }}
          >
            <h3 style={{ marginBottom: 8 }}>Your next appointment</h3>
            <div>
              <b>Customer:</b> {nextAppt.customers?.firstName} {nextAppt.customers?.lastName}
            </div>
            <div>
              <b>Service :</b> {nextAppt.services?.name ?? 'N/A'}
            </div>
            <div>
              <b>Date :</b> {nextAppt.appointmentDate}
            </div>
            <div>
              <b>Time :</b> {nextAppt.startTime} – {nextAppt.endTime}
            </div>

            <button
              onClick={() => navigate('/my-appointments')}
              style={{
                marginTop: 12,
                padding: '8px 16px',
                background: '#3b82f6',
                color: '#fff',
                border: 'none',
                borderRadius: 6,
                cursor: 'pointer',
              }}
            >
              View full schedule
            </button>
          </div>
        ) : (
          <p>No upcoming appointments.</p>
        )}

        {/* ───── Billing quick links – visible for STAFF & ADMIN ───── */}
        <RoleGate allow={['STAFF', 'ADMIN']}>
          <div style={{ display: 'flex', gap: 12 }}>
            <Link to="/bills" style={quickBtn}>
              📄 View Bills
            </Link>
          </div>
        </RoleGate>
      </div>
    </div>
  );
}

const quickBtn: React.CSSProperties = {
  flex: 1,
  textAlign: 'center',
  padding: '12px 0',
  background: '#4f46e5',
  color: '#fff',
  borderRadius: 8,
  textDecoration: 'none',
  fontWeight: 600,
};
