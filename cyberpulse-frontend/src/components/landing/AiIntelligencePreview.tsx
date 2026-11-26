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
    <div className="overflow-hidden rounded-2xl border border-border bg-card shadow-lg shadow-black/5">
      <div className="flex flex-wrap items-center justify-between gap-2 border-b border-border/70 bg-muted/30 px-5 py-3.5">
        <div className="flex items-center gap-2 text-xs font-semibold">
          <BrainCircuit className="h-4 w-4 text-foreground" />
          <span>CyberPulse AI Triage Console</span>
        </div>
        <Badge
          variant="outline"
          className="rounded-full font-mono text-[10px] text-muted-foreground"
        >
          Interactive Preview
        </Badge>
      </div>

      <div className="p-5">
        {/* Sample Event Tabs */}
        <div className="flex flex-wrap gap-2">
          {SAMPLES.map((sample) => (
            <button
              key={sample.id}
              type="button"
              onClick={() => setSelectedId(sample.id)}
              className={cn(
                "rounded-full border px-3.5 py-1.5 text-xs font-medium transition-all",
                sample.id === current.id
                  ? "border-foreground bg-foreground text-background"
                  : "border-border bg-background text-muted-foreground hover:border-foreground/30 hover:text-foreground",
              )}
            >
              {sample.label}
            </button>
          ))}
        </div>

        {/* Analysis Output */}
        <div className="mt-4 space-y-4 rounded-xl border border-border/70 bg-background/70 p-4 text-xs">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <div className="flex items-center gap-2">
              <ShieldAlert className="h-4 w-4 text-amber-500" />
              <span className="font-mono font-semibold">{current.mitre}</span>
              <span className="text-muted-foreground">·</span>
              <code className="font-mono text-muted-foreground">
                {current.sourceIp}
              </code>
            </div>
            <Badge
              variant="outline"
              className="rounded-full border-red-500/30 bg-red-500/10 text-red-500"
            >
              Risk {current.risk}
            </Badge>
          </div>

          <p className="leading-relaxed text-muted-foreground">
            {current.summary}
          </p>

          <div className="space-y-2 border-t border-border/70 pt-3">
            <div className="text-[11px] font-semibold tracking-wider text-muted-foreground uppercase">
              Recommended Containment
            </div>
            {current.actions.map((action) => (
              <div key={action} className="flex items-start gap-2">
                <CheckCircle2 className="mt-0.5 h-3.5 w-3.5 shrink-0 text-emerald-500" />
                <span className="text-foreground/90">{action}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
