// ────────────────────────────────────────────────────────────────
// CreateBill.tsx   – save bill, then redirect (no PDF preview)
// ────────────────────────────────────────────────────────────────
import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate }   from 'react-router-dom';
import toast             from 'react-hot-toast';
import API               from '../api/axios';

/* DTOs returned by the back‑end */
interface ServiceDTO {
  serviceId: number;
  name     : string;
  price    : number;
}
interface AppointmentDTO {
  appointmentId : number;
  appointmentDate: string;
  startTime      : string;
  customers      : { customerId: number; firstName: string; lastName: string };
}

/* Local line‑item (UI only) */
type Item = { id: number; name: string; price: number };

export default function CreateBill() {

  /* ───── state ───── */
  const [catalogue, setCatalogue]     = useState<ServiceDTO[]>([]);
  const [appts,     setAppts]         = useState<AppointmentDTO[]>([]);
  const [items,     setItems]         = useState<Item[]>([]);
  const [apptId,    setApptId]        = useState<number | ''>('');
  const [invoiceId, setInvoiceId]     = useState(`INV-${Date.now()}`);
  const [billDate,  setBillDate]      = useState(new Date().toISOString().slice(0,10));
  const [status,    setStatus]        = useState<'PAID'|'PENDING'>('PENDING');

  /* helpers */
  const token = localStorage.getItem('token')!;
  const nav   = useNavigate();
  const selectedAppt = appts.find(a => a.appointmentId === Number(apptId));

  /* ───── initial fetch (services + open appts) ───── */
  useEffect(() => {
    API.get<ServiceDTO[]>('/api/salon/service/all')
       .then(r => setCatalogue(r.data ?? []))
       .catch(() => toast.error('Failed to load services'));

    API.get<AppointmentDTO[]>('/api/appointment/all',
      { headers: { Authorization:`Bearer ${token}` } })
       .then(r => setAppts(r.data ?? []))
       .catch(() => toast.error('Failed to load appointments'));
  }, [token]);

  /* ───── add / remove items ───── */
  const addItem = (svcId: number) => {
    if (items.some(it => it.id === svcId)) {
      toast('Already added'); return;
    }
    const svc = catalogue.find(s => s.serviceId === svcId);
    if (svc) setItems(p => [ ...p, { id: svc.serviceId, name: svc.name, price: svc.price } ]);
  };

  const delItem = (idx: number) => setItems(p => p.filter((_, i) => i !== idx));

  /* ───── totals ───── */
  const subTotal = useMemo(() => items.reduce((s,i) => s + i.price, 0), [items]);
  const gst      = subTotal * 0.18;
  const grand    = subTotal + gst;

  /* ───── submit ───── */
  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!apptId || items.length === 0) {
      toast.error('Select appointment and at least one service'); return;
    }

    try {
      await API.post('/api/bills/create', {
        bill: {
          appointment : { appointmentId: Number(apptId) },
          customers   : { customerId: selectedAppt!.customers.customerId },
          amount      : grand,
          paymentStatus: status,
          paymentMethod: "",         // optional
          transactionId: "",         // optional
          createdAt   : new Date().toISOString(),
        },
        serviceIds: items.map(i => i.id),
      }, { headers: { Authorization:`Bearer ${token}` } });

      toast.success('Bill saved 👍');
      nav('/bills');
    } catch (err:any) {
      if (err?.response?.status === 409) toast.error('Invoice ID already exists');
      else toast.error('Create failed');
    }
  };

  /* ───── UI ───── */
  return (
    <div style={{maxWidth:680,margin:'2rem auto',padding:32,fontFamily:'Segoe UI'}}>
      <h2 style={{fontSize:26,marginBottom:20}}>🧾 Create Bill</h2>

      <form onSubmit={submit} style={{display:'grid',gap:18}}>
        {/* Invoice + date */}
        <div style={{display:'flex',gap:16}}>
          <label style={{flex:1}}>
            Invoice&nbsp;ID
            <input value={invoiceId} onChange={e=>setInvoiceId(e.target.value)} required style={input}/>
          </label>
          <label style={{width:180}}>
            Bill&nbsp;Date
            <input type="date" value={billDate} onChange={e=>setBillDate(e.target.value)} required style={input}/>
          </label>
        </div>

        {/* Appointment selector */}
        <label>
          Select&nbsp;Appointment
          <select value={apptId} onChange={e=>setApptId(Number(e.target.value))} required style={input}>
            <option value="">Choose…</option>
            {appts.map(a=>(
              <option key={a.appointmentId} value={a.appointmentId}>
                {`${a.customers.firstName} ${a.customers.lastName} • ${a.appointmentDate}`}
              </option>
            ))}
          </select>
        </label>

        {/* Items table */}
        <div style={{border:'1px solid #e2e8f0',borderRadius:8}}>
          <table style={{width:'100%',borderCollapse:'collapse'}}>
            <thead style={{background:'#f8fafc'}}>
              <tr><th style={th}>Service</th><th style={th}>Price ₹</th><th style={th}></th></tr>
            </thead>
            <tbody>
              {items.map((it,idx)=>(
                <tr key={idx}>
                  <td style={td}>{it.name}</td>
                  <td style={td}>{it.price.toFixed(2)}</td>
                  <td style={{...td,textAlign:'center'}}>
                    <button type="button" onClick={()=>delItem(idx)} style={btnSmall}>🗑️</button>
                  </td>
                </tr>
              ))}
              {items.length===0 && (
                <tr><td colSpan={3} style={{textAlign:'center',padding:12}}>No services added</td></tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Add service */}
        <div style={{display:'flex',gap:8,alignItems:'center'}}>
          <select defaultValue="" id="svcSel" style={{...input,flex:1}}>
            <option value="">Add service…</option>
            {catalogue.map(s=>(
              <option key={s.serviceId} value={s.serviceId}>
                {s.name} – ₹{s.price}
              </option>
            ))}
          </select>
          <button type="button" style={btnGreen}
                  onClick={()=>{
                    const sel = document.getElementById('svcSel') as HTMLSelectElement;
                    if (sel.value) { addItem(Number(sel.value)); sel.value=''; }
                  }}>➕ Add</button>
        </div>

        {/* Totals */}
        <div style={{textAlign:'right',fontSize:15}}>
          <div>Sub‑Total : <b>₹{subTotal.toFixed(2)}</b></div>
          <div>GST 18 %  : <b>₹{gst.toFixed(2)}</b></div>
          <div style={{fontSize:18,marginTop:4}}>Grand Total: <b>₹{grand.toFixed(2)}</b></div>
        </div>

        {/* Payment status */}
        <label>
          Payment&nbsp;Status
          <select value={status} onChange={e=>setStatus(e.target.value as any)} style={input}>
            <option value="PENDING">Pending</option>
            <option value="PAID">Paid</option>
          </select>
        </label>

        <button type="submit" style={btn}>Save Bill</button>
      </form>
    </div>
  );
}

/* 🔹 tiny inline styles */
const input:React.CSSProperties = {
  padding:10,border:'1px solid #cbd5e0',borderRadius:6,width:'100%'
};
const btn :React.CSSProperties = {
  padding:12,background:'#3b82f6',color:'#fff',border:'none',
  borderRadius:6,fontWeight:600,cursor:'pointer'
};
const btnGreen  = {...btn,background:'#16a34a'};
const btnSmall  = {padding:'4px 8px',border:'none',borderRadius:4,
                   background:'#ef4444',color:'#fff',cursor:'pointer'};
const th:React.CSSProperties  = {padding:8,textAlign:'left',borderBottom:'1px solid #e2e8f0'};
const td:React.CSSProperties  = {padding:8,borderBottom:'1px solid #f1f5f9'};
