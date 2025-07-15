import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API, { bookAppointment } from '../api/axios';
import toast from 'react-hot-toast';

interface Service {
  serviceId: number;
  name: string;
  duration?: number;
}

export default function BookAppointment() {
  const nav = useNavigate();
  const token = localStorage.getItem('token');
  const role  = localStorage.getItem('role')?.toUpperCase();
  const userId = Number(localStorage.getItem('userId'));

  useEffect(() => {
    if (!token) {
      toast.error('Please log in first');
      nav('/login');
    } else if (role !== 'CUSTOMER') {
      toast.error('Only customers can book appointments');
      nav(-1);
    }
  }, [token, role, nav]);

  const [services, setServices] = useState<Service[]>([]);
  const [form, setForm] = useState({
    serviceId: '',
    date: '',
    startTime: '',
    endTime: '',
  });

  useEffect(() => {
    API.get<Service[]>('/api/salon/service/all')
      .then(res => setServices(res.data))
      .catch(() => toast.error('Failed to fetch services'));
  }, []);

  useEffect(() => {
    const sel = services.find(s => s.serviceId === +form.serviceId);
    if (!sel?.duration || !form.startTime) return;

    const [h, m] = form.startTime.split(':').map(Number);
    const endDate = new Date(0, 0, 0, h, m + sel.duration);
    const hh = String(endDate.getHours()).padStart(2, '0');
    const mm = String(endDate.getMinutes()).padStart(2, '0');
    setForm(f => ({ ...f, endTime: `${hh}:${mm}` }));
  }, [form.serviceId, form.startTime, services]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!form.serviceId) return toast.error('Please select a service');
    if (!form.date)      return toast.error('Choose a date');
    if (!form.startTime) return toast.error('Choose a start time');
    if (!form.endTime)   return toast.error('Choose an end time');

    if (
      new Date(`1970-01-01T${form.endTime}`) <=
      new Date(`1970-01-01T${form.startTime}`)
    ) {
      return toast.error('End time must be after start time');
    }

    try {
      await bookAppointment(
        {
          services:  [ { serviceId: +form.serviceId } ],   // ✅ fix: array of services
          customers: { customerId: userId },
                                 // or remove if backend auto-assigns
          appointmentDate: form.date,
          startTime: form.startTime,
          endTime:   form.endTime,
        },
        token!
      );
      toast.success('Appointment booked!');
      nav('/my-appointments');
    } catch (err: any) {
      console.error(err.response?.data || err.message);
      toast.error(err.response?.data?.message ?? 'Booking failed');
    }
  };

  const today = new Date().toISOString().split('T')[0];

  return (
    <div style={wrapper}>
      <h2 style={h2}>📅 Book an Appointment</h2>

      <form onSubmit={handleSubmit} style={formGrid}>
        <select
          name="serviceId"
          value={form.serviceId}
          onChange={handleChange}
          required
          style={input}
        >
          <option value="">Select service</option>
          {services.map(s => (
            <option key={s.serviceId} value={s.serviceId}>
              {s.name} {s.duration ? `(${s.duration} min)` : ''}
            </option>
          ))}
        </select>

        <input
          type="date"
          name="date"
          min={today}
          value={form.date}
          onChange={handleChange}
          required
          style={input}
        />

        <input
          type="time"
          name="startTime"
          value={form.startTime}
          onChange={handleChange}
          required
          style={input}
        />

        <input
          type="time"
          name="endTime"
          value={form.endTime}
          onChange={handleChange}
          required
          style={input}
        />

        <button type="submit" style={btnPrimary}>
          ✅ Book
        </button>
      </form>
    </div>
  );
}

/* INLINE STYLES */
const wrapper:React.CSSProperties = {
  maxWidth:480,margin:'2rem auto',padding:24,
  background:'#fff',borderRadius:10,
  boxShadow:'0 4px 12px rgba(0,0,0,0.1)',
  fontFamily:'Segoe UI, sans-serif',
};
const h2   = { textAlign:'center',marginBottom:24,fontSize:22 };
const formGrid = { display:'flex',flexDirection:'column',gap:12 };
const input = {
  padding:12,fontSize:16,border:'1px solid #ccc',borderRadius:6,
};
const btnPrimary = {
  padding:12,border:'none',borderRadius:6,
  background:'#4f46e5',color:'#fff',fontSize:16,cursor:'pointer',
};
