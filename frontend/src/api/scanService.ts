import { apiClient } from './client';
import { ScanResultDto } from './types';

export const scanService = {
  async getByAppointmentId(appointmentId: number): Promise<ScanResultDto> {
    const response = await apiClient.get<ScanResultDto>(`/appointments/${appointmentId}/scan`);
    return response.data;
  },

  async uploadScan(appointmentId: number, file: File, scanDetails: string): Promise<ScanResultDto> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('scanDetails', scanDetails);

    const response = await apiClient.post<ScanResultDto>(
      `/appointments/${appointmentId}/scan`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data;
  },

  async updateScan(
    appointmentId: number,
    scanDetails: string,
    file?: File
  ): Promise<ScanResultDto> {
    const formData = new FormData();
    if (file) {
      formData.append('file', file);
    }
    formData.append('scanDetails', scanDetails);

    const response = await apiClient.put<ScanResultDto>(
      `/appointments/${appointmentId}/scan`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data;
  },

  resolveImageUrl(imageUrl: string | undefined): string {
    if (!imageUrl) return '';
    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      return imageUrl;
    }
    // Relative endpoint (e.g. /api/v1/scans/download/xyz.jpg)
    return imageUrl;
  },
};
