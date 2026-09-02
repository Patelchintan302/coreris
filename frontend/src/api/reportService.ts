import { apiClient } from './client';
import { ReportCreateDto, ReportDto } from './types';

export const reportService = {
  async getByAppointmentId(appointmentId: number): Promise<ReportDto> {
    const response = await apiClient.get<ReportDto>(`/appointments/${appointmentId}/report`);
    return response.data;
  },

  async createReport(appointmentId: number, data: ReportCreateDto): Promise<ReportDto> {
    const response = await apiClient.post<ReportDto>(`/appointments/${appointmentId}/report`, data);
    return response.data;
  },

  async updateReport(appointmentId: number, data: ReportCreateDto): Promise<ReportDto> {
    const response = await apiClient.put<ReportDto>(`/appointments/${appointmentId}/report`, data);
    return response.data;
  },

  resolvePdfUrl(pdfUrl: string | undefined): string {
    if (!pdfUrl) return '';
    if (pdfUrl.startsWith('http://') || pdfUrl.startsWith('https://')) {
      return pdfUrl;
    }
    return pdfUrl;
  },
};
