/* Reusable inline style objects */
export const containerStyle: React.CSSProperties = {
  minHeight: '100vh',
  background: 'linear-gradient(to bottom right, #f0f4ff 0%, #e8fbff 100%)',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  padding: 16,
  fontFamily: 'Segoe UI, sans-serif',
};

export const formStyle: React.CSSProperties = {
  width: '100%',
  maxWidth: 400,
  background: '#ffffff',
  padding: 24,
  borderRadius: 12,
  boxShadow: '0 8px 20px rgba(0,0,0,0.08)',
  display: 'flex',
  flexDirection: 'column',
  gap: 16,
};

export const titleStyle: React.CSSProperties = {
  textAlign: 'center',
  fontSize: 24,
  fontWeight: 700,
};

export const inputStyle: React.CSSProperties = {
  width: '100%',
  padding: '10px 12px',
  borderRadius: 8,
  border: '1px solid #cbd5e0',
  fontSize: 14,
};

export const buttonStyle: React.CSSProperties = {
  width: '100%',
  padding: '12px 16px',
  border: 'none',
  color: '#fff',
  fontWeight: 600,
  borderRadius: 8,
  cursor: 'pointer',
};

export const roleTabContainer: React.CSSProperties = {
  display: 'flex',
  justifyContent: 'space-between',
  gap: 8,
};

export const roleTab: React.CSSProperties = {
  flex: 1,
  padding: '10px 0',
  borderRadius: 8,
  fontWeight: 600,
  fontSize: 14,
  border: '1px solid #d1d5db',
  cursor: 'pointer',
};
