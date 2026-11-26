import { Activity, AlertTriangle, BrainCircuit, ShieldAlert } from "lucide-react";
import { Badge } from "@/components/ui/badge";

const SAMPLE_ALERTS = [
  {
    id: "INC-2041",
    title: "SSH Brute-Force Burst",
    ip: "198.51.100.77",
    severity: "CRITICAL",
    time: "12s ago",
    dotClass: "bg-red-500",
    badgeClass: "border-red-500/30 bg-red-500/10 text-red-500",
  },
  {
    id: "DET-8812",
    title: "Perimeter Port Scan (T1046)",
    ip: "203.0.113.45",
    severity: "HIGH",
    time: "1m ago",
    dotClass: "bg-orange-500",
    badgeClass: "border-orange-500/30 bg-orange-500/10 text-orange-500",
  },
  {
    id: "DET-8809",
    title: "Multi-IP Login Anomaly",
    ip: "185.220.101.4",
    severity: "MEDIUM",
    time: "4m ago",
    dotClass: "bg-amber-500",
    badgeClass: "border-amber-500/30 bg-amber-500/10 text-amber-500",
  },
];

const NETWORK_BARS = [32, 46, 38, 68, 54, 86, 72, 94, 62, 50, 74, 82];

export function HeroSocPreview() {
  return (
    <div className="overflow-hidden rounded-2xl border border-border bg-card shadow-2xl shadow-black/10 dark:shadow-black/50">
      {/* Minimal Window Chrome */}
      <div className="flex items-center justify-between border-b border-border/70 bg-muted/30 px-4 py-3 sm:px-5">
        <div className="flex items-center gap-2">
          <span className="h-2.5 w-2.5 rounded-full bg-border" />
          <span className="h-2.5 w-2.5 rounded-full bg-border" />
          <span className="h-2.5 w-2.5 rounded-full bg-border" />
          <span className="ml-2 font-mono text-[11px] text-muted-foreground">
            cyberpulse.app / soc-overview
          </span>
        </div>
        <Badge
          variant="outline"
          className="rounded-full border-emerald-500/30 bg-emerald-500/10 px-2.5 py-0.5 font-mono text-[10px] text-emerald-500"
        >
          <span className="mr-1.5 inline-block h-1.5 w-1.5 animate-pulse rounded-full bg-emerald-500" />
          LIVE DEMO
        </Badge>
      </div>

      <div className="p-4 sm:p-6">
        {/* Top KPI Strip */}
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
          <div className="rounded-xl border border-border/70 bg-background/70 p-3.5">
            <div className="flex items-center justify-between text-xs text-muted-foreground">
              <span>Active Incidents</span>
              <AlertTriangle className="h-3.5 w-3.5 text-red-500" />
            </div>
            <div className="mt-1.5 flex items-baseline gap-2">
              <span className="text-2xl font-semibold tracking-tight">03</span>
              <span className="text-[11px] text-muted-foreground">
                1 Critical SLA
              </span>
            </div>
          </div>

          <div className="rounded-xl border border-border/70 bg-background/70 p-3.5">
            <div className="flex items-center justify-between text-xs text-muted-foreground">
              <span>Risk Score</span>
              <ShieldAlert className="h-3.5 w-3.5 text-amber-500" />
            </div>
            <div className="mt-1.5 flex items-baseline gap-2">
              <span className="text-2xl font-semibold tracking-tight">
                84/100
              </span>
              <span className="text-[11px] text-muted-foreground">
                Elevated posture
              </span>
            </div>
          </div>

          <div className="rounded-xl border border-border/70 bg-background/70 p-3.5">
            <div className="flex items-center justify-between text-xs text-muted-foreground">
              <span>Events / min</span>
              <Activity className="h-3.5 w-3.5 text-foreground/70" />
            </div>
            <div className="mt-1.5 flex items-baseline gap-2">
              <span className="text-2xl font-semibold tracking-tight">
                1,420
              </span>
              <span className="text-[11px] text-muted-foreground">
                99.8% parsed
              </span>
            </div>
          </div>
        </div>

        {/* Chart + Timeline Split */}
        <div className="mt-4 grid gap-4 lg:grid-cols-12">
          <div className="rounded-xl border border-border/70 bg-background/70 p-4 lg:col-span-5">
            <div className="mb-3 flex items-center justify-between text-xs">
              <span className="font-medium">Network &amp; Threat Activity</span>
              <span className="font-mono text-[10px] text-muted-foreground">
                12 intervals
              </span>
            </div>

            <div className="flex h-28 items-end gap-1.5">
              {NETWORK_BARS.map((height, idx) => (
                <div
                  key={idx}
                  className="flex-1 rounded-t-sm bg-foreground/20 transition-colors hover:bg-foreground/60"
                  style={{ height: `${height}%` }}
                />
              ))}
            </div>

            <div className="mt-4 rounded-lg border border-border/70 bg-muted/30 p-3 text-xs">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-1.5 font-medium">
                  <BrainCircuit className="h-3.5 w-3.5 text-foreground" />
                  <span>AI Threat Triage</span>
                </div>
                <span className="font-mono text-[10px] text-muted-foreground">
                  96% conf
                </span>
              </div>
              <p className="mt-1.5 leading-relaxed text-muted-foreground">
                SSH credential-stuffing burst from{" "}
                <code className="font-mono text-foreground">198.51.100.77</code>{" "}
                (MITRE T1110). Recommended: auto-block IP &amp; revoke sessions.
              </p>
            </div>
          </div>

          <div className="rounded-xl border border-border/70 bg-background/70 p-4 lg:col-span-7">
            <div className="mb-3 flex items-center justify-between text-xs">
              <span className="font-medium">Security Events Timeline</span>
              <span className="font-mono text-[10px] text-muted-foreground">
                SSE Stream Connected
              </span>
            </div>

            <div className="divide-y divide-border/60">
              {SAMPLE_ALERTS.map((alert) => (
                <div
                  key={alert.id}
                  className="flex items-center justify-between gap-3 py-3 first:pt-1 last:pb-1"
                >
                  <div className="flex min-w-0 items-center gap-3">
                    <span
                      className={`h-2 w-2 shrink-0 rounded-full ${alert.dotClass}`}
                    />
                    <div className="min-w-0">
                      <div className="truncate text-xs font-medium">
                        <span className="mr-1.5 font-mono text-[11px] text-muted-foreground">
                          {alert.id}
                        </span>
                        {alert.title}
                      </div>
                      <div className="font-mono text-[10px] text-muted-foreground">
                        src={alert.ip} · {alert.time}
                      </div>
                    </div>
                  </div>
                  <Badge variant="outline" className={alert.badgeClass}>
                    {alert.severity}
                  </Badge>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
