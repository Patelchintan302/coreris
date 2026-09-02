import React, { useEffect, useState } from 'react';
import { patientService } from '../api/patientService';
import { PatientDto, BloodGroupType, PatientHistoryDto } from '../api/types';
import { Modal } from '../components/common/Modal';
import { ConfirmDialog } from '../components/common/ConfirmDialog';
import { LoadingSkeleton } from '../components/common/LoadingSkeleton';
import { EmptyState } from '../components/common/EmptyState';
import { useToast } from '../context/ToastContext';
import { getErrorMessage } from '../api/client';
import {
  UserPlus,
  Search,
  History,
  Edit2,
  Trash2,
  Users,
  Calendar,
  Phone,
  Mail,
  HeartPulse,
} from 'lucide-react';

const BLOOD_GROUPS: BloodGroupType[] = [
  'A_POSITIVE',
  'A_NEGATIVE',
  'B_POSITIVE',
  'B_NEGATIVE',
  'AB_POSITIVE',
  'AB_NEGATIVE',
  'O_POSITIVE',
  'O_NEGATIVE',
];

export const PatientsPage: React.FC = () => {
  const [patients, setPatients] = useState<PatientDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [searchTerm, setSearchTerm] = useState('');

  // Modal States
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isHistoryOpen, setIsHistoryOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  // Selected Records
  const [selectedPatient, setSelectedPatient] = useState<PatientDto | null>(null);
  const [patientHistory, setPatientHistory] = useState<PatientHistoryDto | null>(null);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // Form State
  const [formData, setFormData] = useState<PatientDto>({
    name: '',
    email: '',
    mobileNo: '',
    bloodGroup: 'O_POSITIVE',
    dob: '',
    gender: 'Male',
  });

  const toast = useToast();

  const fetchPatients = async () => {
    try {
      setLoading(true);
      const data = await patientService.getAll();
      setPatients(Array.isArray(data) ? data : []);
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPatients();
  }, []);

  const handleOpenCreate = () => {
    setSelectedPatient(null);
    setFormData({
      name: '',
      email: '',
      mobileNo: '',
      bloodGroup: 'O_POSITIVE',
      dob: '1995-01-01',
      gender: 'Male',
    });
    setIsFormOpen(true);
  };

  const handleOpenEdit = (patient: PatientDto) => {
    setSelectedPatient(patient);
    setFormData({
      ...patient,
      dob: patient.dob ? patient.dob.substring(0, 10) : '',
    });
    setIsFormOpen(true);
  };

  const handleOpenHistory = async (patient: PatientDto) => {
    if (!patient.id) return;
    setSelectedPatient(patient);
    setIsHistoryOpen(true);
    try {
      setHistoryLoading(true);
      const history = await patientService.getHistory(patient.id);
      setPatientHistory(history);
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setHistoryLoading(false);
    }
  };

  const handleOpenDelete = (patient: PatientDto) => {
    setSelectedPatient(patient);
    setIsDeleteOpen(true);
  };

  const handleSubmitForm = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || !formData.email || !formData.mobileNo) {
      toast.error('Please fill all required fields.');
      return;
    }

    setSubmitting(true);
    try {
      if (selectedPatient?.id) {
        await patientService.update(selectedPatient.id, formData);
        toast.success(`Updated patient: ${formData.name}`);
      } else {
        await patientService.create(formData);
        toast.success(`Registered new patient: ${formData.name}`);
      }
      setIsFormOpen(false);
      fetchPatients();
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteConfirm = async () => {
    if (!selectedPatient?.id) return;
    setSubmitting(true);
    try {
      await patientService.delete(selectedPatient.id);
      toast.success(`Deleted patient: ${selectedPatient.name}`);
      setIsDeleteOpen(false);
      fetchPatients();
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const filteredPatients = (Array.isArray(patients) ? patients : []).filter((p) => {
    const term = searchTerm.toLowerCase();
    return (
      p.name?.toLowerCase().includes(term) ||
      p.email?.toLowerCase().includes(term) ||
      String(p.mobileNo).includes(term)
    );
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Patient Directory</h1>
          <p className="text-sm text-slate-400 mt-1">
            Register and manage master patient electronic health records.
          </p>
        </div>
        <button
          onClick={handleOpenCreate}
          className="inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 hover:bg-blue-500 text-white text-sm font-medium rounded-xl shadow-lg shadow-blue-500/20 transition-all self-start sm:self-auto"
        >
          <UserPlus className="w-4 h-4" />
          <span>Register Patient</span>
        </button>
      </div>

      {/* Search Bar */}
      <div className="relative">
        <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" />
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Search by patient name, email, or mobile..."
          className="w-full pl-10 pr-4 py-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
        />
      </div>

      {/* Patient Table */}
      <div className="rounded-2xl bg-slate-900/60 border border-slate-800 overflow-hidden shadow-sm">
        {loading ? (
          <LoadingSkeleton rows={5} />
        ) : filteredPatients.length === 0 ? (
          <EmptyState
            title="No patients found"
            description={
              searchTerm
                ? 'No matching patient records found for your search term.'
                : 'Get started by registering the first patient in the clinic.'
            }
            icon={<Users className="w-8 h-8" />}
            action={
              !searchTerm && (
                <button
                  onClick={handleOpenCreate}
                  className="px-4 py-2 bg-blue-600 text-white rounded-xl text-sm font-medium hover:bg-blue-500"
                >
                  Register Patient
                </button>
              )
            }
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/50 text-slate-400 text-xs uppercase tracking-wider border-b border-slate-800/80">
                <tr>
                  <th className="px-5 py-3.5">ID</th>
                  <th className="px-5 py-3.5">Name</th>
                  <th className="px-5 py-3.5">Contact Details</th>
                  <th className="px-5 py-3.5">Gender / DOB</th>
                  <th className="px-5 py-3.5">Blood Group</th>
                  <th className="px-5 py-3.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 text-slate-300">
                {filteredPatients.map((patient) => (
                  <tr key={patient.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-5 py-4 font-mono text-xs text-slate-500">#{patient.id}</td>
                    <td className="px-5 py-4 font-medium text-white">{patient.name}</td>
                    <td className="px-5 py-4">
                      <div className="flex flex-col gap-0.5 text-xs">
                        <span className="flex items-center gap-1.5 text-slate-300">
                          <Mail className="w-3.5 h-3.5 text-slate-500" /> {patient.email}
                        </span>
                        <span className="flex items-center gap-1.5 text-slate-400">
                          <Phone className="w-3.5 h-3.5 text-slate-500" /> {patient.mobileNo}
                        </span>
                      </div>
                    </td>
                    <td className="px-5 py-4 text-xs text-slate-300">
                      <div>{patient.gender}</div>
                      <div className="text-slate-500 text-[11px]">{patient.dob}</div>
                    </td>
                    <td className="px-5 py-4">
                      <span className="px-2.5 py-1 rounded-full text-[11px] font-bold bg-red-500/10 text-red-400 border border-red-500/20">
                        {patient.bloodGroup?.replace('_', ' ')}
                      </span>
                    </td>
                    <td className="px-5 py-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <button
                          onClick={() => handleOpenHistory(patient)}
                          title="Medical History"
                          className="p-2 text-slate-400 hover:text-sky-400 hover:bg-sky-500/10 rounded-lg transition-colors"
                        >
                          <History className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleOpenEdit(patient)}
                          title="Edit Details"
                          className="p-2 text-slate-400 hover:text-amber-400 hover:bg-amber-500/10 rounded-lg transition-colors"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleOpenDelete(patient)}
                          title="Delete Patient"
                          className="p-2 text-slate-400 hover:text-rose-400 hover:bg-rose-500/10 rounded-lg transition-colors"
                        >
                          <Trash2 className="w-4 h-4" />
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

      {/* Patient Create/Edit Modal */}
      <Modal
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        title={selectedPatient ? `Edit Patient: ${selectedPatient.name}` : 'Register New Patient'}
        maxWidth="lg"
      >
        <form onSubmit={handleSubmitForm} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Full Name *</label>
            <input
              type="text"
              required
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="e.g. John Doe"
              className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-slate-100 placeholder-slate-500 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Email Address *</label>
              <input
                type="email"
                required
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                placeholder="john.doe@example.com"
                className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-slate-100 placeholder-slate-500 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Mobile Number (10 digits) *</label>
              <input
                type="number"
                required
                value={formData.mobileNo}
                onChange={(e) => setFormData({ ...formData, mobileNo: e.target.value })}
                placeholder="9876543210"
                className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-slate-100 placeholder-slate-500 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Blood Group *</label>
              <select
                value={formData.bloodGroup}
                onChange={(e) =>
                  setFormData({ ...formData, bloodGroup: e.target.value as BloodGroupType })
                }
                className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-slate-100 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
              >
                {BLOOD_GROUPS.map((bg) => (
                  <option key={bg} value={bg}>
                    {bg.replace('_', ' ')}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Gender *</label>
              <select
                value={formData.gender}
                onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-slate-100 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
              >
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Date of Birth *</label>
              <input
                type="date"
                required
                value={formData.dob}
                onChange={(e) => setFormData({ ...formData, dob: e.target.value })}
                className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-slate-100 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
            <button
              type="button"
              onClick={() => setIsFormOpen(false)}
              className="px-4 py-2 text-sm text-slate-400 hover:text-white bg-slate-800 hover:bg-slate-700 rounded-xl transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="px-5 py-2 text-sm font-semibold text-white bg-blue-600 hover:bg-blue-500 rounded-xl transition-colors disabled:opacity-50"
            >
              {submitting ? 'Saving...' : selectedPatient ? 'Save Changes' : 'Register Patient'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Patient History Modal */}
      <Modal
        isOpen={isHistoryOpen}
        onClose={() => setIsHistoryOpen(false)}
        title={`Medical History: ${selectedPatient?.name || ''}`}
        maxWidth="2xl"
      >
        {historyLoading ? (
          <LoadingSkeleton rows={3} />
        ) : !patientHistory?.appointments || patientHistory.appointments.length === 0 ? (
          <div className="text-center py-8 text-sm text-slate-400">
            No appointment records found for this patient yet.
          </div>
        ) : (
          <div className="space-y-4">
            <div className="p-4 bg-slate-950/60 rounded-xl border border-slate-800 text-xs text-slate-400 flex items-center justify-between">
              <span>Patient ID: #{patientHistory.patient?.id}</span>
              <span>Blood Group: {patientHistory.patient?.bloodGroup?.replace('_', ' ')}</span>
            </div>

            <div className="divide-y divide-slate-800/80">
              {patientHistory.appointments.map((appt) => (
                <div key={appt.id} className="py-3.5 flex items-center justify-between gap-4">
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-semibold text-white text-sm">{appt.scanType}</span>
                      <span className="font-mono text-xs text-slate-500">#{appt.id}</span>
                    </div>
                    <div className="text-xs text-slate-400 mt-0.5">
                      {new Date(appt.appointmentTime).toLocaleString()}
                    </div>
                  </div>
                  <span
                    className={`text-xs px-2.5 py-1 rounded-full font-semibold ${
                      appt.status === 'COMPLETED'
                        ? 'bg-emerald-500/10 text-emerald-400'
                        : appt.status === 'CANCELLED'
                        ? 'bg-rose-500/10 text-rose-400'
                        : 'bg-amber-500/10 text-amber-400'
                    }`}
                  >
                    {appt.status}
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}
      </Modal>

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={isDeleteOpen}
        onClose={() => setIsDeleteOpen(false)}
        onConfirm={handleDeleteConfirm}
        title="Delete Patient Record"
        message={`Are you sure you want to permanently delete patient "${selectedPatient?.name}"? This action cannot be undone.`}
        confirmText="Delete Record"
        variant="danger"
        loading={submitting}
      />
    </div>
  );
};
