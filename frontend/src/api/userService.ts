import { apiClient } from './client';
import { RadiologistDto, ReceptionistDto, TechnicianDto, UserDto } from './types';

export const userService = {
  async getAll(): Promise<UserDto[]> {
    const response = await apiClient.get<UserDto[]>('/users');
    return response.data;
  },

  async getById(id: number): Promise<UserDto> {
    const response = await apiClient.get<UserDto>(`/users/${id}`);
    return response.data;
  },

  async create(user: UserDto): Promise<UserDto> {
    const response = await apiClient.post<UserDto>('/users', user);
    return response.data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(`/users/${id}`);
  },

  async getRadiologists(): Promise<RadiologistDto[]> {
    const response = await apiClient.get<RadiologistDto[]>('/radiologists');
    return response.data;
  },

  async getTechnicians(): Promise<TechnicianDto[]> {
    const response = await apiClient.get<TechnicianDto[]>('/technicians');
    return response.data;
  },

  async getReceptionists(): Promise<ReceptionistDto[]> {
    const response = await apiClient.get<ReceptionistDto[]>('/receptionists');
    return response.data;
  },
};
