import React, { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import API, { cancelAppointment } from '../api/axios';

interface Appointment {
  appointmentId: number;
  appointmentDate: string;
  startTime: string;
  endTime: string;
  status?: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED';
  services?: { name?: string }[];
  customers?: { firstName?: string; lastName?: string };
  staff?: { firstName?: string; lastName?: string };
}

export default function MyAppointments() {
  const token = localStorage.getItem('token')!;
  const role = (localStorage.getItem('role') ?? '').toUpperCase().trim();
  const userId = Number(localStorage.getItem('userId') ?? 0); // ✅ Use this for both CUSTOMER and STAFF

  const [list, setList] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!token) return;
    setLoading(true);

    const url = role === 'ADMIN'
      ? '/api/appointment/all'
      : role === 'STAFF'
      ? `/api/appointment/staff/${userId}`  // ✅ FIXED: Use userId instead of staffId
      : `/api/appointment/customer/${userId}`;

    API.get<Appointment[]>(url, { headers: { Authorization: `Bearer ${token}` } })
      .then(r => setList(r.data ?? []))
      .catch(() => toast.error('Cannot load appointments'))
      .finally(() => setLoading(false));
  }, [token, role, userId]);

  const svc = (a: Appointment) => (a.services ?? []).map(s => s.name).join(', ') || 'Unnamed';
  const isPast = (a: Appointment) => new Date(`${a.appointmentDate}T${a.endTime}`) < new Date();

  const badge = (s?: string) => {
    const bg = s === 'COMPLETED' ? '#22c55e' : s === 'CANCELLED' ? '#ef4444' : '#3b82f6';
    return (
      <span style={{
        background: bg,
        color: '#fff',
        fontSize: 12,
        padding: '2px 8px',
        borderRadius: 12,
        fontWeight: 600
      }}>{s}</span>
    );
  };

  const handleCancel = async (id: number) => {
    if (!window.confirm('Are you sure you want to cancel this appointment?')) return;

    try {
      await cancelAppointment(id, token);
      setList(l => l.map(x =>
        x.appointmentId === id ? { ...x, status: 'CANCELLED' } : x
      ));
      toast.success('Appointment cancelled');
    } catch (e) {
      toast.error('Cancel failed');
    }
  };

  const handleComplete = async (id: number) => {
    if (!window.confirm('Mark this appointment as completed?')) return;

    try {
      await API.put(`/api/appointment/${id}/status`, { status: 'COMPLETED' }, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setList(l => l.map(x =>
        x.appointmentId === id ? { ...x, status: 'COMPLETED' } : x
      ));
      toast.success('Appointment marked as completed');
    } catch {
      toast.error('Failed to mark completed');
    }
  };

  return (
    <div style={{ padding: 32, fontFamily: 'Segoe UI, sans-serif', background: '#f8fafc', minHeight: '100vh' }}>
      <h2 style={{ fontSize: 26, marginBottom: 24 }}>
        {role === 'ADMIN' ? 'All Appointments' : role === 'STAFF' ? 'My Schedule' : 'My Appointments'}
      </h2>

      {loading ? (
        <p>Loading…</p>
      ) : list.length === 0 ? (
        <p>No appointments found.</p>
      ) : (
        <div style={cardGrid}>
          {list.map((a) => (
            <div key={a.appointmentId} style={card}>
              <h3 style={{ marginBottom: 6 }}>{svc(a)}</h3>
              <p><b>Date:</b> {a.appointmentDate}</p>
              <p><b>Time:</b> {a.startTime} – {a.endTime}</p>
              {a.staff && <p><b>Staff:</b> {a.staff.firstName} {a.staff.lastName}</p>}
              <div style={{ marginTop: 8 }}>{badge(a.status)}</div>

              <div style={{ marginTop: 12, display: 'flex', gap: 10 }}>
                {role !== 'ADMIN' && a.status === 'SCHEDULED' && !isPast(a) && (
                  <button onClick={() => handleCancel(a.appointmentId)} style={btnRed}>
                    Cancel
                  </button>
                )}
                {role === 'STAFF' && a.status === 'SCHEDULED' && isPast(a) && (
                  <button onClick={() => handleComplete(a.appointmentId)} style={btnGreen}>
                    Mark Completed
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

/* ─── Styles ─── */
const cardGrid: React.CSSProperties = {
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
  gap: 20,
};

const card: React.CSSProperties = {
  border: '1px solid #e2e8f0',
  borderRadius: 8,
  padding: 16,
  background: '#fff',
  boxShadow: '0 2px 6px rgba(0,0,0,0.05)',
};

const btnRed: React.CSSProperties = {
  padding: '6px 12px',
  borderRadius: 4,
  border: 'none',
  background: '#ef4444',
  color: '#fff',
  cursor: 'pointer',
  fontWeight: 600
};

const btnGreen: React.CSSProperties = {
  ...btnRed,
  background: '#22c55e'
};
