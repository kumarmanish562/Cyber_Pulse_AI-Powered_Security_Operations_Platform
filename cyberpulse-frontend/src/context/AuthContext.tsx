import {
  createContext,
  useCallback,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";

import {
  getCurrentUser,
  login as loginApi,
  logout as logoutApi,
  refreshToken as refreshTokenApi,
  register as registerApi,
} from "@/services/api/authApi";

import {
  clearTokens,
  getRefreshToken,
  hasRefreshToken,
  setAccessToken,
  setRefreshToken,
} from "@/services/api/tokenStore";

import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  User,
} from "@/types/auth";

interface AuthContextValue {
  user: User | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;

  login(
    request: LoginRequest,
  ): Promise<void>;

  register(
    request: RegisterRequest,
  ): Promise<void>;

  logout(): Promise<void>;
}

export const AuthContext =
  createContext<AuthContextValue | undefined>(
    undefined,
  );

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({
  children,
}: AuthProviderProps) {
  const [user, setUser] =
    useState<User | null>(null);

  const [accessToken, setAccessTokenState] =
    useState<string | null>(null);

  const [isLoading, setIsLoading] =
    useState(true);

  // ==========================================================
  // LOGIN
  // ==========================================================

  const login = useCallback(
    async (request: LoginRequest) => {
      const response: AuthResponse =
        await loginApi(request);

      setAccessToken(
        response.accessToken,
      );

      setAccessTokenState(
        response.accessToken,
      );

      if (response.refreshToken) {
        setRefreshToken(
          response.refreshToken,
        );
      }

      setUser(response.user);
    },
    [],
  );

  // ==========================================================
  // REGISTER
  // ==========================================================

  const register = useCallback(
    async (
      request: RegisterRequest,
    ) => {
      await registerApi(request);
    },
    [],
  );

  // ==========================================================
  // LOGOUT
  // ==========================================================

  const logout = useCallback(
    async () => {
      const refreshToken =
        getRefreshToken();

      try {
        await logoutApi(
          refreshToken ?? undefined,
        );
      } finally {
        clearTokens();

        setAccessTokenState(null);

        setUser(null);
      }
    },
    [],
  );

  // ==========================================================
  // RESTORE SESSION
  // ==========================================================

  useEffect(() => {
    let mounted = true;

    async function restoreSession() {
      /*
       * IMPORTANT:
       *
       * Do not call /auth/refresh when there is
       * no refresh token.
       */

      if (!hasRefreshToken()) {
        if (mounted) {
          setIsLoading(false);
        }

        return;
      }

      try {
        const refreshToken =
          getRefreshToken();

        if (!refreshToken) {
          return;
        }

        const response =
          await refreshTokenApi(
            refreshToken,
          );

        if (!mounted) {
          return;
        }

        setAccessToken(
          response.accessToken,
        );

        setAccessTokenState(
          response.accessToken,
        );

        if (response.refreshToken) {
          setRefreshToken(
            response.refreshToken,
          );
        }

        setUser(response.user);
      } catch (error) {
        console.warn(
          "Session restoration failed.",
          error,
        );

        clearTokens();

        if (mounted) {
          setAccessTokenState(null);
          setUser(null);
        }
      } finally {
        if (mounted) {
          setIsLoading(false);
        }
      }
    }

    restoreSession();

    return () => {
      mounted = false;
    };
  }, []);

  const value = useMemo(
    () => ({
      user,
      accessToken,
      isAuthenticated:
        Boolean(accessToken),
      isLoading,
      login,
      register,
      logout,
    }),
    [
      user,
      accessToken,
      isLoading,
      login,
      register,
      logout,
    ],
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}