import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import { AppLayout } from './components/layout/AppLayout';
import { ProtectedRoute } from './components/layout/ProtectedRoute';

import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { PatientsPage } from './pages/PatientsPage';
import { AppointmentsPage } from './pages/AppointmentsPage';
import { TechnicianQueuePage } from './pages/TechnicianQueuePage';
import { RadiologistQueuePage } from './pages/RadiologistQueuePage';
import { UsersPage } from './pages/UsersPage';
import { NotFoundPage } from './pages/NotFoundPage';

export function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
          <Routes>
            {/* Public Login Route */}
            <Route path="/login" element={<LoginPage />} />

            {/* Authenticated Workspace Routes */}
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <AppLayout />
                </ProtectedRoute>
              }
            >
              <Route index element={<Navigate to="/dashboard" replace />} />
              <Route path="dashboard" element={<DashboardPage />} />

              {/* Patient Management (Receptionist & Admin) */}
              <Route
                path="patients"
                element={
                  <ProtectedRoute allowedRoles={['RECEPTIONIST', 'ADMIN']}>
                    <PatientsPage />
                  </ProtectedRoute>
                }
              />

              {/* Appointment Scheduling (All Authenticated Staff) */}
              <Route path="appointments" element={<AppointmentsPage />} />

              {/* Technician Scan Worklist (Technician & Admin) */}
              <Route
                path="scans"
                element={
                  <ProtectedRoute allowedRoles={['TECHNICIAN', 'ADMIN']}>
                    <TechnicianQueuePage />
                  </ProtectedRoute>
                }
              />

              {/* Radiologist Diagnostic Queue (Radiologist & Admin) */}
              <Route
                path="reports"
                element={
                  <ProtectedRoute allowedRoles={['RADIOLOGIST', 'ADMIN']}>
                    <RadiologistQueuePage />
                  </ProtectedRoute>
                }
              />

              {/* Administrator User Management (Admin Only) */}
              <Route
                path="users"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <UsersPage />
                  </ProtectedRoute>
                }
              />

              {/* 404 Route */}
              <Route path="*" element={<NotFoundPage />} />
            </Route>
          </Routes>
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
