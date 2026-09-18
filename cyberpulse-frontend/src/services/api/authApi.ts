import { apiClient } from "./axios";

import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  User,
} from "@/types/auth";

export async function register(
  request: RegisterRequest,
): Promise<User> {
  const response =
    await apiClient.post<User>(
      "/auth/register",
      request,
    );

  return response.data;
}

export async function login(
  request: LoginRequest,
): Promise<AuthResponse> {
  const response =
    await apiClient.post<AuthResponse>(
      "/auth/login",
      request,
    );

  return response.data;
}

export async function refreshToken(
  refreshToken?: string,
): Promise<AuthResponse> {
  const response =
    await apiClient.post<AuthResponse>(
      "/auth/refresh",
      refreshToken
        ? { refreshToken }
        : {},
    );

  return response.data;
}

export async function getCurrentUser(): Promise<User> {
  const response =
    await apiClient.get<User>(
      "/auth/me",
    );

  return response.data;
}

export async function logout(
  refreshToken?: string,
): Promise<void> {
  await apiClient.post(
    "/auth/logout",
    refreshToken
      ? { refreshToken }
      : {},
  );
}