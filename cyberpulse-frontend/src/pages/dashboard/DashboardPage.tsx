import { useQuery } from "@tanstack/react-query";
import {
  Activity,
  AlertTriangle,
  Clock,
  Gauge,
  ShieldAlert,
} from "lucide-react";
import { Badge } from "@/components/ui/badge";
import {
  Bar,
  BarChart,
  Cell,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  IncidentStatusBadge,
  SeverityBadge,
} from "@/components/soc/SeverityBadge";
import { formatDateTime } from "@/utils/format";
import { api } from "@/lib/api";
import type {
  Incident,
  PageResponse,
  RiskAssessment,
  SecurityEvent,
  SocPostureResponse,
  SpringPage,
  ThreatDetection,
} from "@/types/api";

export function DashboardPage() {
  const { data: eventsPage, isLoading: eventsLoading } = useQuery({
    queryKey: ["dashboard", "events"],
    queryFn: async () =>
      (await api.get<SpringPage<SecurityEvent>>("/api/v1/events?size=50")).data,
  });

  const { data: detectionsPage, isLoading: detectionsLoading } = useQuery({
    queryKey: ["dashboard", "detections"],
    queryFn: async () =>
      (
        await api.get<PageResponse<ThreatDetection>>(
          "/api/v1/detections?size=5",
        )
      ).data,
  });

  const { data: riskPage, isLoading: riskLoading } = useQuery({
    queryKey: ["dashboard", "risk"],
    queryFn: async () =>
      (
        await api.get<PageResponse<RiskAssessment>>(
          "/api/v1/risk-assessments?size=5",
        )
      ).data,
  });

  const { data: incidentsPage, isLoading: incidentsLoading } = useQuery({
    queryKey: ["dashboard", "incidents"],
    queryFn: async () =>
      (await api.get<PageResponse<Incident>>("/api/v1/incidents?size=5"))
        .data,
  });

  const { data: posture } = useQuery({
    queryKey: ["dashboard", "soc-posture"],
    queryFn: async () =>
      (await api.get<SocPostureResponse>("/api/v1/soc/posture")).data,
  });

  const severityCounts = ["LOW", "MEDIUM", "HIGH", "CRITICAL"].map((sev) => ({
    severity: sev,
    count:
      eventsPage?.content.filter((e) => e.severity === sev).length ?? 0,
  }));

  const isLoading =
    eventsLoading || detectionsLoading || riskLoading || incidentsLoading;

  const SEVERITY_BAR_COLORS: Record<string, string> = {
    LOW: "#10b981",
    MEDIUM: "#f59e0b",
    HIGH: "#f97316",
    CRITICAL: "#ef4444",
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">
          CyberPulse Security Operations Center
        </h1>
        <p className="mt-1 text-muted-foreground">
          Live overview of ingested events, threat detections, risk scores, and
          active incidents.
        </p>
      </div>

      {/* KPI Cards */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <KpiCard
          title="Security Events"
          value={eventsPage?.totalElements ?? 0}
          icon={<Activity className="h-4 w-4 text-muted-foreground" />}
          loading={isLoading}
        />
        <KpiCard
          title="Threat Detections"
          value={detectionsPage?.totalElements ?? 0}
          icon={<ShieldAlert className="h-4 w-4 text-muted-foreground" />}
          loading={isLoading}
        />
        <KpiCard
          title="Risk Assessments"
          value={riskPage?.totalElements ?? 0}
          icon={<Gauge className="h-4 w-4 text-muted-foreground" />}
          loading={isLoading}
        />
        <KpiCard
          title="Total Incidents"
          value={incidentsPage?.totalElements ?? 0}
          icon={<AlertTriangle className="h-4 w-4 text-muted-foreground" />}
          loading={isLoading}
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-7">
        {/* Severity Chart */}
        <Card className="lg:col-span-3">
          <CardHeader>
            <CardTitle>Recent Event Severity</CardTitle>
            <CardDescription>
              Distribution across the latest 50 security events
            </CardDescription>
          </CardHeader>
          <CardContent className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={severityCounts}>
                <XAxis dataKey="severity" fontSize={12} />
                <YAxis allowDecimals={false} fontSize={12} />
                <Tooltip
                  contentStyle={{
                    borderRadius: "8px",
                    border: "1px solid var(--border)",
                    backgroundColor: "var(--card)",
                    color: "var(--card-foreground)",
                  }}
                />
                <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                  {severityCounts.map((entry) => (
                    <Cell
                      key={entry.severity}
                      fill={SEVERITY_BAR_COLORS[entry.severity] ?? "#3b82f6"}
                    />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Recent Incidents */}
        <Card className="lg:col-span-4">
          <CardHeader>
            <CardTitle>Recent Incidents</CardTitle>
            <CardDescription>
              Latest incidents generated manually or from high-risk assessments
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Number</TableHead>
                  <TableHead>Title</TableHead>
                  <TableHead>Severity</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Created</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {incidentsPage?.content.length ? (
                  incidentsPage.content.map((inc) => (
                    <TableRow key={inc.id}>
                      <TableCell className="font-mono text-xs">
                        {inc.incidentNumber}
                      </TableCell>
                      <TableCell className="max-w-[180px] truncate font-medium">
                        {inc.title}
                      </TableCell>
                      <TableCell>
                        <SeverityBadge severity={inc.severity} />
                      </TableCell>
                      <TableCell>
                        <IncidentStatusBadge status={inc.status} />
                      </TableCell>
                      <TableCell className="text-xs text-muted-foreground">
                        {formatDateTime(inc.createdAt)}
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell
                      colSpan={5}
                      className="text-center text-muted-foreground"
                    >
                      No incidents recorded yet.
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>

      {/* SLA & MITRE ATT&CK Posture */}
      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Clock className="h-4 w-4 text-primary" />
              <span>Incident SLA Monitor</span>
              {posture && posture.slaBreachedCount > 0 && (
                <Badge variant="destructive" className="ml-auto">
                  {posture.slaBreachedCount} Breached
                </Badge>
              )}
            </CardTitle>
            <CardDescription>
              Open &amp; investigating incidents tracked against response SLAs
              (Critical 15m / High 60m)
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-2">
            {posture?.slaAlerts.length ? (
              posture.slaAlerts.slice(0, 5).map((sla) => (
                <div
                  key={sla.incidentId}
                  className="flex items-center justify-between rounded border px-3 py-2 text-xs"
                >
                  <div className="min-w-0">
                    <div className="truncate font-medium">
                      <span className="font-mono text-muted-foreground">
                        {sla.incidentNumber}
                      </span>{" "}
                      {sla.title}
                    </div>
                    <div className="text-muted-foreground">
                      Elapsed {sla.elapsedMinutes}m / Target{" "}
                      {sla.slaTargetMinutes}m
                    </div>
                  </div>
                  <Badge variant={sla.breached ? "destructive" : "secondary"}>
                    {sla.breached ? "SLA BREACHED" : sla.severity}
                  </Badge>
                </div>
              ))
            ) : (
              <p className="text-xs text-muted-foreground">
                No active open incidents breaching SLA targets.
              </p>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>MITRE ATT&amp;CK Coverage</CardTitle>
            <CardDescription>
              Observed adversary tactics and techniques from threat detections
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-2">
            {posture?.mitreTechniques.length ? (
              posture.mitreTechniques.map((m) => (
                <div
                  key={m.techniqueId}
                  className="flex items-center justify-between rounded border px-3 py-2 text-xs"
                >
                  <div>
                    <span className="font-mono font-semibold text-primary">
                      {m.techniqueId}
                    </span>{" "}
                    <span className="font-medium">{m.techniqueName}</span>
                    <div className="text-muted-foreground">
                      {m.tacticId} — {m.tacticName}
                    </div>
                  </div>
                  <Badge variant="outline">{m.detectionCount} hits</Badge>
                </div>
              ))
            ) : (
              <p className="text-xs text-muted-foreground">
                No MITRE ATT&amp;CK detections recorded yet.
              </p>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

function KpiCard({
  title,
  value,
  icon,
  loading,
}: {
  title: string;
  value: number;
  icon: React.ReactNode;
  loading: boolean;
}) {
  return (
    <Card className="border-t-2 border-t-primary/60">
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <CardTitle className="text-sm font-medium text-muted-foreground">
          {title}
        </CardTitle>
        {icon}
      </CardHeader>
      <CardContent>
        {loading ? (
          <Skeleton className="h-8 w-20" />
        ) : (
          <div className="text-3xl font-bold">{value}</div>
        )}
      </CardContent>
    </Card>
  );
}
