import { ShieldCheck } from "lucide-react";
import { Link, Outlet } from "react-router-dom";

export function AuthLayout() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-muted/30 px-4 py-12 text-foreground">
      <Link
        to="/"
        className="mb-8 flex items-center gap-2 text-lg font-semibold tracking-tight"
      >
        <ShieldCheck className="h-6 w-6 text-primary" />
        <span>CyberPulse SOC</span>
      </Link>
      <div className="w-full max-w-md">
        <Outlet />
      </div>
    </div>
  );
}
