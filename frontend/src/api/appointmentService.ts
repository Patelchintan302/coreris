import { apiClient } from './client';
import { AppointmentCreateDto, AppointmentDto, AppointmentHistoryDto, SpringPage } from './types';

export const appointmentService = {
  async getAll(): Promise<AppointmentDto[]> {
    const response = await apiClient.get<SpringPage<AppointmentDto> | AppointmentDto[]>('/appointments?size=100');
    if (Array.isArray(response.data)) {
      return response.data;
    }
    return response.data?.content || [];
  },

  async getById(id: number): Promise<AppointmentDto> {
    const response = await apiClient.get<AppointmentDto>(`/appointments/${id}`);
    return response.data;
  },

  async getHistory(id: number): Promise<AppointmentHistoryDto> {
    const response = await apiClient.get<AppointmentHistoryDto>(`/appointments/history/${id}`);
    return response.data;
  },

  async create(appointment: AppointmentCreateDto): Promise<AppointmentDto> {
    const response = await apiClient.post<AppointmentDto>('/appointments', appointment);
    return response.data;
  },

  async update(id: number, appointment: Partial<AppointmentCreateDto>): Promise<AppointmentDto> {
    const response = await apiClient.put<AppointmentDto>(`/appointments/${id}`, appointment);
    return response.data;
  },

  async cancel(id: number): Promise<AppointmentDto> {
    const response = await apiClient.put<AppointmentDto>(`/appointments/${id}/cancel`);
    return response.data;
  },
};
