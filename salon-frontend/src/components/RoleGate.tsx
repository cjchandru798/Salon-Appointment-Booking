import React, { ReactNode } from 'react';

/** Renders children only if the user’s role matches one of the allowed ones */
export default function RoleGate({
  allow,
  children,
}: {
  allow: ('ADMIN' | 'STAFF' | 'CUSTOMER')[];
  children: ReactNode;
}) {
  const role = (localStorage.getItem('role') ?? '').toUpperCase();
  return allow.includes(role as any) ? <>{children}</> : null;
}
