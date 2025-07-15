import React, { useEffect, useState } from 'react';
import API from '../api/axios';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';

interface Service {
  serviceId: number;
  name: string;
  description: string;
  price: number;
  duration: number;
  category: string;
}

const Services = () => {
  const [services, setServices] = useState<Service[]>([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [filterCategory, setFilterCategory] = useState('');
  const navigate = useNavigate();

  const itemsPerPage = 4;

  useEffect(() => {
    fetchServices();
  }, []);

  const fetchServices = () => {
    setLoading(true);
    API.get('/api/salon/service/all')
      .then((res) => setServices(res.data))
      .catch(() => toast.error('Failed to load services'))
      .finally(() => setLoading(false));
  };

  const handleDelete = (id: number) => {
    const confirm = window.confirm('Are you sure you want to delete this service?');
    if (!confirm) return;

    API.delete(`/api/admin/service/delete/${id}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })
      .then(() => {
        toast.success('Service deleted');
        fetchServices();
      })
      .catch(() => toast.error('Failed to delete service'));
  };

  const filtered = filterCategory
    ? services.filter((s) => s.category === filterCategory)
    : services;

  const paginated = filtered.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const totalPages = Math.ceil(filtered.length / itemsPerPage);

  return (
    <div
      style={{
        padding: '2rem',
        fontFamily: 'Segoe UI',
        background: '#f9fafb',
        minHeight: '100vh',
      }}
    >
      <h2
        style={{
          fontSize: 28,
          fontWeight: 700,
          marginBottom: '1.5rem',
          textAlign: 'center',
        }}
      >
        💇‍♀️ Our Services
      </h2>

      <div
        style={{
          marginBottom: '1rem',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '1rem',
        }}
      >
        <select
          value={filterCategory}
          onChange={(e) => setFilterCategory(e.target.value)}
          style={{
            padding: '0.5rem',
            fontSize: '1rem',
            borderRadius: '6px',
            border: '1px solid #ccc',
          }}
        >
          <option value="">All Categories</option>
          <option value="HAIR">Hair</option>
          <option value="SKIN">Skin</option>
          <option value="NAIL">Nail</option>
        </select>

        <button
          onClick={() => navigate('/add-service')}
          style={{
            padding: '0.5rem 1rem',
            backgroundColor: '#4f46e5',
            color: '#fff',
            borderRadius: '6px',
            border: 'none',
            cursor: 'pointer',
          }}
        >
          ➕ Add Service
        </button>
      </div>

      {loading ? (
        <p style={{ textAlign: 'center' }}>Loading...</p>
      ) : paginated.length === 0 ? (
        <p style={{ textAlign: 'center' }}>No services available</p>
      ) : (
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
            gap: '1rem',
          }}
        >
          {paginated.map((s) => (
            <div
              key={s.serviceId}
              style={{
                background: '#fff',
                borderRadius: '10px',
                padding: '1.5rem',
                boxShadow: '0 2px 12px rgba(0,0,0,0.06)',
              }}
            >
              <h3 style={{ fontSize: '1.2rem', fontWeight: 600 }}>{s.name}</h3>
              <p style={{ margin: '0.5rem 0', color: '#555' }}>{s.description}</p>
              <p>💰 ₹{s.price}</p>
              <p>🕒 {s.duration} min</p>
              <p>📂 {s.category}</p>

              <div style={{ marginTop: '1rem', display: 'flex', gap: '0.5rem' }}>
                <button
                  onClick={() => navigate(`/edit-service/${s.serviceId}`)}
                  style={{
                    flex: 1,
                    backgroundColor: '#3b82f6',
                    color: '#fff',
                    padding: '0.4rem 0.6rem',
                    borderRadius: '5px',
                    border: 'none',
                    cursor: 'pointer',
                  }}
                >
                  ✏️ Edit
                </button>
                <button
                  onClick={() => handleDelete(s.serviceId)}
                  style={{
                    flex: 1,
                    backgroundColor: '#ef4444',
                    color: '#fff',
                    padding: '0.4rem 0.6rem',
                    borderRadius: '5px',
                    border: 'none',
                    cursor: 'pointer',
                  }}
                >
                  🗑️ Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div style={{ marginTop: '2rem', textAlign: 'center' }}>
          {Array.from({ length: totalPages }).map((_, i) => (
            <button
              key={i}
              onClick={() => setCurrentPage(i + 1)}
              style={{
                margin: '0 5px',
                padding: '6px 12px',
                fontSize: '1rem',
                borderRadius: '5px',
                border: 'none',
                background: currentPage === i + 1 ? '#4f46e5' : '#e5e7eb',
                color: currentPage === i + 1 ? '#fff' : '#000',
              }}
            >
              {i + 1}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

export default Services;
