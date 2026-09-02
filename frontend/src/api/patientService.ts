import { apiClient } from './client';
import { PatientDto, PatientHistoryDto, SpringPage } from './types';

export const patientService = {
  async getAll(): Promise<PatientDto[]> {
    const response = await apiClient.get<SpringPage<PatientDto> | PatientDto[]>('/patients?size=100');
    if (Array.isArray(response.data)) {
      return response.data;
    }
    return response.data?.content || [];
  },

  async getById(id: number): Promise<PatientDto> {
    const response = await apiClient.get<PatientDto>(`/patients/${id}`);
    return response.data;
  },

  async getHistory(id: number): Promise<PatientHistoryDto> {
    const response = await apiClient.get<PatientHistoryDto>(`/patients/history/${id}`);
    return response.data;
  },

  async create(patient: PatientDto): Promise<PatientDto> {
    const response = await apiClient.post<PatientDto>('/patients', patient);
    return response.data;
  },

  async update(id: number, patient: PatientDto): Promise<PatientDto> {
    const response = await apiClient.put<PatientDto>(`/patients/${id}`, patient);
    return response.data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(`/patients/${id}`);
  },
};
