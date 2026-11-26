import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { SecurityEventsPage } from "@/pages/soc/SecurityEventsPage";

const mockGet = vi.fn();
const mockPost = vi.fn();

vi.mock("@/lib/api", () => ({
  api: {
    get: (...args: unknown[]) => mockGet(...args),
    post: (...args: unknown[]) => mockPost(...args),
  },
  getApiErrorMessage: () => "Error",
}));

describe("SecurityEventsPage", () => {
  beforeEach(() => {
    mockGet.mockResolvedValue({
      data: {
        content: [
          {
            id: "ev-1",
            eventType: "LOGIN_FAILED",
            sourceIp: "10.0.0.5",
            username: "jdoe",
            service: "auth-api",
            message: "Bad password",
            severity: "HIGH",
            eventTime: "2026-01-01T00:00:00Z",
            createdAt: "2026-01-01T00:00:00Z",
          },
        ],
        number: 0,
        size: 15,
        totalElements: 1,
        totalPages: 1,
        first: true,
        last: true,
      },
    });
    mockPost.mockResolvedValue({ data: {} });
  });

  it("renders events and submits a new event via modal", async () => {
    const qc = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const user = userEvent.setup();

    render(
      <QueryClientProvider client={qc}>
        <SecurityEventsPage />
      </QueryClientProvider>,
    );

    expect(await screen.findByText("10.0.0.5")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: /ingest event/i }));
    await user.click(screen.getByRole("button", { name: /submit event/i }));

    await waitFor(() => {
      expect(mockPost).toHaveBeenCalledWith(
        "/api/v1/events",
        expect.objectContaining({ eventType: "LOGIN_FAILED" }),
      );
    });
  });
});
