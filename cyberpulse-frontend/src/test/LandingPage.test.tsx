import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it } from "vitest";
import { ThemeProvider } from "@/context/ThemeContext";
import { LandingPage } from "@/pages/landing/LandingPage";

describe("LandingPage", () => {
  it("renders redesigned hero, features, and interactive AI preview", async () => {
    const user = userEvent.setup();

    render(
      <ThemeProvider>
        <MemoryRouter>
          <LandingPage />
        </MemoryRouter>
      </ThemeProvider>,
    );

    expect(screen.getByText(/see every threat\./i)).toBeInTheDocument();
    expect(screen.getByText(/stop it before it spreads\./i)).toBeInTheDocument();
    expect(
      screen.getByText(/everything your security team needs\./i),
    ).toBeInTheDocument();
    expect(
      screen.getByText(/security intelligence, powered by ai\./i),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: /perimeter port scan/i }));
    expect(screen.getByText(/sequential tcp syn probes/i)).toBeInTheDocument();
  });
});
