import axios, {
  AxiosError,
  type InternalAxiosRequestConfig,
} from "axios";

import {
  getAccessToken,
  getRefreshToken,
  setAccessToken,
  setRefreshToken,
  clearTokens,
} from "./tokenStore";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ||
  "http://localhost:8080/api/v1";

// ============================================================
// MAIN API CLIENT
// ============================================================

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
});

// ============================================================
// REFRESH CLIENT
// IMPORTANT:
// This client has NO response interceptor.
// Therefore refresh cannot recursively trigger itself.
// ============================================================

const refreshClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
});

// ============================================================
// REQUEST INTERCEPTOR
// ============================================================

apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getAccessToken();

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
);

// ============================================================
// REFRESH STATE
// ============================================================

let isRefreshing = false;

let refreshPromise: Promise<string | null> | null = null;

// ============================================================
// REFRESH ACCESS TOKEN
// ============================================================

async function refreshAccessToken(): Promise<string | null> {
  // ----------------------------------------------------------
  // If another request is already refreshing, reuse it.
  // ----------------------------------------------------------

  if (isRefreshing && refreshPromise) {
    return refreshPromise;
  }

  // ----------------------------------------------------------
  // IMPORTANT:
  // Never call /auth/refresh without a refresh token.
  // ----------------------------------------------------------

  const currentRefreshToken =
    getRefreshToken();

  if (!currentRefreshToken) {
    clearTokens();
    return null;
  }

  isRefreshing = true;

  refreshPromise = (async () => {
    try {
      const response =
        await refreshClient.post(
          "/auth/refresh",
          {
            refreshToken:
              currentRefreshToken,
          },
        );

      const newAccessToken =
        response.data.accessToken;

      const newRefreshToken =
        response.data.refreshToken;

      if (!newAccessToken) {
        throw new Error(
          "Refresh response did not contain an access token.",
        );
      }

      // ------------------------------------------------------
      // Store new access token
      // ------------------------------------------------------

      setAccessToken(
        newAccessToken,
      );

      // ------------------------------------------------------
      // Store rotated refresh token if backend returns one
      // ------------------------------------------------------

      if (newRefreshToken) {
        setRefreshToken(
          newRefreshToken,
        );
      }

      return newAccessToken;
    } catch (refreshError) {
      console.error(
        "Token refresh failed:",
        refreshError,
      );

      clearTokens();

      return null;
    } finally {
      isRefreshing = false;
      refreshPromise = null;
    }
  })();

  return refreshPromise;
}

// ============================================================
// RESPONSE INTERCEPTOR
// ============================================================

apiClient.interceptors.response.use(
  (response) => {
    return response;
  },

  async (error: AxiosError) => {
    const originalRequest =
      error.config as
        | (InternalAxiosRequestConfig & {
            _retry?: boolean;
          })
        | undefined;

    // --------------------------------------------------------
    // No request configuration
    // --------------------------------------------------------

    if (!originalRequest) {
      return Promise.reject(error);
    }

    const requestUrl =
      originalRequest.url ?? "";

    // --------------------------------------------------------
    // NEVER refresh for authentication endpoints
    // --------------------------------------------------------

    const isAuthRequest =
      requestUrl.includes("/auth/login") ||
      requestUrl.includes("/auth/register") ||
      requestUrl.includes("/auth/refresh") ||
      requestUrl.includes("/auth/logout");

    if (isAuthRequest) {
      return Promise.reject(error);
    }

    // --------------------------------------------------------
    // Only 401 should trigger token refresh.
    //
    // 400 = bad request
    // 401 = authentication expired/invalid
    // 403 = authenticated but forbidden
    // --------------------------------------------------------

    if (
      error.response?.status !== 401
    ) {
      return Promise.reject(error);
    }

    // --------------------------------------------------------
    // Never retry the same request twice
    // --------------------------------------------------------

    if (originalRequest._retry) {
      clearTokens();

      return Promise.reject(error);
    }

    originalRequest._retry = true;

    // --------------------------------------------------------
    // Check refresh token BEFORE calling refresh endpoint
    // --------------------------------------------------------

    if (!getRefreshToken()) {
      clearTokens();

      return Promise.reject(error);
    }

    // --------------------------------------------------------
    // Get new access token
    // --------------------------------------------------------

    const newAccessToken =
      await refreshAccessToken();

    if (!newAccessToken) {
      return Promise.reject(error);
    }

    // --------------------------------------------------------
    // Retry original request
    // --------------------------------------------------------

    originalRequest.headers.Authorization =
      `Bearer ${newAccessToken}`;

    return apiClient(
      originalRequest,
    );
  },
);

// ============================================================
// RESET AUTHENTICATION
// ============================================================

export function resetApiAuthentication(): void {
  clearTokens();
}