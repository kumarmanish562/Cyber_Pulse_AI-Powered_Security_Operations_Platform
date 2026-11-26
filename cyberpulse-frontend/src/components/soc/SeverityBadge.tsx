import { Badge } from "@/components/ui/badge";
import type { IncidentStatus, Severity } from "@/types/api";

const SEVERITY_STYLES: Record<Severity, string> = {
  CRITICAL: "border-red-500/40 bg-red-500/15 text-red-500",
  HIGH: "border-orange-500/40 bg-orange-500/15 text-orange-500",
  MEDIUM: "border-amber-500/40 bg-amber-500/15 text-amber-500",
  LOW: "border-emerald-500/40 bg-emerald-500/15 text-emerald-500",
};

const STATUS_STYLES: Record<IncidentStatus, string> = {
  OPEN: "border-red-500/40 bg-red-500/15 text-red-500",
  INVESTIGATING: "border-amber-500/40 bg-amber-500/15 text-amber-500",
  RESOLVED: "border-emerald-500/40 bg-emerald-500/15 text-emerald-500",
  CLOSED: "border-slate-500/40 bg-slate-500/15 text-slate-400",
};

export function SeverityBadge({ severity }: { severity: Severity }) {
  return (
    <Badge variant="outline" className={SEVERITY_STYLES[severity]}>
      {severity}
    </Badge>
  );
}

export function IncidentStatusBadge({ status }: { status: IncidentStatus }) {
  return (
    <Badge variant="outline" className={STATUS_STYLES[status]}>
      {status}
    </Badge>
  );
}
