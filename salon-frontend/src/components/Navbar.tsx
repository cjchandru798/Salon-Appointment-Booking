import { useNavigate } from 'react-router-dom';

export default function Navbar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const role = localStorage.getItem('role') || 'GUEST';

  return (
    <div
      style={{
        background: '#4f46e5',
        color: '#fff',
        padding: '12px 24px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        fontFamily: 'Segoe UI, sans-serif',
      }}
    >
      <h1 style={{ fontSize: 20, fontWeight: 600 }}>Salon Dashboard</h1>
      <div style={{ display: 'flex', gap: 16, alignItems: 'center' }}>
        <span style={{ fontWeight: 500 }}>
          Role: {role.charAt(0).toUpperCase() + role.slice(1).toLowerCase()}
        </span>
        <button
          onClick={handleLogout}
          style={{
            backgroundColor: '#ef4444',
            color: '#fff',
            border: 'none',
            padding: '8px 12px',
            borderRadius: 6,
            cursor: 'pointer',
            fontWeight: 500,
          }}
        >
          Logout
        </button>
      </div>
    </div>
  );
}
