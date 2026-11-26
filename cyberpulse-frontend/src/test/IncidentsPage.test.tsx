import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { IncidentsPage } from "@/pages/soc/IncidentsPage";

const mockGet = vi.fn();
const mockPost = vi.fn();

vi.mock("@/lib/api", () => ({
  api: {
    get: (...args: unknown[]) => mockGet(...args),
    post: (...args: unknown[]) => mockPost(...args),
    patch: vi.fn(),
  },
  getApiErrorMessage: () => "Error",
}));

describe("IncidentsPage", () => {
  beforeEach(() => {
    mockGet.mockImplementation((url: string) => {
      if (url.includes("/notes")) {
        return Promise.resolve({ data: [] });
      }
      return Promise.resolve({
        data: {
          content: [
            {
              id: "inc-1",
              incidentNumber: "INC-2026-0001",
              title: "Brute force spike",
              description: "Repeated failed SSH logins",
              status: "OPEN",
              severity: "HIGH",
              riskAssessmentId: null,
              assignedTo: null,
              createdAt: "2026-01-01T00:00:00Z",
              updatedAt: "2026-01-01T00:00:00Z",
              resolvedAt: null,
              closedAt: null,
            },
          ],
          page: 0,
          size: 15,
          totalElements: 1,
          totalPages: 1,
          first: true,
          last: true,
        },
      });
    });

    mockPost.mockResolvedValue({
      data: {
        incidentId: "inc-1",
        incidentNumber: "INC-2026-0001",
        provider: "DETERMINISTIC_PLAYBOOK",
        summary: "Automated brute-force containment summary",
        likelyAttackVector: "Credential stuffing",
        recommendedActions: ["Block source IP"],
        priorityAssessment: "P2 — Urgent",
        generatedAt: "2026-01-01T00:00:00Z",
      },
    });
  });

  it("opens incident details and runs AI triage", async () => {
    const qc = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const user = userEvent.setup();

    render(
      <QueryClientProvider client={qc}>
        <IncidentsPage />
      </QueryClientProvider>,
    );

    expect(await screen.findByText("INC-2026-0001")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: /inspect/i }));
    await user.click(screen.getByRole("button", { name: /run ai triage/i }));

    await waitFor(() => {
      expect(mockPost).toHaveBeenCalledWith("/api/v1/incidents/inc-1/ai-triage");
      expect(
        screen.getByText(/automated brute-force containment summary/i),
      ).toBeInTheDocument();
    });
  });
});
