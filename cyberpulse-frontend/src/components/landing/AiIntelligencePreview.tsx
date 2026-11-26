import { useState } from "react";
import { BrainCircuit, CheckCircle2, ShieldAlert } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

const SAMPLES = [
  {
    id: "brute-force",
    label: "SSH Brute-Force Burst",
    mitre: "TA0006 · T1110",
    risk: "92 / CRITICAL",
    sourceIp: "198.51.100.77",
    summary:
      "12 consecutive failed SSH logins targeting root on ssh-gateway within 18 seconds. Pattern matches automated credential stuffing.",
    actions: [
      "Add 198.51.100.77 to edge firewall blocklist (24h TTL)",
      "Verify zero successful logins for affected accounts",
      "Enforce MFA & rotate credentials if any session matched",
    ],
  },
  {
    id: "port-scan",
    label: "Perimeter Port Scan",
    mitre: "TA0043 · T1046",
    risk: "78 / HIGH",
    sourceIp: "203.0.113.45",
    summary:
      "Sequential TCP SYN probes across 6 exposed service ports on edge-firewall. Indicates active external reconnaissance.",
    actions: [
      "Drop inbound traffic from 203.0.113.45 at perimeter WAF",
      "Audit open ports and close unused management interfaces",
      "Correlate follow-on unauthorized access attempts",
    ],
  },
  {
    id: "impossible-travel",
    label: "Multi-IP Login Anomaly",
    mitre: "TA0001 · T1078",
    risk: "68 / MEDIUM",
    sourceIp: "185.220.101.4",
    summary:
      "Account soc_executive authenticated from 3 distinct ASN/IPs within 30 seconds on sso-portal, suggesting token replay.",
    actions: [
      "Revoke active refresh tokens for soc_executive",
      "Require step-up TOTP verification on next login",
      "Inspect audit logs for privilege escalation events",
    ],
  },
];

export function AiIntelligencePreview() {
  const [selectedId, setSelectedId] = useState(SAMPLES[0].id);
  const current = SAMPLES.find((s) => s.id === selectedId) ?? SAMPLES[0];

  return (
    <div className="rounded-xl border border-border/70 bg-card/85 p-5 shadow-xl backdrop-blur-xl">
      <div className="flex flex-wrap items-center justify-between gap-2 border-b border-border/60 pb-4">
        <div className="flex items-center gap-2 text-sm font-semibold">
          <BrainCircuit className="h-4 w-4 text-violet-400" />
          <span>CyberPulse AI Triage Console</span>
        </div>
        <Badge variant="outline" className="font-mono text-[10px]">
          Sample Interactive Preview
        </Badge>
      </div>

      {/* Sample Event Tabs */}
      <div className="mt-4 flex flex-wrap gap-2">
        {SAMPLES.map((sample) => (
          <button
            key={sample.id}
            type="button"
            onClick={() => setSelectedId(sample.id)}
            className={cn(
              "rounded-md border px-3 py-1.5 text-xs font-medium transition-colors",
              sample.id === current.id
                ? "border-primary bg-primary/15 text-primary"
                : "border-border/60 bg-background/50 text-muted-foreground hover:text-foreground",
            )}
          >
            {sample.label}
          </button>
        ))}
      </div>

      {/* Analysis Output */}
      <div className="mt-4 space-y-4 rounded-lg border border-border/60 bg-background/60 p-4 text-xs">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <div className="flex items-center gap-2">
            <ShieldAlert className="h-4 w-4 text-amber-400" />
            <span className="font-mono font-semibold">{current.mitre}</span>
            <span className="text-muted-foreground">·</span>
            <code className="font-mono text-cyan-400">{current.sourceIp}</code>
          </div>
          <Badge variant="outline" className="border-red-500/40 bg-red-500/10 text-red-400">
            Risk {current.risk}
          </Badge>
        </div>

        <p className="leading-relaxed text-muted-foreground">
          {current.summary}
        </p>

        <div className="space-y-2 border-t border-border/50 pt-3">
          <div className="font-semibold text-foreground">
            Recommended Containment Steps
          </div>
          {current.actions.map((action) => (
            <div
              key={action}
              className="flex items-start gap-2 text-muted-foreground"
            >
              <CheckCircle2 className="mt-0.5 h-3.5 w-3.5 shrink-0 text-emerald-400" />
              <span>{action}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
