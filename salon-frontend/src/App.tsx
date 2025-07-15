import { Routes, Route, Navigate } from 'react-router-dom';
import PrivateRoute from './routes/PrivateRoute';

/* ───── Bill Pages ───── */
import CreateBill from './pages/CreateBill';
import BillsList from './pages/BillsList';

/* ───── Public Pages ───── */
import Login        from './pages/Login';
import Register     from './pages/Register';
import Services     from './pages/Services';
import TailwindTest from './pages/TailwindTest';

/* ───── Core Features ───── */
import AddService      from './pages/AddService';
import EditService     from './pages/EditService';
import MyAppointments  from './pages/MyAppointments';
import BookAppointment from './pages/BookAppointment';

/* ───── Dashboards ───── */
import AdminDashboard    from './pages/AdminDashboard';
import StaffDashboard    from './pages/StaffDashboard';
import CustomerDashboard from './pages/CustomerDashboard';

/* ───── Staff Management ───── */
import StaffPage  from './pages/StaffPage';
import AddStaff   from './pages/AddStaff';

/* ───── 404 Fallback ───── */
function NotFoundPage() {
  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex', flexDirection: 'column',
      justifyContent: 'center', alignItems: 'center',
      fontFamily: 'Segoe UI, sans-serif',
    }}>
      <h1 style={{ fontSize: 48, fontWeight: 700, marginBottom: 12 }}>404</h1>
      <p style={{ fontSize: 18, color: '#666' }}>
        Page not found. Please check the URL.
      </p>
      <a href="/login" style={{
        marginTop: 16, color: '#3b82f6', textDecoration: 'underline',
      }}>
        Go to Login
      </a>
    </div>
  );
}

/* ───── App Router ───── */
export default function App() {
  return (
    <Routes>
      {/* Redirect root to login */}
      <Route path="/" element={<Navigate to="/login" />} />

      {/* ───── Public Routes ───── */}
      <Route path="/login"    element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/services" element={<Services />} />
      <Route path="/test"     element={<TailwindTest />} />

      {/* ───── Protected Routes ───── */}
      <Route element={<PrivateRoute />}>
        {/* Dashboards */}
        <Route path="/admin-dashboard"    element={<AdminDashboard />} />
        <Route path="/staff-dashboard"    element={<StaffDashboard />} />
        <Route path="/customer-dashboard" element={<CustomerDashboard />} />

        {/* Appointments / Services */}
        <Route path="/appointments"         element={<MyAppointments />} />
        <Route path="/my-appointments"      element={<MyAppointments />} />
        <Route path="/book"                 element={<BookAppointment />} />
        <Route path="/add-service"          element={<AddService />} />
        <Route path="/edit-service/:serviceId" element={<EditService />} />

        {/* Staff Management */}
        <Route path="/staff-page"           element={<StaffPage />} />
        <Route path="/add-staff"            element={<AddStaff />} />

        {/* ───── Protected Billing Routes ───── */}
        <Route path="/bills/create"         element={<CreateBill />} />
        <Route path="/bills"                element={<BillsList />} />
      </Route>

      {/* 404 fallback */}
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
