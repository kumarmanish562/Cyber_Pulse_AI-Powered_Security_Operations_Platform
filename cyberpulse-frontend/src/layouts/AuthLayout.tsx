import { CheckCircle2, ShieldCheck } from "lucide-react";
import { Link, Outlet } from "react-router-dom";
import { ThemeToggle } from "@/components/theme/ThemeToggle";

const HIGHLIGHTS = [
  "JWT session security with optional TOTP 2FA",
  "Real-time threat detection & MITRE ATT&CK mapping",
  "AI-assisted triage & automated IP containment",
];

export function AuthLayout() {
  return (
    <div className="grid min-h-screen bg-background text-foreground lg:grid-cols-12">
      {/* Left Branding Panel */}
      <aside className="relative hidden flex-col justify-between overflow-hidden border-r border-border/60 bg-card/50 p-10 lg:col-span-5 lg:flex">
        <div className="pointer-events-none absolute inset-0 -z-10 bg-[radial-gradient(circle_at_top_left,rgba(56,189,248,0.16),transparent_50%),radial-gradient(circle_at_bottom_right,rgba(139,92,246,0.14),transparent_50%)]" />
        <div className="pointer-events-none absolute inset-0 -z-10 bg-[linear-gradient(to_right,rgba(148,163,184,0.04)_1px,transparent_1px),linear-gradient(to_bottom,rgba(148,163,184,0.04)_1px,transparent_1px)] bg-[size:40px_40px]" />

        <Link to="/" className="flex items-center gap-2.5">
          <div className="flex h-9 w-9 items-center justify-center rounded-lg border border-primary/40 bg-primary/15 text-primary">
            <ShieldCheck className="h-5 w-5" />
          </div>
          <span className="text-lg font-bold tracking-tight">
            CyberPulse SOC
          </span>
        </Link>

        <div className="space-y-6">
          <p className="font-mono text-xs font-semibold uppercase tracking-widest text-cyan-400">
            Enterprise Security Operations
          </p>
          <h2 className="text-3xl font-bold leading-tight tracking-tight">
            Intelligent Security.
            <br />
            Faster Response.
            <br />
            Stronger Defense.
          </h2>
          <p className="max-w-md text-sm leading-relaxed text-muted-foreground">
            Unified telemetry, automated risk scoring, and AI-powered incident
            triage built for modern security operations teams.
          </p>

          <ul className="space-y-3 pt-2 text-sm text-muted-foreground">
            {HIGHLIGHTS.map((item) => (
              <li key={item} className="flex items-center gap-2.5">
                <CheckCircle2 className="h-4 w-4 shrink-0 text-cyan-400" />
                <span>{item}</span>
              </li>
            ))}
          </ul>
        </div>

        <div className="text-xs text-muted-foreground">
          © {new Date().getFullYear()} CyberPulse SOC Platform
        </div>
      </aside>

      {/* Right Form Panel */}
      <div className="relative flex flex-col items-center justify-center px-4 py-12 sm:px-8 lg:col-span-7">
        <div className="absolute top-4 right-4 flex items-center gap-2">
          <ThemeToggle />
        </div>

        <Link
          to="/"
          className="mb-8 flex items-center gap-2 text-lg font-bold tracking-tight lg:hidden"
        >
          <ShieldCheck className="h-6 w-6 text-primary" />
          <span>CyberPulse SOC</span>
        </Link>

        <div className="w-full max-w-md">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
