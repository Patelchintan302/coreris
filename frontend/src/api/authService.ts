import { apiClient } from './client';
import { AuthResponseDto } from './types';

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface SessionUser {
  username: string;
  role: string;
}

export const authService = {
  async login(credentials: LoginCredentials): Promise<AuthResponseDto> {
    const response = await apiClient.post<AuthResponseDto>('/auth/login', credentials);
    if (response.data.token) {
      localStorage.setItem('coreris_token', response.data.token);
      localStorage.setItem(
        'coreris_user',
        JSON.stringify({
          username: response.data.username,
          role: response.data.role.replace('ROLE_', ''),
        })
      );
    }
    return response.data;
  },

  logout(): void {
    localStorage.removeItem('coreris_token');
    localStorage.removeItem('coreris_user');
  },

  getToken(): string | null {
    return localStorage.getItem('coreris_token');
  },

  getCurrentUser(): SessionUser | null {
    const raw = localStorage.getItem('coreris_user');
    if (!raw) return null;
    try {
      return JSON.parse(raw) as SessionUser;
    } catch {
      return null;
    }
  },

  isAuthenticated(): boolean {
    return !!this.getToken();
  },
};
