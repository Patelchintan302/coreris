import React, { useEffect, useState } from 'react';
import { appointmentService } from '../api/appointmentService';
import { scanService } from '../api/scanService';
import { reportService } from '../api/reportService';
import { AppointmentDto, ScanResultDto, ReportDto } from '../api/types';
import { Modal } from '../components/common/Modal';
import { LoadingSkeleton } from '../components/common/LoadingSkeleton';
import { EmptyState } from '../components/common/EmptyState';
import { useToast } from '../context/ToastContext';
import { getErrorMessage } from '../api/client';
import {
  FileText,
  Stethoscope,
  ExternalLink,
  CheckCircle2,
  Clock,
  Search,
  FileCheck2,
  Eye,
  Edit3,
} from 'lucide-react';

export const RadiologistQueuePage: React.FC = () => {
  const [appointments, setAppointments] = useState<AppointmentDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [searchTerm, setSearchTerm] = useState('');

  // Reporting Modal
  const [isReportModalOpen, setIsReportModalOpen] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState<AppointmentDto | null>(null);
  const [scanResult, setScanResult] = useState<ScanResultDto | null>(null);
  const [existingReport, setExistingReport] = useState<ReportDto | null>(null);
  const [modalLoading, setModalLoading] = useState(false);

  // Form State
  const [finding, setFinding] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const toast = useToast();

  const fetchAppointments = async () => {
    try {
      setLoading(true);
      const data = await appointmentService.getAll();
      setAppointments(Array.isArray(data) ? data : []);
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAppointments();
  }, []);

  const handleOpenReportModal = async (appt: AppointmentDto) => {
    setSelectedAppointment(appt);
    setIsReportModalOpen(true);
    setFinding('');
    setExistingReport(null);
    setScanResult(null);

    try {
      setModalLoading(true);
      // Fetch scan result and existing report in parallel
      const [scan, report] = await Promise.all([
        scanService.getByAppointmentId(appt.id).catch(() => null),
        reportService.getByAppointmentId(appt.id).catch(() => null),
      ]);
      setScanResult(scan);
      setExistingReport(report);
      if (report) {
        setFinding(report.finding);
      }
    } finally {
      setModalLoading(false);
    }
  };

  const handleSubmitReport = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAppointment) return;

    if (!finding.trim()) {
      toast.error('Please enter diagnostic findings before generating the report.');
      return;
    }

    setSubmitting(true);
    try {
      let saved: ReportDto;
      if (existingReport) {
        saved = await reportService.updateReport(selectedAppointment.id, { finding });
        toast.success(`Updated diagnostic report for Appointment #${selectedAppointment.id}!`);
      } else {
        saved = await reportService.createReport(selectedAppointment.id, { finding });
        toast.success(`Generated official PDF report for Appointment #${selectedAppointment.id}!`);
      }
      setExistingReport(saved);
      setIsReportModalOpen(false);
      fetchAppointments();

      // Open generated PDF in new tab if available
      if (saved.pdfUrl) {
        window.open(reportService.resolvePdfUrl(saved.pdfUrl), '_blank');
      }
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const filteredAppointments = (Array.isArray(appointments) ? appointments : []).filter((appt) => {
    const term = searchTerm.toLowerCase();
    return (
      appt.patient?.name?.toLowerCase().includes(term) ||
      String(appt.id).includes(term) ||
      appt.scanType.toLowerCase().includes(term)
    );
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Radiologist Diagnostic Queue</h1>
          <p className="text-sm text-slate-400 mt-1">
            Review scan captures, formulate findings, and generate official OpenPDF diagnostic reports.
          </p>
        </div>
        <div className="flex items-center gap-2 text-xs px-3 py-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-300">
          <Stethoscope className="w-4 h-4" />
          <span>Diagnostic Interpretation Station</span>
        </div>
      </div>

      {/* Search */}
      <div className="relative">
        <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" />
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Filter queue by patient name, scan type, or ID..."
          className="w-full pl-10 pr-4 py-2 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
        />
      </div>

      {/* Table */}
      <div className="rounded-2xl bg-slate-900/60 border border-slate-800 overflow-hidden shadow-sm">
        {loading ? (
          <LoadingSkeleton rows={5} />
        ) : filteredAppointments.length === 0 ? (
          <EmptyState
            title="Diagnostic queue empty"
            description="All diagnostic reports have been completed."
            icon={<FileCheck2 className="w-8 h-8" />}
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/50 text-slate-400 text-xs uppercase tracking-wider border-b border-slate-800/80">
                <tr>
                  <th className="px-5 py-3.5">Appointment</th>
                  <th className="px-5 py-3.5">Patient Details</th>
                  <th className="px-5 py-3.5">Scan Modality</th>
                  <th className="px-5 py-3.5">Scheduled Slot</th>
                  <th className="px-5 py-3.5">Diagnostic Status</th>
                  <th className="px-5 py-3.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 text-slate-300">
                {filteredAppointments.map((appt) => (
                  <tr key={appt.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-5 py-4 font-mono text-xs text-slate-400">#{appt.id}</td>
                    <td className="px-5 py-4">
                      <div className="font-semibold text-white">{appt.patient?.name}</div>
                      <div className="text-xs text-slate-500">Contact: {appt.patient?.mobileNo}</div>
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
                    <td className="px-5 py-4">
                      {appt.status === 'COMPLETED' ? (
                        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                          <CheckCircle2 className="w-3 h-3" /> Report Finalized
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-amber-500/10 text-amber-400 border border-amber-500/20">
                          <Clock className="w-3 h-3" /> Awaiting Report
                        </span>
                      )}
                    </td>
                    <td className="px-5 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => handleOpenReportModal(appt)}
                          className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-medium transition-all shadow-sm shadow-emerald-500/20"
                        >
                          {appt.status === 'COMPLETED' ? (
                            <>
                              <Edit3 className="w-3.5 h-3.5" />
                              <span>Edit / View Report</span>
                            </>
                          ) : (
                            <>
                              <FileText className="w-3.5 h-3.5" />
                              <span>Write Report</span>
                            </>
                          )}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Side-by-Side Diagnostic Reporting Modal */}
      <Modal
        isOpen={isReportModalOpen}
        onClose={() => setIsReportModalOpen(false)}
        title={`Diagnostic Reporting: Appointment #${selectedAppointment?.id}`}
        maxWidth="3xl"
      >
        {modalLoading ? (
          <LoadingSkeleton rows={4} />
        ) : (
          <form onSubmit={handleSubmitReport} className="space-y-6">
            {/* Top Patient Summary */}
            <div className="p-4 bg-slate-950 rounded-xl border border-slate-800 grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs text-slate-300">
              <div>
                <span className="text-slate-500 block">Patient</span>
                <strong className="text-white">{selectedAppointment?.patient?.name}</strong>
              </div>
              <div>
                <span className="text-slate-500 block">Scan Modality</span>
                <strong className="text-white">{selectedAppointment?.scanType}</strong>
              </div>
              <div>
                <span className="text-slate-500 block">Gender / Age</span>
                <strong className="text-white">
                  {selectedAppointment?.patient?.gender} ({selectedAppointment?.patient?.dob})
                </strong>
              </div>
              <div>
                <span className="text-slate-500 block">Blood Group</span>
                <strong className="text-white">
                  {selectedAppointment?.patient?.bloodGroup?.replace('_', ' ')}
                </strong>
              </div>
            </div>

            {/* Split Screen: Scan Image on Left, Finding Form on Right */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Left: Scan Image & Acquisition */}
              <div className="space-y-3">
                <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
                  Technician Imaging Capture
                </label>
                <div className="rounded-2xl overflow-hidden border border-slate-800 bg-black min-h-[220px] flex items-center justify-center p-2">
                  {scanResult?.imageUrl ? (
                    <img
                      src={scanService.resolveImageUrl(scanResult.imageUrl)}
                      alt="Scan Inspection"
                      className="max-h-72 object-contain rounded-lg"
                      onError={(e) => {
                        (e.target as HTMLImageElement).src = 'https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=600&q=80';
                      }}
                    />
                  ) : (
                    <div className="text-center p-6 text-xs text-slate-500">
                      No scan image uploaded yet by technician.
                    </div>
                  )}
                </div>

                {scanResult && (
                  <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 text-xs text-slate-300">
                    <span className="text-slate-500 block text-[10px] uppercase font-semibold">
                      Technician Protocol Notes:
                    </span>
                    {scanResult.scanDetails}
                  </div>
                )}
              </div>

              {/* Right: Diagnostic Findings */}
              <div className="space-y-3 flex flex-col justify-between">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">
                    Diagnostic Findings & Impression *
                  </label>
                  <textarea
                    required
                    rows={8}
                    value={finding}
                    onChange={(e) => setFinding(e.target.value)}
                    placeholder="Enter diagnostic conclusions, tissue observations, and clinical impression (e.g. Gallbladder wall is intact. No evidence of acute cholecystitis or biliary dilatation)..."
                    className="w-full p-3.5 bg-slate-950 border border-slate-700/80 rounded-xl text-slate-100 placeholder-slate-500 text-sm focus:ring-2 focus:ring-emerald-500 outline-none leading-relaxed"
                  />
                  <p className="text-[11px] text-slate-500 mt-1.5">
                    Submitting this form automatically generates and stamps an official OpenPDF clinical document with clinic branding.
                  </p>
                </div>

                {existingReport?.pdfUrl && (
                  <div className="p-3 bg-emerald-950/30 border border-emerald-500/30 rounded-xl flex items-center justify-between">
                    <div className="text-xs text-emerald-300 font-medium">
                      Archival PDF document is ready:
                    </div>
                    <a
                      href={reportService.resolvePdfUrl(existingReport.pdfUrl)}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="inline-flex items-center gap-1.5 text-xs font-bold text-emerald-400 hover:text-emerald-300"
                    >
                      <ExternalLink className="w-3.5 h-3.5" />
                      <span>View PDF</span>
                    </a>
                  </div>
                )}
              </div>
            </div>

            {/* Modal Actions */}
            <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
              <button
                type="button"
                onClick={() => setIsReportModalOpen(false)}
                className="px-4 py-2 text-sm text-slate-400 hover:text-white bg-slate-800 hover:bg-slate-700 rounded-xl transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={submitting}
                className="px-5 py-2 text-sm font-semibold text-white bg-emerald-600 hover:bg-emerald-500 rounded-xl transition-colors disabled:opacity-50 flex items-center gap-2 shadow-lg shadow-emerald-600/20"
              >
                {submitting && (
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                )}
                <span>
                  {submitting
                    ? 'Compiling PDF...'
                    : existingReport
                    ? 'Update & Regenerate PDF'
                    : 'Finalize & Generate PDF'}
                </span>
              </button>
            </div>
          </form>
        )}
      </Modal>
    </div>
  );
};
