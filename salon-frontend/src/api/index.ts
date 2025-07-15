import React, { useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import { getCustomerAppointments, Appointment } from "../api";

export default function MyAppointments() {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      setError("You are not logged in.");
      setLoading(false);
      return;
    }

    let uid: number | undefined;
    try {
      ({ uid } = jwtDecode<{ uid: number }>(token));
    } catch (err) {
      setError("Invalid token.");
      setLoading(false);
      return;
    }

    getCustomerAppointments(uid)
      .then((res) => {
        setAppointments(res.data || []);
      })
      .catch((err) => {
        console.error("Error fetching appointments:", err);
        setError("Could not fetch appointments.");
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Loading appointments…</p>;
  if (error) return <p style={{ color: "red" }}>{error}</p>;
  if (appointments.length === 0) return <p>No appointments found.</p>;

  return (
    <section>
      <h2 style={{ marginBottom: "1rem" }}>My Appointments</h2>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {appointments.map((a) => (
          <li
            key={a.id}
            style={{
              marginBottom: ".75rem",
              padding: ".75rem 1rem",
              border: "1px solid #eee",
              borderRadius: "6px",
              background: "#f9f9f9",
            }}
          >
            <strong>{new Date(a.dateTime).toLocaleString()}</strong> —{" "}
            {a.serviceName} with {a.staffName}
          </li>
        ))}
      </ul>
    </section>
  );
}
