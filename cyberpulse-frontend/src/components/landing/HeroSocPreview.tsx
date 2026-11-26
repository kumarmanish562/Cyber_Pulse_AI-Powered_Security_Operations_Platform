import { Activity, AlertTriangle, BrainCircuit, ShieldAlert } from "lucide-react";
import { Badge } from "@/components/ui/badge";

const SAMPLE_ALERTS = [
  {
    id: "INC-2041",
    title: "SSH Brute-Force Burst",
    ip: "198.51.100.77",
    severity: "CRITICAL",
    time: "12s ago",
    badgeClass: "border-red-500/40 bg-red-500/15 text-red-400",
  },
  {
    id: "DET-8812",
    title: "Perimeter Port Scan (T1046)",
    ip: "203.0.113.45",
    severity: "HIGH",
    time: "1m ago",
    badgeClass: "border-orange-500/40 bg-orange-500/15 text-orange-400",
  },
  {
    id: "DET-8809",
    title: "Multi-IP Login Anomaly",
    ip: "185.220.101.4",
    severity: "MEDIUM",
    time: "4m ago",
    badgeClass: "border-amber-500/40 bg-amber-500/15 text-amber-400",
  },
];

const NETWORK_BARS = [28, 42, 35, 64, 52, 88, 74, 96, 61, 48, 72, 84];

export function HeroSocPreview() {
  return (
    <div className="relative rounded-xl border border-primary/25 bg-card/85 p-4 shadow-2xl shadow-primary/10 backdrop-blur-xl sm:p-5">
      {/* Subtle top glow */}
      <div className="pointer-events-none absolute -top-px right-12 left-12 h-px bg-gradient-to-r from-transparent via-cyan-400/70 to-transparent" />

      {/* Window Chrome */}
      <div className="flex items-center justify-between border-b border-border/60 pb-3">
        <div className="flex items-center gap-2">
          <span className="h-2.5 w-2.5 rounded-full bg-red-500/70" />
          <span className="h-2.5 w-2.5 rounded-full bg-amber-500/70" />
          <span className="h-2.5 w-2.5 rounded-full bg-emerald-500/70" />
          <span className="ml-2 font-mono text-[11px] text-muted-foreground">
            cyberpulse-soc // live-telemetry
          </span>
        </div>
        <div className="flex items-center gap-2">
          <Badge
            variant="outline"
            className="border-emerald-500/40 bg-emerald-500/10 font-mono text-[10px] text-emerald-400"
          >
            <span className="mr-1.5 inline-block h-1.5 w-1.5 animate-pulse rounded-full bg-emerald-400" />
            LIVE DEMO
          </Badge>
        </div>
      </div>

      {/* Top Mini KPIs */}
      <div className="mt-4 grid grid-cols-3 gap-2.5">
        <div className="rounded-lg border border-border/60 bg-background/60 p-2.5">
          <div className="flex items-center justify-between text-[11px] text-muted-foreground">
            <span>Active Incidents</span>
            <AlertTriangle className="h-3.5 w-3.5 text-red-400" />
          </div>
          <div className="mt-1 text-lg font-bold tracking-tight">03</div>
          <div className="text-[10px] text-red-400">1 Critical SLA</div>
        </div>

        <div className="rounded-lg border border-border/60 bg-background/60 p-2.5">
          <div className="flex items-center justify-between text-[11px] text-muted-foreground">
            <span>Risk Score</span>
            <ShieldAlert className="h-3.5 w-3.5 text-amber-400" />
          </div>
          <div className="mt-1 text-lg font-bold tracking-tight">84/100</div>
          <div className="text-[10px] text-amber-400">Elevated posture</div>
        </div>

        <div className="rounded-lg border border-border/60 bg-background/60 p-2.5">
          <div className="flex items-center justify-between text-[11px] text-muted-foreground">
            <span>Events / min</span>
            <Activity className="h-3.5 w-3.5 text-cyan-400" />
          </div>
          <div className="mt-1 text-lg font-bold tracking-tight">1,420</div>
          <div className="text-[10px] text-cyan-400">99.8% parsed</div>
        </div>
      </div>

      {/* Threat & Network Activity Visualization */}
      <div className="mt-3 rounded-lg border border-border/60 bg-background/60 p-3">
        <div className="mb-2 flex items-center justify-between text-xs">
          <span className="font-medium">Network &amp; Threat Activity</span>
          <span className="font-mono text-[10px] text-muted-foreground">
            Last 12 intervals
          </span>
        </div>

        <div className="flex h-20 items-end gap-1.5">
          {NETWORK_BARS.map((height, idx) => (
            <div
              key={idx}
              className="group relative flex-1 rounded-t bg-gradient-to-t from-primary/40 via-cyan-400/70 to-violet-400/90 transition-all duration-300 hover:brightness-125"
              style={{ height: `${height}%` }}
            />
          ))}
        </div>
      </div>

      {/* Recent Security Alerts */}
      <div className="mt-3 space-y-1.5">
        <div className="flex items-center justify-between text-xs">
          <span className="font-medium">Security Events Timeline</span>
          <span className="font-mono text-[10px] text-cyan-400">
            SSE Stream Connected
          </span>
        </div>

        {SAMPLE_ALERTS.map((alert) => (
          <div
            key={alert.id}
            className="flex items-center justify-between rounded-lg border border-border/50 bg-background/50 px-3 py-2 text-xs transition-colors hover:border-primary/40"
          >
            <div className="min-w-0">
              <div className="truncate font-medium">
                <span className="mr-1.5 font-mono text-[11px] text-muted-foreground">
                  {alert.id}
                </span>
                {alert.title}
              </div>
              <div className="font-mono text-[10px] text-muted-foreground">
                src={alert.ip} · {alert.time}
              </div>
            </div>
            <Badge variant="outline" className={alert.badgeClass}>
              {alert.severity}
            </Badge>
          </div>
        ))}
      </div>

      {/* AI Threat Analysis Panel */}
      <div className="mt-3 rounded-lg border border-violet-500/30 bg-violet-500/10 p-3 text-xs">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1.5 font-semibold text-violet-300">
            <BrainCircuit className="h-3.5 w-3.5" />
            <span>AI Threat Triage</span>
          </div>
          <span className="font-mono text-[10px] text-violet-300/80">
            Confidence 96%
          </span>
        </div>
        <p className="mt-1.5 leading-relaxed text-muted-foreground">
          Coordinated SSH credential-stuffing burst from{" "}
          <code className="font-mono text-foreground">198.51.100.77</code>{" "}
          mapped to <span className="text-foreground">MITRE T1110</span>.
          Recommended: auto-block IP &amp; revoke active sessions.
        </p>
      </div>
    </div>
  );
}
