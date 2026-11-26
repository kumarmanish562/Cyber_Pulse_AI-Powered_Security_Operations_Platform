import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import { api, tokenStorage } from "@/lib/api";
import type { AuthResponse, UserResponse } from "@/types/api";

interface LoginCredentials {
  usernameOrEmail: string;
  password: string;
  totpCode?: string;
}

interface RegisterPayload {
  username: string;
  email: string;
  password: string;
}

interface AuthContextValue {
  user: UserResponse | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginCredentials) => Promise<AuthResponse>;
  register: (payload: RegisterPayload) => Promise<UserResponse>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
  hasRole: (role: string) => boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const refreshUser = useCallback(async () => {
    const res = await api.get<UserResponse>("/api/v1/auth/me");
    setUser(res.data);
  }, []);

  useEffect(() => {
    const token = tokenStorage.getAccessToken();
    if (!token) {
      setIsLoading(false);
      return;
    }

    api
      .get<UserResponse>("/api/v1/auth/me")
      .then((res) => setUser(res.data))
      .catch(() => {
        tokenStorage.clear();
        setUser(null);
      })
      .finally(() => setIsLoading(false));
  }, []);

  const login = useCallback(async (credentials: LoginCredentials) => {
    const res = await api.post<AuthResponse>("/api/v1/auth/login", credentials);
    if (res.data.mfaRequired) {
      return res.data;
    }
    if (res.data.accessToken && res.data.refreshToken) {
      tokenStorage.setTokens(res.data.accessToken, res.data.refreshToken);
    }
    setUser(res.data.user);
    return res.data;
  }, []);

  const register = useCallback(async (payload: RegisterPayload) => {
    const res = await api.post<UserResponse>("/api/v1/auth/register", payload);
    return res.data;
  }, []);

  const logout = useCallback(async () => {
    const refreshToken = tokenStorage.getRefreshToken();
    try {
      if (refreshToken) {
        await api.post("/api/v1/auth/logout", { refreshToken });
      }
    } catch {
      // Clear local session even if server-side revocation fails
    } finally {
      tokenStorage.clear();
      setUser(null);
    }
  }, []);

  const hasRole = useCallback(
    (role: string) => Boolean(user?.roles?.includes(role)),
    [user],
  );

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isAuthenticated: Boolean(user),
      isLoading,
      login,
      register,
      logout,
      refreshUser,
      hasRole,
    }),
    [user, isLoading, login, register, logout, refreshUser, hasRole],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return ctx;
}
