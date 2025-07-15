// ────────────────────────────────────────────────────────────────
// BillList.tsx  –  card grid + PDF download + safe delete
// ────────────────────────────────────────────────────────────────
import React, { useEffect, useState } from 'react';
import toast                     from 'react-hot-toast';
import API                       from '../api/axios';

/* ─── DTO coming from /api/bills ─── */
interface Bill {
  billId      : number;
  amount      : number;
  createdAt   : string;
  customerName: string;
}

export default function BillList() {
  const [bills, setBills]   = useState<Bill[]>([]);
  const [loading, setLoad]  = useState(true);
  const token               = localStorage.getItem('token');

  /* ─── initial fetch ─── */
  useEffect(() => {
    API.get<Bill[]>('/api/bills', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(({ data }) => setBills(Array.isArray(data) ? data : []))
      .catch(() => toast.error('Failed to load bills'))
      .finally(() => setLoad(false));
  }, [token]);

  /* ─── PDF download ─── */
  const download = async (id: number) => {
    try {
      const { data } = await API.get(`/api/bills/${id}/pdf`, {
        headers: { Authorization: `Bearer ${token}` },
        responseType: 'blob',
      });
      const url  = URL.createObjectURL(new Blob([data], { type: 'application/pdf' }));
      const link = Object.assign(document.createElement('a'), {
        href: url,
        download: `bill-${id}.pdf`,
      });
      link.click();
      URL.revokeObjectURL(url);
    } catch {
      toast.error('Download failed');
    }
  };

  /* ─── DELETE (with confirm) ─── */
  const deleteBill = async (id: number) => {
    if (!window.confirm(`Delete bill #${id}? This can’t be undone.`)) return;

    try {
      await API.delete(`/api/bills/delete/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setBills((prev) => prev.filter((b) => b.billId !== id));
      toast.success(`Bill #${id} deleted`);
    } catch {
      toast.error('Delete failed');
    }
  };

  /* ─── UI ─── */
  return (
    <div style={{ padding: 32, fontFamily: 'Segoe UI, sans-serif' }}>
      <h2 style={{ fontSize: 26, marginBottom: 20 }}>📄 All Bills</h2>

      {loading ? (
        <p>Loading bills…</p>
      ) : bills.length === 0 ? (
        <p>No bills yet.</p>
      ) : (
        <div style={cardGrid}>
          {bills.map((b) => (
            <div key={b.billId} style={card}>
              <h3 style={{ marginBottom: 6 }}>🧾 Bill #{b.billId}</h3>

              <p><strong>Customer:</strong> {b.customerName}</p>
              <p><strong>Amount&nbsp;:</strong> ₹{b.amount.toFixed(2)}</p>
              <p><strong>Date&nbsp;&nbsp;&nbsp;&nbsp;:</strong> {new Date(b.createdAt).toLocaleDateString()}</p>

              <div style={{ display: 'flex', gap: 8, marginTop: 12 }}>
                <button onClick={() => download(b.billId)} style={btnBlue}>
                  ⬇ PDF
                </button>
                <button onClick={() => deleteBill(b.billId)} style={btnRed}>
                  🗑 Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

/* ─── styles ─── */
const cardGrid: React.CSSProperties = {
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
  gap: 20,
};

const card: React.CSSProperties = {
  border: '1px solid #e2e8f0',
  borderRadius: 8,
  padding: 16,
  background: '#f9fafb',
  boxShadow: '0 2px 6px rgba(0,0,0,0.05)',
};

const baseBtn: React.CSSProperties = {
  flex: 1,
  padding: '6px 12px',
  border: 'none',
  borderRadius: 4,
  color: '#fff',
  cursor: 'pointer',
};

const btnBlue: React.CSSProperties = { ...baseBtn, background: '#3b82f6' };
const btnRed : React.CSSProperties = { ...baseBtn, background: '#ef4444' };
