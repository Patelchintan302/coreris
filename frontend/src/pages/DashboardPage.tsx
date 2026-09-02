import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { appointmentService } from '../api/appointmentService';
import { patientService } from '../api/patientService';
import { AppointmentDto } from '../api/types';
import { CardSkeleton, LoadingSkeleton } from '../components/common/LoadingSkeleton';
import {
  Calendar,
  Users,
  Scan,
  FileCheck2,
  ArrowRight,
  Clock,
  CheckCircle2,
  XCircle,
  PlusCircle,
  FileText,
} from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const { user, hasRole } = useAuth();
  const [appointments, setAppointments] = useState<AppointmentDto[]>([]);
  const [patientCount, setPatientCount] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [appts, patients] = await Promise.all([
          appointmentService.getAll().catch(() => []),
          patientService.getAll().catch(() => []),
        ]);
        setAppointments(Array.isArray(appts) ? appts : []);
        setPatientCount(Array.isArray(patients) ? patients.length : 0);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const safeAppointments = Array.isArray(appointments) ? appointments : [];
  const totalAppointments = safeAppointments.length;
  const bookedCount = safeAppointments.filter((a) => a.status === 'BOOKED').length;
  const completedCount = safeAppointments.filter((a) => a.status === 'COMPLETED').length;

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'BOOKED':
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-amber-500/10 text-amber-400 border border-amber-500/20">
            <Clock className="w-3 h-3" /> Booked / Pending Scan
          </span>
        );
      case 'COMPLETED':
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
            <CheckCircle2 className="w-3 h-3" /> Completed Report
          </span>
        );
      case 'CANCELLED':
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-rose-500/10 text-rose-400 border border-rose-500/20">
            <XCircle className="w-3 h-3" /> Cancelled
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-slate-800 text-slate-300 border border-slate-700">
            {status}
          </span>
        );
    }
  };

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-blue-900/60 via-slate-900 to-slate-900 border border-blue-800/40 p-6 md:p-8 shadow-2xl">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-blue-500/10 border border-blue-500/20 text-blue-300 text-xs font-semibold mb-3">
              Role: <span className="text-white uppercase">{user?.role}</span>
            </div>
            <h1 className="text-2xl md:text-3xl font-bold text-white tracking-tight">
              Clinical Workspace Dashboard
            </h1>
            <p className="text-sm text-slate-400 mt-1 max-w-xl">
              Welcome, <span className="text-slate-200 font-medium">{user?.username}</span>. Monitor patient intake, imaging queues, and diagnostic reports in real time.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            {hasRole(['RECEPTIONIST', 'ADMIN']) && (
              <Link
                to="/appointments"
                className="inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold rounded-xl shadow-lg shadow-blue-500/20 transition-all"
              >
                <PlusCircle className="w-4 h-4" />
                <span>Book Appointment</span>
              </Link>
            )}
            {hasRole(['TECHNICIAN', 'ADMIN']) && (
              <Link
                to="/scans"
                className="inline-flex items-center gap-2 px-4 py-2.5 bg-sky-600 hover:bg-sky-500 text-white text-xs font-semibold rounded-xl shadow-lg shadow-sky-500/20 transition-all"
              >
                <Scan className="w-4 h-4" />
                <span>Scan Worklist</span>
              </Link>
            )}
            {hasRole(['RADIOLOGIST', 'ADMIN']) && (
              <Link
                to="/reports"
                className="inline-flex items-center gap-2 px-4 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold rounded-xl shadow-lg shadow-emerald-500/20 transition-all"
              >
                <FileText className="w-4 h-4" />
                <span>Diagnostic Queue</span>
              </Link>
            )}
          </div>
        </div>
      </div>

      {/* Metrics Cards */}
      {loading ? (
        <CardSkeleton count={4} />
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <div className="p-5 rounded-2xl bg-slate-900/60 border border-slate-800 shadow-sm flex items-center justify-between">
            <div>
              <div className="text-xs font-medium text-slate-400 uppercase tracking-wider">
                Appointments
              </div>
              <div className="text-3xl font-extrabold text-white mt-1">{totalAppointments}</div>
              <div className="text-xs text-slate-500 mt-1">Total in system</div>
            </div>
            <div className="p-3 bg-blue-500/10 text-blue-400 rounded-2xl border border-blue-500/20">
              <Calendar className="w-6 h-6" />
            </div>
          </div>

          <div className="p-5 rounded-2xl bg-slate-900/60 border border-slate-800 shadow-sm flex items-center justify-between">
            <div>
              <div className="text-xs font-medium text-slate-400 uppercase tracking-wider">
                Scan Queue
              </div>
              <div className="text-3xl font-extrabold text-amber-400 mt-1">{bookedCount}</div>
              <div className="text-xs text-slate-500 mt-1">Pending scan upload</div>
            </div>
            <div className="p-3 bg-amber-500/10 text-amber-400 rounded-2xl border border-amber-500/20">
              <Scan className="w-6 h-6" />
            </div>
          </div>

          <div className="p-5 rounded-2xl bg-slate-900/60 border border-slate-800 shadow-sm flex items-center justify-between">
            <div>
              <div className="text-xs font-medium text-slate-400 uppercase tracking-wider">
                Completed Reports
              </div>
              <div className="text-3xl font-extrabold text-emerald-400 mt-1">{completedCount}</div>
              <div className="text-xs text-slate-500 mt-1">PDFs generated</div>
            </div>
            <div className="p-3 bg-emerald-500/10 text-emerald-400 rounded-2xl border border-emerald-500/20">
              <FileCheck2 className="w-6 h-6" />
            </div>
          </div>

          <div className="p-5 rounded-2xl bg-slate-900/60 border border-slate-800 shadow-sm flex items-center justify-between">
            <div>
              <div className="text-xs font-medium text-slate-400 uppercase tracking-wider">
                Registered Patients
              </div>
              <div className="text-3xl font-extrabold text-sky-400 mt-1">{patientCount}</div>
              <div className="text-xs text-slate-500 mt-1">Master records</div>
            </div>
            <div className="p-3 bg-sky-500/10 text-sky-400 rounded-2xl border border-sky-500/20">
              <Users className="w-6 h-6" />
            </div>
          </div>
        </div>
      )}

      {/* Recent Activity Table */}
      <div className="rounded-2xl bg-slate-900/60 border border-slate-800 overflow-hidden shadow-sm">
        <div className="flex items-center justify-between p-5 border-b border-slate-800">
          <div>
            <h2 className="text-base font-semibold text-white">Recent Appointments</h2>
            <p className="text-xs text-slate-400 mt-0.5">Overview of active imaging sessions</p>
          </div>
          <Link
            to="/appointments"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-blue-400 hover:text-blue-300 transition-colors"
          >
            <span>View All</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        {loading ? (
          <LoadingSkeleton rows={4} />
        ) : safeAppointments.length === 0 ? (
          <div className="p-8 text-center text-sm text-slate-500">No appointments recorded yet.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/50 text-slate-400 text-xs uppercase tracking-wider border-b border-slate-800/80">
                <tr>
                  <th className="px-5 py-3.5">ID</th>
                  <th className="px-5 py-3.5">Patient</th>
                  <th className="px-5 py-3.5">Scan Modality</th>
                  <th className="px-5 py-3.5">Scheduled Date & Time</th>
                  <th className="px-5 py-3.5">Workflow Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 text-slate-300">
                {safeAppointments.slice(0, 6).map((appt) => (
                  <tr key={appt.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-5 py-4 font-mono text-xs text-slate-400">#{appt.id}</td>
                    <td className="px-5 py-4 font-medium text-white">
                      {appt.patient?.name || `Patient #${appt.patient?.id || 'N/A'}`}
                    </td>
                    <td className="px-5 py-4">
                      <span className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-slate-800 text-slate-200 border border-slate-700/60">
                        {appt.scanType}
                      </span>
                    </td>
                    <td className="px-5 py-4 text-xs text-slate-400">
                      {new Date(appt.appointmentTime).toLocaleString(undefined, {
                        dateStyle: 'medium',
                        timeStyle: 'short',
                      })}
                    </td>
                    <td className="px-5 py-4">{getStatusBadge(appt.status)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
