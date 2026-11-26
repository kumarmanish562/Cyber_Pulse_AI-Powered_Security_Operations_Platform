import { BrowserRouter, Route, Routes } from "react-router-dom";

import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { AuthLayout } from "@/layouts/AuthLayout";
import { DashboardLayout } from "@/layouts/DashboardLayout";
import { PublicLayout } from "@/layouts/PublicLayout";

import { ForgotPasswordPage } from "@/pages/auth/ForgotPasswordPage";
import { LoginPage } from "@/pages/auth/LoginPage";
import { ResetPasswordPage } from "@/pages/auth/ResetPasswordPage";
import { SignupPage } from "@/pages/auth/SignupPage";
import { DashboardPage } from "@/pages/dashboard/DashboardPage";
import { LandingPage } from "@/pages/landing/LandingPage";
import { AuditLogsPage } from "@/pages/soc/AuditLogsPage";
import { DetectionRulesPage } from "@/pages/soc/DetectionRulesPage";
import { IncidentsPage } from "@/pages/soc/IncidentsPage";
import { RiskAssessmentsPage } from "@/pages/soc/RiskAssessmentsPage";
import { SecurityEventsPage } from "@/pages/soc/SecurityEventsPage";
import { ThreatDetectionsPage } from "@/pages/soc/ThreatDetectionsPage";
import { UsersPage } from "@/pages/soc/UsersPage";

export function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<PublicLayout />}>
          <Route path="/" element={<LandingPage />} />
        </Route>

        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />
        </Route>

        <Route element={<ProtectedRoute />}>
          <Route element={<DashboardLayout />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/events" element={<SecurityEventsPage />} />
            <Route path="/detections" element={<ThreatDetectionsPage />} />
            <Route path="/rules" element={<DetectionRulesPage />} />
            <Route path="/risk-assessments" element={<RiskAssessmentsPage />} />
            <Route path="/incidents" element={<IncidentsPage />} />
            <Route path="/audit-logs" element={<AuditLogsPage />} />
            <Route path="/users" element={<UsersPage />} />
          </Route>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
