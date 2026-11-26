import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { LoginPage } from "@/pages/auth/LoginPage";

const mockLogin = vi.fn();

vi.mock("@/context/AuthContext", () => ({
  useAuth: () => ({
    login: mockLogin,
    user: null,
    isAuthenticated: false,
    isLoading: false,
  }),
}));

describe("LoginPage", () => {
  beforeEach(() => {
    mockLogin.mockReset();
  });

  it("renders sign in form and prompts for 2FA when mfaRequired is true", async () => {
    mockLogin
      .mockResolvedValueOnce({
        accessToken: null,
        refreshToken: null,
        tokenType: "Bearer",
        expiresIn: 0,
        mfaRequired: true,
        user: { id: "1", username: "admin", email: "admin@cp.io", roles: ["ADMIN"] },
      })
      .mockResolvedValueOnce({
        accessToken: "jwt-token",
        refreshToken: "refresh-token",
        tokenType: "Bearer",
        expiresIn: 900,
        mfaRequired: false,
        user: { id: "1", username: "admin", email: "admin@cp.io", roles: ["ADMIN"] },
      });

    const user = userEvent.setup();
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    );

    await user.type(screen.getByLabelText(/username or email/i), "admin");
    await user.type(screen.getByLabelText(/^password$/i), "Secret123!");
    await user.click(screen.getByRole("button", { name: /sign in/i }));

    await waitFor(() => {
      expect(screen.getByText(/two-factor verification/i)).toBeInTheDocument();
    });

    await user.type(screen.getByLabelText(/6-digit authenticator code/i), "123456");
    await user.click(screen.getByRole("button", { name: /verify & sign in/i }));

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledTimes(2);
    });
  });
});
