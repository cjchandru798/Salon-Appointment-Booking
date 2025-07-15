import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import RoleGate from '../components/RoleGate';

export default function AdminDashboard() {
  const navigate = useNavigate();
  const first = (localStorage.getItem('firstName') ?? 'Admin')
                .replace(/^./, c => c.toUpperCase());

  /* Core cards every admin sees */
  const cards = [
    { label: 'Appointments', path: '/appointments', icon: '📅'  },
    { label: 'Services',     path: '/services',     icon: '💇‍♂️' },
    { label: 'Staff',        path: '/staff-page',   icon: '👷'   },
    // Bills card will be added conditionally below
  ];

  return (
    <div style={{ minHeight: '100vh', background: '#f0f4f8' }}>
      <Navbar />

      <div style={{ padding: 32, maxWidth: 900, margin: '0 auto',
                    fontFamily: 'Segoe UI, sans-serif', color: '#1e293b' }}>
        <h2 style={{ fontSize: 32, fontWeight: 800, marginBottom: 8 }}>
          🛠️ Welcome back, {first}!
        </h2>
        <p style={{ fontSize: 16, marginBottom: 24 }}>
          Manage your salon resources below.
        </p>

        {/* ───── Card Grid ───── */}
        <div style={{ display: 'flex', gap: 24, flexWrap: 'wrap' }}>
          {/* static cards */}
          {cards.map(c => (
            <Card key={c.path} icon={c.icon} label={c.label}
                  onClick={() => navigate(c.path)} />
          ))}

          {/* Bills card – only for STAFF & ADMIN (admin dashboard already) */}
          <RoleGate allow={['STAFF', 'ADMIN']}>
            <Card icon="📄" label="Bills"
                  onClick={() => navigate('/bills')} />
          </RoleGate>
        </div>

        {/* quick links under grid */}
        <RoleGate allow={['STAFF', 'ADMIN']}>
          <div style={{ marginTop: 32 }}>
            <Link to="/bills/create" style={linkBtn}>➕ Create New Bill</Link>
          </div>
        </RoleGate>
      </div>
    </div>
  );
}

/* ─── Small reusable Card component ─── */
function Card({ icon, label, onClick }: {
  icon: string; label: string; onClick: () => void;
}) {
  return (
    <div onClick={onClick}
         style={{
           flex: '1 1 240px',
           cursor: 'pointer',
           background: '#fff',
           borderRadius: 12,
           padding: 24,
           textAlign: 'center',
           boxShadow: '0 4px 12px rgba(0,0,0,0.06)',
           transition: 'transform .2s, box-shadow .2s',
         }}
         onMouseOver={e => hover(e.currentTarget, true)}
         onMouseOut ={e => hover(e.currentTarget, false)}>
      <div style={{ fontSize: 40, marginBottom: 12 }}>{icon}</div>
      <div style={{ fontSize: 18, fontWeight: 600 }}>{label}</div>
    </div>
  );
}
function hover(el: HTMLElement, up: boolean) {
  el.style.transform  = up ? 'translateY(-4px)' : 'translateY(0)';
  el.style.boxShadow  = up
    ? '0 6px 18px rgba(0,0,0,0.08)'
    : '0 4px 12px rgba(0,0,0,0.06)';
}

const linkBtn: React.CSSProperties = {
  display: 'inline-block',
  padding: '12px 24px',
  background: '#4f46e5',
  color: '#fff',
  borderRadius: 8,
  textDecoration: 'none',
  fontWeight: 600,
};
