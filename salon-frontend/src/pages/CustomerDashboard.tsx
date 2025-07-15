import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import API from '../api/axios';
import toast from 'react-hot-toast';

interface Appt {
  appointmentId: number;
  appointmentDate: string;
  startTime: string;
  endTime: string;
  status?: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED';
  services?: { name?: string; duration?: number }[];
  staff?: { firstName?: string; lastName?: string };
}

export default function CustomerDashboard() {
  const nav = useNavigate();
  const token = localStorage.getItem('token')!;
  const userId = Number(localStorage.getItem('userId') ?? 0);
  const first = (localStorage.getItem('firstName') ?? 'Customer').replace(/^./, c => c.toUpperCase());

  const [next, setNext] = useState<Appt | null>(null);

  useEffect(() => {
    if (!token || !userId) return;
    API.get<Appt[]>(`/api/appointment/customer/${userId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => {
        const now = Date.now();
        const upcoming = (r.data ?? [])
          .filter(a => new Date(`${a.appointmentDate}T${a.startTime}`).getTime() >= now)
          .sort((a, b) =>
            new Date(`${a.appointmentDate}T${a.startTime}`).getTime() -
            new Date(`${b.appointmentDate}T${b.startTime}`).getTime()
          )[0] ?? null;
        setNext(upcoming);
      })
      .catch(() => toast.error('Failed to load appointments'));
  }, [token, userId]);

  const svc = (a: Appt) => (a.services ?? []).map(s => s.name).join(', ') || 'N/A';
  const badge = (s?: string) => {
    const bg = s === 'COMPLETED' ? '#22c55e' : s === 'CANCELLED' ? '#ef4444' : '#3b82f6';
    return (
      <span style={{
        background: bg,
        color: '#fff',
        padding: '2px 8px',
        fontSize: 12,
        fontWeight: 600,
        borderRadius: 12,
        marginLeft: 8,
      }}>{s ?? 'SCHEDULED'}</span>
    );
  };

  return (
    <div style={{ minHeight: '100vh', background: '#f1f5f9', fontFamily: 'Segoe UI, sans-serif' }}>
      <Navbar />
      <div style={{ maxWidth: 700, margin: '0 auto', padding: 32 }}>
        <h2 style={{ fontSize: 28, fontWeight: 700, marginBottom: 24 }}>💇 Welcome, {first}!</h2>

        {next ? (
          <div style={{
            background: '#fff',
            borderRadius: 10,
            padding: 20,
            boxShadow: '0 2px 8px rgba(0,0,0,0.05)',
            border: '1px solid #e2e8f0'
          }}>
            <h3 style={{ marginBottom: 12, fontSize: 18, fontWeight: 600 }}>
              Your next appointment {badge(next.status)}
            </h3>
            <p><b>Service:</b> {svc(next)}</p>
            {next.services?.[0]?.duration &&
              <p><b>Duration:</b> {next.services[0].duration} minutes</p>}
            <p><b>Date:</b> {next.appointmentDate}</p>
            <p><b>Time:</b> {next.startTime} – {next.endTime}</p>
            {next.staff &&
              <p><b>Staff:</b> {next.staff.firstName} {next.staff.lastName}</p>}

            <div style={{ marginTop: 16, display: 'flex', gap: 10 }}>
              <button onClick={() => nav('/my-appointments')} style={btnBlue}>View / Cancel</button>
              <button onClick={() => nav('/book')} style={btnGray}>Book New</button>
            </div>
          </div>
        ) : (
          <div style={{
            background: '#fff',
            borderRadius: 10,
            padding: 20,
            boxShadow: '0 2px 8px rgba(0,0,0,0.05)',
            border: '1px solid #e2e8f0'
          }}>
            <h3 style={{ fontSize: 18, fontWeight: 600, marginBottom: 12 }}>
              No upcoming appointments.
            </h3>
            <button onClick={() => nav('/book')} style={btnBlue}>Book Now</button>
          </div>
        )}
      </div>
    </div>
  );
}

const btnBlue: React.CSSProperties = {
  padding: '8px 16px',
  borderRadius: 6,
  background: '#3b82f6',
  color: '#fff',
  border: 'none',
  fontWeight: 600,
  cursor: 'pointer'
};

const btnGray: React.CSSProperties = {
  ...btnBlue,
  background: '#6b7280'
};
