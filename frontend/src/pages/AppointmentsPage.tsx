import React, { useEffect, useState } from 'react';
import { appointmentService } from '../api/appointmentService';
import { patientService } from '../api/patientService';
import { scanService } from '../api/scanService';
import { reportService } from '../api/reportService';
import {
  AppointmentDto,
  AppointmentCreateDto,
  PatientDto,
  ScanType,
  StatusType,
  AppointmentHistoryDto,
} from '../api/types';
import { Modal } from '../components/common/Modal';
import { ConfirmDialog } from '../components/common/ConfirmDialog';
import { LoadingSkeleton } from '../components/common/LoadingSkeleton';
import { EmptyState } from '../components/common/EmptyState';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { getErrorMessage } from '../api/client';
import {
  Calendar,
  PlusCircle,
  Search,
  Filter,
  Ban,
  Eye,
  Clock,
  CheckCircle2,
  XCircle,
  FileText,
  Scan as ScanIcon,
  ExternalLink,
} from 'lucide-react';

const SCAN_TYPES: ScanType[] = [
  'XRAY_SCAN',
  'CT_SCAN',
  'MRI_SCAN',
  'ULTRASOUND_SCAN',
  'PET_SCAN',
];

export const AppointmentsPage: React.FC = () => {
  const { hasRole } = useAuth();
  const toast = useToast();

  const [appointments, setAppointments] = useState<AppointmentDto[]>([]);
  const [patients, setPatients] = useState<PatientDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  // Filters & Search
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [scanTypeFilter, setScanTypeFilter] = useState<string>('ALL');

  // Modals
  const [isBookOpen, setIsBookOpen] = useState(false);
  const [isCancelOpen, setIsCancelOpen] = useState(false);
  const [isDetailsOpen, setIsDetailsOpen] = useState(false);

  const [selectedAppointment, setSelectedAppointment] = useState<AppointmentDto | null>(null);
  const [appointmentHistory, setAppointmentHistory] = useState<AppointmentHistoryDto | null>(null);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // New Appointment Form
  const [newAppointment, setNewAppointment] = useState<AppointmentCreateDto>({
    patientId: 0,
    scanType: 'XRAY_SCAN',
    appointmentTime: '',
  });

  const fetchData = async () => {
    try {
      setLoading(true);
      const [appts, pts] = await Promise.all([
        appointmentService.getAll(),
        patientService.getAll().catch(() => []),
      ]);
      setAppointments(Array.isArray(appts) ? appts : []);
      setPatients(Array.isArray(pts) ? pts : []);
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleOpenBook = () => {
    // Default to tomorrow at 10:00 AM
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(10, 0, 0, 0);
    const formatted = tomorrow.toISOString().substring(0, 16);

    setNewAppointment({
      patientId: patients.length > 0 ? patients[0].id || 0 : 0,
      scanType: 'XRAY_SCAN',
      appointmentTime: formatted,
    });
    setIsBookOpen(true);
  };

  const handleCreateAppointment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newAppointment.patientId) {
      toast.error('Please select a valid patient.');
      return;
    }
    if (!newAppointment.appointmentTime) {
      toast.error('Please select an appointment time.');
      return;
    }

    setSubmitting(true);
    try {
      await appointmentService.create(newAppointment);
      toast.success('Appointment booked successfully!');
      setIsBookOpen(false);
      fetchData();
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handleOpenCancel = (appt: AppointmentDto) => {
    setSelectedAppointment(appt);
    setIsCancelOpen(true);
  };

  const handleConfirmCancel = async () => {
    if (!selectedAppointment) return;
    setSubmitting(true);
    try {
      await appointmentService.cancel(selectedAppointment.id);
      toast.success(`Appointment #${selectedAppointment.id} has been cancelled.`);
      setIsCancelOpen(false);
      fetchData();
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handleOpenDetails = async (appt: AppointmentDto) => {
    setSelectedAppointment(appt);
    setIsDetailsOpen(true);
    try {
      setDetailsLoading(true);
      const history = await appointmentService.getHistory(appt.id);
      setAppointmentHistory(history);
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setDetailsLoading(false);
    }
  };

  const filteredAppointments = (Array.isArray(appointments) ? appointments : []).filter((appt) => {
    const matchesSearch =
      appt.patient?.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      String(appt.id).includes(searchTerm);
    const matchesStatus = statusFilter === 'ALL' || appt.status === statusFilter;
    const matchesScanType = scanTypeFilter === 'ALL' || appt.scanType === scanTypeFilter;
    return matchesSearch && matchesStatus && matchesScanType;
  });

  const getStatusBadge = (status: StatusType) => {
    switch (status) {
      case 'BOOKED':
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-amber-500/10 text-amber-400 border border-amber-500/20">
            <Clock className="w-3 h-3" /> Booked
          </span>
        );
      case 'COMPLETED':
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
            <CheckCircle2 className="w-3 h-3" /> Completed
          </span>
        );
      case 'CANCELLED':
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-rose-500/10 text-rose-400 border border-rose-500/20">
            <XCircle className="w-3 h-3" /> Cancelled
          </span>
        );
      default:
        return <span className="px-2.5 py-1 rounded-full text-xs bg-slate-800 text-slate-300">{status}</span>;
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Appointment Center</h1>
          <p className="text-sm text-slate-400 mt-1">
            Track and coordinate diagnostic scan appointments across all hospital modalities.
          </p>
        </div>

        {hasRole(['RECEPTIONIST', 'ADMIN']) && (
          <button
            onClick={handleOpenBook}
            className="inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 hover:bg-blue-500 text-white text-sm font-medium rounded-xl shadow-lg shadow-blue-500/20 transition-all self-start sm:self-auto"
          >
            <PlusCircle className="w-4 h-4" />
            <span>Book Appointment</span>
          </button>
        )}
      </div>

      {/* Filter Controls */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
        <div className="relative sm:col-span-1">
          <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search by patient name or ID..."
            className="w-full pl-10 pr-4 py-2 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="w-full px-3.5 py-2 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="ALL">All Statuses</option>
            <option value="BOOKED">Booked (Pending Scan)</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>

        <div>
          <select
            value={scanTypeFilter}
            onChange={(e) => setScanTypeFilter(e.target.value)}
            className="w-full px-3.5 py-2 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="ALL">All Scan Modalities</option>
            {SCAN_TYPES.map((st) => (
              <option key={st} value={st}>
                {st}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Appointment Table */}
      <div className="rounded-2xl bg-slate-900/60 border border-slate-800 overflow-hidden shadow-sm">
        {loading ? (
          <LoadingSkeleton rows={5} />
        ) : filteredAppointments.length === 0 ? (
          <EmptyState
            title="No appointments match criteria"
            description="Adjust your search filters or book a new appointment."
            icon={<Calendar className="w-8 h-8" />}
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/50 text-slate-400 text-xs uppercase tracking-wider border-b border-slate-800/80">
                <tr>
                  <th className="px-5 py-3.5">ID</th>
                  <th className="px-5 py-3.5">Patient</th>
                  <th className="px-5 py-3.5">Scan Modality</th>
                  <th className="px-5 py-3.5">Scheduled Date & Time</th>
                  <th className="px-5 py-3.5">Status</th>
                  <th className="px-5 py-3.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 text-slate-300">
                {filteredAppointments.map((appt) => (
                  <tr key={appt.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-5 py-4 font-mono text-xs text-slate-500">#{appt.id}</td>
                    <td className="px-5 py-4 font-medium text-white">
                      {appt.patient?.name || `Patient #${appt.patient?.id}`}
                    </td>
                    <td className="px-5 py-4">
                      <span className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-slate-800 text-slate-200 border border-slate-700/60">
                        {appt.scanType}
                      </span>
                    </td>
                    <td className="px-5 py-4 text-xs text-slate-300">
                      {new Date(appt.appointmentTime).toLocaleString(undefined, {
                        dateStyle: 'medium',
                        timeStyle: 'short',
                      })}
                    </td>
                    <td className="px-5 py-4">{getStatusBadge(appt.status)}</td>
                    <td className="px-5 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => handleOpenDetails(appt)}
                          title="View Case Timeline"
                          className="p-2 text-slate-400 hover:text-sky-400 hover:bg-sky-500/10 rounded-lg transition-colors"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        {hasRole(['RECEPTIONIST', 'ADMIN']) && appt.status === 'BOOKED' && (
                          <button
                            onClick={() => handleOpenCancel(appt)}
                            title="Cancel Appointment"
                            className="p-2 text-slate-400 hover:text-rose-400 hover:bg-rose-500/10 rounded-lg transition-colors"
                          >
                            <Ban className="w-4 h-4" />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Book Appointment Modal */}
      <Modal
        isOpen={isBookOpen}
        onClose={() => setIsBookOpen(false)}
        title="Book New Imaging Appointment"
        maxWidth="md"
      >
        <form onSubmit={handleCreateAppointment} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Select Patient *</label>
            <select
              required
              value={newAppointment.patientId}
              onChange={(e) =>
                setNewAppointment({ ...newAppointment, patientId: Number(e.target.value) })
              }
              className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-slate-100 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
            >
              {patients.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name} (Mobile: {p.mobileNo})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Scan Modality *</label>
            <select
              value={newAppointment.scanType}
              onChange={(e) =>
                setNewAppointment({ ...newAppointment, scanType: e.target.value as ScanType })
              }
              className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-slate-100 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
            >
              {SCAN_TYPES.map((st) => (
                <option key={st} value={st}>
                  {st}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Date & Time *</label>
            <input
              type="datetime-local"
              required
              value={newAppointment.appointmentTime}
              onChange={(e) =>
                setNewAppointment({ ...newAppointment, appointmentTime: e.target.value })
              }
              className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-slate-100 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
            />
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
            <button
              type="button"
              onClick={() => setIsBookOpen(false)}
              className="px-4 py-2 text-sm text-slate-400 hover:text-white bg-slate-800 hover:bg-slate-700 rounded-xl transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="px-5 py-2 text-sm font-semibold text-white bg-blue-600 hover:bg-blue-500 rounded-xl transition-colors disabled:opacity-50"
            >
              {submitting ? 'Booking...' : 'Confirm Booking'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Appointment History / Timeline Modal */}
      <Modal
        isOpen={isDetailsOpen}
        onClose={() => setIsDetailsOpen(false)}
        title={`Imaging Case Timeline: #${selectedAppointment?.id}`}
        maxWidth="2xl"
      >
        {detailsLoading ? (
          <LoadingSkeleton rows={4} />
        ) : appointmentHistory ? (
          <div className="space-y-6">
            {/* Step 1: Patient & Booking */}
            <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-blue-400 uppercase tracking-wider">
                  Step 1: Patient Intake
                </span>
                {getStatusBadge(appointmentHistory.appointment.status)}
              </div>
              <div className="grid grid-cols-2 gap-2 text-xs text-slate-300 pt-1">
                <div>Patient: <span className="text-white font-medium">{appointmentHistory.appointment.patient?.name}</span></div>
                <div>Modality: <span className="text-white font-medium">{appointmentHistory.appointment.scanType}</span></div>
                <div>Scheduled: {new Date(appointmentHistory.appointment.appointmentTime).toLocaleString()}</div>
                <div>Contact: {appointmentHistory.appointment.patient?.mobileNo}</div>
              </div>
            </div>

            {/* Step 2: Scan Image */}
            <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-sky-400 uppercase tracking-wider flex items-center gap-1.5">
                  <ScanIcon className="w-3.5 h-3.5" /> Step 2: Imaging Capture
                </span>
                <span className="text-xs text-slate-500 font-mono">
                  {appointmentHistory.scanResult ? 'Captured' : 'Pending'}
                </span>
              </div>

              {appointmentHistory.scanResult ? (
                <div className="space-y-3">
                  <p className="text-xs text-slate-300">
                    <span className="text-slate-500">Notes:</span> {appointmentHistory.scanResult.scanDetails}
                  </p>
                  {appointmentHistory.scanResult.imageUrl && (
                    <div className="relative group max-h-48 overflow-hidden rounded-xl border border-slate-800 bg-black flex items-center justify-center">
                      <img
                        src={scanService.resolveImageUrl(appointmentHistory.scanResult.imageUrl)}
                        alt="Scan Result"
                        className="max-h-48 object-contain"
                        onError={(e) => {
                          (e.target as HTMLImageElement).src = 'https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=400&q=80';
                        }}
                      />
                    </div>
                  )}
                </div>
              ) : (
                <p className="text-xs text-slate-500 italic">Waiting for technician to upload imaging scans.</p>
              )}
            </div>

            {/* Step 3: Diagnostic Report & PDF */}
            <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-emerald-400 uppercase tracking-wider flex items-center gap-1.5">
                  <FileText className="w-3.5 h-3.5" /> Step 3: Diagnostic Report & PDF
                </span>
                <span className="text-xs text-slate-500 font-mono">
                  {appointmentHistory.report ? 'Report Completed' : 'Pending'}
                </span>
              </div>

              {appointmentHistory.report ? (
                <div className="space-y-3">
                  <div className="p-3 bg-slate-900 rounded-lg text-xs text-slate-200 border border-slate-800/80">
                    <div className="text-slate-400 text-[10px] font-semibold uppercase mb-1">Clinical Findings</div>
                    {appointmentHistory.report.finding}
                  </div>

                  {appointmentHistory.report.pdfUrl && (
                    <a
                      href={reportService.resolvePdfUrl(appointmentHistory.report.pdfUrl)}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="inline-flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-semibold shadow-md transition-all"
                    >
                      <ExternalLink className="w-3.5 h-3.5" />
                      <span>Open Official Clinical PDF Report</span>
                    </a>
                  )}
                </div>
              ) : (
                <p className="text-xs text-slate-500 italic">Waiting for radiologist diagnostic interpretation.</p>
              )}
            </div>
          </div>
        ) : null}
      </Modal>

      {/* Cancel Confirmation Dialog */}
      <ConfirmDialog
        isOpen={isCancelOpen}
        onClose={() => setIsCancelOpen(false)}
        onConfirm={handleConfirmCancel}
        title="Cancel Appointment"
        message={`Are you sure you want to cancel Appointment #${selectedAppointment?.id} for ${selectedAppointment?.patient?.name}?`}
        confirmText="Cancel Appointment"
        variant="danger"
        loading={submitting}
      />
    </div>
  );
};
