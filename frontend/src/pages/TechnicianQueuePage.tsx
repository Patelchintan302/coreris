import React, { useEffect, useState } from 'react';
import { appointmentService } from '../api/appointmentService';
import { scanService } from '../api/scanService';
import { AppointmentDto, ScanResultDto } from '../api/types';
import { Modal } from '../components/common/Modal';
import { LoadingSkeleton } from '../components/common/LoadingSkeleton';
import { EmptyState } from '../components/common/EmptyState';
import { useToast } from '../context/ToastContext';
import { getErrorMessage } from '../api/client';
import {
  Scan,
  Upload,
  Image as ImageIcon,
  CheckCircle2,
  Clock,
  Search,
  Eye,
  Calendar,
  AlertCircle,
} from 'lucide-react';

export const TechnicianQueuePage: React.FC = () => {
  const [appointments, setAppointments] = useState<AppointmentDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [searchTerm, setSearchTerm] = useState('');

  // Upload Modal State
  const [isUploadOpen, setIsUploadOpen] = useState(false);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState<AppointmentDto | null>(null);
  const [currentScan, setCurrentScan] = useState<ScanResultDto | null>(null);
  const [viewLoading, setViewLoading] = useState(false);

  // Form State
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [filePreview, setFilePreview] = useState<string | null>(null);
  const [scanDetails, setScanDetails] = useState('');
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

  const handleOpenUpload = async (appt: AppointmentDto) => {
    setSelectedAppointment(appt);
    setSelectedFile(null);
    setFilePreview(null);
    setScanDetails('');
    setIsUploadOpen(true);

    // If scan exists, prefetch to allow update
    try {
      const scan = await scanService.getByAppointmentId(appt.id);
      if (scan) {
        setScanDetails(scan.scanDetails);
      }
    } catch {
      // Scan not uploaded yet
    }
  };

  const handleOpenView = async (appt: AppointmentDto) => {
    setSelectedAppointment(appt);
    setIsViewOpen(true);
    try {
      setViewLoading(true);
      const scan = await scanService.getByAppointmentId(appt.id);
      setCurrentScan(scan);
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setViewLoading(false);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setSelectedFile(file);

      // Generate local preview URL
      const reader = new FileReader();
      reader.onload = () => {
        setFilePreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmitUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAppointment) return;

    if (!selectedFile) {
      toast.error('Please select an image file to upload.');
      return;
    }
    if (!scanDetails.trim()) {
      toast.error('Please enter technician notes and scan details.');
      return;
    }

    setSubmitting(true);
    try {
      await scanService.uploadScan(selectedAppointment.id, selectedFile, scanDetails);
      toast.success(`Scan successfully uploaded for Appointment #${selectedAppointment.id}!`);
      setIsUploadOpen(false);
      fetchAppointments();
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
          <h1 className="text-2xl font-bold text-white tracking-tight">Technician Scan Worklist</h1>
          <p className="text-sm text-slate-400 mt-1">
            Capture imaging procedures, upload diagnostic files, and verify technical protocols.
          </p>
        </div>
        <div className="flex items-center gap-2 text-xs px-3 py-1.5 rounded-xl bg-sky-500/10 border border-sky-500/20 text-sky-300">
          <Scan className="w-4 h-4" />
          <span>Imaging Capture Station</span>
        </div>
      </div>

      {/* Search */}
      <div className="relative">
        <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" />
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Filter worklist by patient, scan modality, or ID..."
          className="w-full pl-10 pr-4 py-2 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500"
        />
      </div>

      {/* Worklist Table */}
      <div className="rounded-2xl bg-slate-900/60 border border-slate-800 overflow-hidden shadow-sm">
        {loading ? (
          <LoadingSkeleton rows={5} />
        ) : filteredAppointments.length === 0 ? (
          <EmptyState
            title="Scan queue empty"
            description="No imaging appointments are currently pending in this queue."
            icon={<Scan className="w-8 h-8" />}
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/50 text-slate-400 text-xs uppercase tracking-wider border-b border-slate-800/80">
                <tr>
                  <th className="px-5 py-3.5">Appointment</th>
                  <th className="px-5 py-3.5">Patient Details</th>
                  <th className="px-5 py-3.5">Procedure Modality</th>
                  <th className="px-5 py-3.5">Scheduled Slot</th>
                  <th className="px-5 py-3.5">Status</th>
                  <th className="px-5 py-3.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 text-slate-300">
                {filteredAppointments.map((appt) => (
                  <tr key={appt.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-5 py-4 font-mono text-xs text-slate-400">#{appt.id}</td>
                    <td className="px-5 py-4">
                      <div className="font-semibold text-white">{appt.patient?.name}</div>
                      <div className="text-xs text-slate-500">Age/DOB: {appt.patient?.dob}</div>
                    </td>
                    <td className="px-5 py-4">
                      <span className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-slate-800 text-sky-400 border border-sky-500/20">
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
                      {appt.status === 'BOOKED' ? (
                        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-amber-500/10 text-amber-400 border border-amber-500/20">
                          <Clock className="w-3 h-3" /> Ready for Scan
                        </span>
                      ) : appt.status === 'COMPLETED' ? (
                        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                          <CheckCircle2 className="w-3 h-3" /> Scan & Report Done
                        </span>
                      ) : (
                        <span className="px-2 py-0.5 rounded-md text-xs bg-slate-800 text-slate-400">
                          {appt.status}
                        </span>
                      )}
                    </td>
                    <td className="px-5 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        {appt.status !== 'CANCELLED' && (
                          <button
                            onClick={() => handleOpenUpload(appt)}
                            className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-sky-600 hover:bg-sky-500 text-white rounded-xl text-xs font-medium transition-all shadow-sm shadow-sky-500/20"
                          >
                            <Upload className="w-3.5 h-3.5" />
                            <span>{appt.status === 'COMPLETED' ? 'Update Scan' : 'Upload Scan'}</span>
                          </button>
                        )}
                        <button
                          onClick={() => handleOpenView(appt)}
                          title="View Current Scan Result"
                          className="p-1.5 text-slate-400 hover:text-white rounded-lg hover:bg-slate-800 transition-colors"
                        >
                          <Eye className="w-4 h-4" />
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

      {/* Upload Scan Modal */}
      <Modal
        isOpen={isUploadOpen}
        onClose={() => setIsUploadOpen(false)}
        title={`Upload Imaging Scan: #${selectedAppointment?.id}`}
        maxWidth="lg"
      >
        <form onSubmit={handleSubmitUpload} className="space-y-4">
          <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 text-xs text-slate-300 flex items-center justify-between">
            <span>Patient: <strong>{selectedAppointment?.patient?.name}</strong></span>
            <span>Modality: <strong>{selectedAppointment?.scanType}</strong></span>
          </div>

          {/* File Picker / Dropzone */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">
              Select Scan Image File *
            </label>
            <div className="relative border-2 border-dashed border-slate-700 hover:border-sky-500 rounded-2xl p-6 text-center transition-colors bg-slate-950/60 cursor-pointer">
              <input
                type="file"
                accept="image/*,.dcm"
                onChange={handleFileChange}
                className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
              />
              <div className="flex flex-col items-center gap-2 pointer-events-none">
                <Upload className="w-8 h-8 text-sky-400" />
                <div className="text-sm font-medium text-slate-200">
                  {selectedFile ? selectedFile.name : 'Click or drag & drop scan file here'}
                </div>
                <div className="text-xs text-slate-500">
                  Supported formats: JPEG, PNG, DICOM, WebP (Max 10MB)
                </div>
              </div>
            </div>
          </div>

          {/* Image Preview */}
          {filePreview && (
            <div className="relative rounded-xl overflow-hidden border border-slate-800 bg-black max-h-48 flex items-center justify-center">
              <img src={filePreview} alt="Preview" className="max-h-48 object-contain" />
            </div>
          )}

          {/* Technical Details Notes */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">
              Technician Notes / Acquisition Details *
            </label>
            <textarea
              required
              rows={3}
              value={scanDetails}
              onChange={(e) => setScanDetails(e.target.value)}
              placeholder="e.g. Axial and coronal CT sections acquired with intravenous contrast. Protocol complete."
              className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-slate-100 placeholder-slate-500 text-sm focus:ring-2 focus:ring-sky-500 outline-none"
            />
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
            <button
              type="button"
              onClick={() => setIsUploadOpen(false)}
              className="px-4 py-2 text-sm text-slate-400 hover:text-white bg-slate-800 hover:bg-slate-700 rounded-xl transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="px-5 py-2 text-sm font-semibold text-white bg-sky-600 hover:bg-sky-500 rounded-xl transition-colors disabled:opacity-50 flex items-center gap-2"
            >
              {submitting && (
                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              )}
              <span>{submitting ? 'Uploading...' : 'Save Scan Result'}</span>
            </button>
          </div>
        </form>
      </Modal>

      {/* View Scan Modal */}
      <Modal
        isOpen={isViewOpen}
        onClose={() => setIsViewOpen(false)}
        title={`Scan Inspection: #${selectedAppointment?.id}`}
        maxWidth="xl"
      >
        {viewLoading ? (
          <LoadingSkeleton rows={3} />
        ) : currentScan ? (
          <div className="space-y-4">
            <div className="rounded-2xl overflow-hidden border border-slate-800 bg-black flex items-center justify-center p-2 min-h-[260px]">
              <img
                src={scanService.resolveImageUrl(currentScan.imageUrl)}
                alt="Scan Inspection"
                className="max-h-96 object-contain rounded-lg"
                onError={(e) => {
                  (e.target as HTMLImageElement).src = 'https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=600&q=80';
                }}
              />
            </div>

            <div className="p-4 bg-slate-950 rounded-xl border border-slate-800 space-y-2">
              <div className="text-xs font-semibold text-sky-400 uppercase tracking-wider">
                Technical Acquisition Notes
              </div>
              <p className="text-sm text-slate-200 leading-relaxed">{currentScan.scanDetails}</p>
              <div className="flex items-center justify-between text-xs text-slate-500 pt-2 border-t border-slate-800/80">
                <span>Technician: {currentScan.technician?.name || 'Administrator Override'}</span>
                <span>Captured: {currentScan.capturedAt ? new Date(currentScan.capturedAt).toLocaleString() : 'Recent'}</span>
              </div>
            </div>
          </div>
        ) : (
          <div className="py-8 text-center text-sm text-slate-400">
            No scan image has been uploaded for this appointment yet.
          </div>
        )}
      </Modal>
    </div>
  );
};
