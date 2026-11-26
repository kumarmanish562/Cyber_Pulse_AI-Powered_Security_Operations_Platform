import { Suspense, lazy } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";

import { GuestRoute } from "@/components/auth/GuestRoute";
import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { AuthLayout } from "@/layouts/AuthLayout";
import { DashboardLayout } from "@/layouts/DashboardLayout";
import { PublicLayout } from "@/layouts/PublicLayout";

import { ForgotPasswordPage } from "@/pages/auth/ForgotPasswordPage";
import { LoginPage } from "@/pages/auth/LoginPage";
import { ResetPasswordPage } from "@/pages/auth/ResetPasswordPage";
import { SignupPage } from "@/pages/auth/SignupPage";
import { LandingPage } from "@/pages/landing/LandingPage";

const DashboardPage = lazy(() =>
  import("@/pages/dashboard/DashboardPage").then((m) => ({
    default: m.DashboardPage,
  })),
);
const SecurityEventsPage = lazy(() =>
  import("@/pages/soc/SecurityEventsPage").then((m) => ({
    default: m.SecurityEventsPage,
  })),
);
const ThreatDetectionsPage = lazy(() =>
  import("@/pages/soc/ThreatDetectionsPage").then((m) => ({
    default: m.ThreatDetectionsPage,
  })),
);
const DetectionRulesPage = lazy(() =>
  import("@/pages/soc/DetectionRulesPage").then((m) => ({
    default: m.DetectionRulesPage,
  })),
);
const RiskAssessmentsPage = lazy(() =>
  import("@/pages/soc/RiskAssessmentsPage").then((m) => ({
    default: m.RiskAssessmentsPage,
  })),
);
const IncidentsPage = lazy(() =>
  import("@/pages/soc/IncidentsPage").then((m) => ({
    default: m.IncidentsPage,
  })),
);
const AuditLogsPage = lazy(() =>
  import("@/pages/soc/AuditLogsPage").then((m) => ({
    default: m.AuditLogsPage,
  })),
);
const UsersPage = lazy(() =>
  import("@/pages/soc/UsersPage").then((m) => ({
    default: m.UsersPage,
  })),
);
const ApiKeysPage = lazy(() =>
  import("@/pages/soc/ApiKeysPage").then((m) => ({
    default: m.ApiKeysPage,
  })),
);
const SecuritySettingsPage = lazy(() =>
  import("@/pages/soc/SecuritySettingsPage").then((m) => ({
    default: m.SecuritySettingsPage,
  })),
);
const BlocklistPage = lazy(() =>
  import("@/pages/soc/BlocklistPage").then((m) => ({
    default: m.BlocklistPage,
  })),
);

function RouteFallback() {
  return (
    <div className="flex min-h-[40vh] items-center justify-center text-sm text-muted-foreground">
      Loading view...
    </div>
  );
}

export function AppRoutes() {
  return (
    <BrowserRouter>
      <Suspense fallback={<RouteFallback />}>
        <Routes>
          <Route element={<PublicLayout />}>
            <Route path="/" element={<LandingPage />} />
          </Route>

          <Route element={<GuestRoute />}>
            <Route element={<AuthLayout />}>
              <Route path="/login" element={<LoginPage />} />
              <Route path="/signup" element={<SignupPage />} />
              <Route path="/forgot-password" element={<ForgotPasswordPage />} />
              <Route path="/reset-password" element={<ResetPasswordPage />} />
            </Route>
          </Route>

          <Route element={<ProtectedRoute />}>
            <Route element={<DashboardLayout />}>
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/events" element={<SecurityEventsPage />} />
              <Route path="/detections" element={<ThreatDetectionsPage />} />
              <Route path="/rules" element={<DetectionRulesPage />} />
              <Route path="/risk-assessments" element={<RiskAssessmentsPage />} />
              <Route path="/incidents" element={<IncidentsPage />} />
              <Route path="/blocklist" element={<BlocklistPage />} />
              <Route path="/audit-logs" element={<AuditLogsPage />} />
              <Route path="/users" element={<UsersPage />} />
              <Route path="/api-keys" element={<ApiKeysPage />} />
              <Route path="/security" element={<SecuritySettingsPage />} />
            </Route>
          </Route>
        </Routes>
      </Suspense>
    </BrowserRouter>
  );
}
