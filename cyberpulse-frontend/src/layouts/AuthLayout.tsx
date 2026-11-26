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
    <div className="grid min-h-screen bg-background text-foreground antialiased lg:grid-cols-12">
      {/* Left Editorial Panel */}
      <aside className="hidden flex-col justify-between border-r border-border/70 bg-muted/20 p-10 lg:col-span-5 lg:flex">
        <Link to="/" className="flex items-center gap-2.5">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-foreground text-background">
            <ShieldCheck className="h-4.5 w-4.5" />
          </div>
          <span className="text-base font-semibold tracking-tight">
            CyberPulse
          </span>
        </Link>

        <div className="space-y-6">
          <p className="text-xs font-medium tracking-wider text-muted-foreground uppercase">
            Enterprise Security Operations
          </p>
          <h2 className="text-3xl font-semibold leading-[1.12] tracking-[-0.03em] sm:text-4xl">
            Intelligent Security.
            <span className="mt-1 block text-muted-foreground">
              Faster Response.
            </span>
            <span className="mt-1 block text-muted-foreground">
              Stronger Defense.
            </span>
          </h2>
          <p className="max-w-md text-sm leading-relaxed text-muted-foreground">
            Unified telemetry, automated risk scoring, and AI-powered incident
            triage built for modern security operations teams.
          </p>

          <ul className="space-y-3 pt-2 text-sm text-muted-foreground">
            {HIGHLIGHTS.map((item) => (
              <li key={item} className="flex items-center gap-2.5">
                <CheckCircle2 className="h-4 w-4 shrink-0 text-foreground/75" />
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
          className="mb-8 flex items-center gap-2 text-base font-semibold tracking-tight lg:hidden"
        >
          <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-foreground text-background">
            <ShieldCheck className="h-4 w-4" />
          </div>
          <span>CyberPulse</span>
        </Link>

        <div className="w-full max-w-md">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
