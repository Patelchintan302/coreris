// CoreRIS TypeScript Type Definitions
export type RoleType = 'ADMIN' | 'RECEPTIONIST' | 'TECHNICIAN' | 'RADIOLOGIST';

export type ScanType = 'XRAY_SCAN' | 'CT_SCAN' | 'MRI_SCAN' | 'ULTRASOUND_SCAN' | 'PET_SCAN';

export type StatusType = 'BOOKED' | 'CANCELLED' | 'PENDING' | 'COMPLETED';

export type BloodGroupType =
  | 'A_POSITIVE'
  | 'A_NEGATIVE'
  | 'B_POSITIVE'
  | 'B_NEGATIVE'
  | 'AB_POSITIVE'
  | 'AB_NEGATIVE'
  | 'O_POSITIVE'
  | 'O_NEGATIVE';

export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface UserDto {
  id?: number;
  role: RoleType;
  name: string;
  username: string;
  password?: string;
}

export interface AuthResponseDto {
  token: string;
  username: string;
  role: string;
}

export interface PatientDto {
  id?: number;
  name: string;
  email: string;
  mobileNo: number | string;
  bloodGroup: BloodGroupType;
  dob: string;
  gender: string;
}

export interface PatientHistoryDto {
  patient: PatientDto;
  appointments: AppointmentDto[];
}

export interface AppointmentDto {
  id: number;
  scanType: ScanType;
  appointmentTime: string;
  createdAt?: string;
  patient: PatientDto;
  status: StatusType;
}

export interface AppointmentCreateDto {
  scanType: ScanType;
  appointmentTime: string;
  patientId: number;
  status?: StatusType;
}

export interface AppointmentHistoryDto {
  appointment: AppointmentDto;
  scanResult?: ScanResultDto;
  report?: ReportDto;
}

export interface TechnicianDto {
  id: number;
  name: string;
}

export interface RadiologistDto {
  id: number;
  name: string;
}

export interface ReceptionistDto {
  id: number;
  name: string;
}

export interface ScanResultDto {
  id: number;
  scanDetails: string;
  imageUrl: string;
  capturedAt?: string;
  technician?: TechnicianDto;
  adminId?: number;
  appointmentId: number;
}

export interface ScanResultCreateDto {
  scanDetails: string;
}

export interface ReportDto {
  id: number;
  finding: string;
  createdAt?: string;
  radiologist?: RadiologistDto;
  adminId?: number;
  pdfUrl?: string;
  appointmentId: number;
}

export interface ReportCreateDto {
  finding: string;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: Record<string, string>;
}
