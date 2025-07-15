import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';
import toast from 'react-hot-toast';

export default function EditService() {
  const navigate          = useNavigate();
  const { serviceId }     = useParams();              //  ←  URL param
  const role   = localStorage.getItem('role');
  const token  = localStorage.getItem('token');

  /* ───── local form state ───── */
  const [form, setForm] = useState({
    name: '', description: '', price: '', duration: '', category: '',
  });
  const [loading, setLoading] = useState(true);

  /* ───── guard + fetch once ───── */
  useEffect(() => {
    if (role !== 'ADMIN') {
      toast.error('Unauthorized'); navigate('/dashboard'); return;
    }
    if (!serviceId) {
      toast.error('Missing service ID'); navigate('/services'); return;
    }

    (async () => {
      try {
        const { data } = await axios.get(
          `http://localhost:7070/api/salon/service/${serviceId}`,   // ✅ correct path
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setForm({
          name: data.name ?? '',
          description: data.description ?? '',
          price:  (data.price ?? '').toString(),
          duration:(data.duration ?? '').toString(),
          category:data.category ?? '',
        });
      } catch (err: any) {
        toast.error(err?.response?.data || 'Failed to fetch service');
        navigate('/services');
      } finally {
        setLoading(false);
      }
    })();
  }, [role, serviceId, token, navigate]);

  /* ───── helpers ───── */
  const handleChange = (e: React.ChangeEvent<HTMLInputElement|HTMLSelectElement>) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await axios.put(
        `http://localhost:7070/api/admin/service/update/${serviceId}`, // ✅ update path
        {
          ...form,
          price:    parseFloat(form.price),
          duration: parseInt(form.duration, 10),
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      toast.success('Service updated!');
      navigate('/services');
    } catch (err: any) {
      toast.error(err?.response?.data || 'Failed to update service');
    }
  };

  /* ───── UI ───── */
  const input: React.CSSProperties = {
    width:'100%',padding:'10px 12px',borderRadius:8,border:'1px solid #cbd5e0',fontSize:14,
  };
  const btn: React.CSSProperties = {
    width:'100%',padding:'12px 16px',border:'none',borderRadius:8,
    background:'#4f46e5',color:'#fff',fontWeight:600,cursor:'pointer',
  };

  if (loading) return <p style={{textAlign:'center',marginTop:40}}>Loading…</p>;

  return (
    <div style={{padding:'2rem',maxWidth:480,margin:'auto'}}>
      <h2 style={{fontSize:24,fontWeight:700,marginBottom:'1.5rem'}}>✏️ Edit Service</h2>

      <form onSubmit={handleSubmit} style={{display:'grid',gap:16}}>
        <input name="name"        value={form.name}        onChange={handleChange} placeholder="Service Name" required style={input}/>
        <input name="description" value={form.description} onChange={handleChange} placeholder="Description"   required style={input}/>
        <input name="price"       value={form.price}       onChange={handleChange} type="number" min="0" step="0.01" placeholder="Price" required style={input}/>
        <input name="duration"    value={form.duration}    onChange={handleChange} type="number" min="1" placeholder="Duration (minutes)" required style={input}/>
        <select name="category"   value={form.category}    onChange={handleChange} required style={input}>
          <option value="">-- Select Category --</option>
          <option value="HAIR">Hair</option><option value="SKIN">Skin</option><option value="NAIL">Nail</option>
        </select>

        <button type="submit" style={btn}>Update Service</button>
      </form>
    </div>
  );
}
