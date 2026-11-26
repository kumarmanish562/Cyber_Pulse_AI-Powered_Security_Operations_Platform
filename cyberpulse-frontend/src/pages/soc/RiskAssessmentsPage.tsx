import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { AlertTriangle, Eye, Gauge, ShieldAlert } from "lucide-react";
import {
  Bar,
  BarChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { PageHeader } from "@/components/soc/PageHeader";
import { SeverityBadge } from "@/components/soc/SeverityBadge";
import { StatCard } from "@/components/soc/StatCard";
import { EmptyState, LoadingSpinner } from "@/components/soc/StateViews";
import { formatDateTime } from "@/utils/format";
import { api } from "@/lib/api";
import type { PageResponse, RiskAssessment, Severity } from "@/types/api";

export function RiskAssessmentsPage() {
  const [page, setPage] = useState(0);
  const [severity, setSeverity] = useState<string>("");
  const [selected, setSelected] = useState<RiskAssessment | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ["risk-assessments", page, severity],
    queryFn: async () => {
      const url = severity
        ? `/api/v1/risk-assessments/severity/${severity}?page=${page}&size=15`
        : `/api/v1/risk-assessments?page=${page}&size=15`;
      return (await api.get<PageResponse<RiskAssessment>>(url)).data;
    },
  });

  const items = data?.content ?? [];
  const highCrit = items.filter(
    (r) => r.severity === "HIGH" || r.severity === "CRITICAL",
  ).length;
  const peakScore = items.length
    ? Math.max(...items.map((r) => r.riskScore))
    : 0;

  const chartData = items.slice(0, 8).map((r) => ({
    name: r.ruleName.slice(0, 14),
    score: r.riskScore,
  }));

  return (
    <div className="space-y-6">
      <PageHeader
        title="Risk Assessments"
        description="Calculated risk scores combining base severity, confidence, and frequency."
        breadcrumbs={[
          { label: "Overview", to: "/dashboard" },
          { label: "Risk Assessments" },
        ]}
        actions={
          <select
            className="h-9 rounded-md border bg-background px-3 text-sm"
            value={severity}
            onChange={(e) => {
              setSeverity(e.target.value as Severity | "");
              setPage(0);
            }}
          >
            <option value="">All Severities</option>
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
            <option value="CRITICAL">CRITICAL</option>
          </select>
        }
      />

      <div className="grid gap-4 sm:grid-cols-3">
        <StatCard
          title="Total Assessments"
          value={data?.totalElements ?? 0}
          subtitle="Evaluated threat detections"
          icon={<Gauge className="h-4 w-4 text-violet-500" />}
          loading={isLoading}
          accentClassName="border-t-violet-500/70"
        />
        <StatCard
          title="High / Critical (Page)"
          value={highCrit}
          subtitle="Priority risk items in view"
          icon={<AlertTriangle className="h-4 w-4 text-red-500" />}
          loading={isLoading}
          accentClassName="border-t-red-500/70"
        />
        <StatCard
          title="Peak Risk Score"
          value={peakScore}
          subtitle="Highest score on current page"
          icon={<ShieldAlert className="h-4 w-4 text-amber-500" />}
          loading={isLoading}
          accentClassName="border-t-amber-500/70"
        />
      </div>

      {chartData.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Risk Score Distribution</CardTitle>
            <CardDescription>
              Top assessed rules by composite risk score
            </CardDescription>
          </CardHeader>
          <CardContent className="h-52">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData}>
                <XAxis dataKey="name" fontSize={11} />
                <YAxis fontSize={11} />
                <Tooltip
                  contentStyle={{
                    borderRadius: "8px",
                    border: "1px solid var(--border)",
                    backgroundColor: "var(--card)",
                    color: "var(--card-foreground)",
                  }}
                />
                <Bar dataKey="score" fill="#8b5cf6" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      )}

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Assessed Threats</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Rule</TableHead>
                <TableHead>Total Score</TableHead>
                <TableHead>Severity</TableHead>
                <TableHead>Breakdown (Base / Conf / Freq / Sev)</TableHead>
                <TableHead>Assessed At</TableHead>
                <TableHead className="text-right">Details</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={6}>
                    <LoadingSpinner label="Loading risk assessments..." />
                  </TableCell>
                </TableRow>
              ) : items.length ? (
                items.map((r) => (
                  <TableRow key={r.id}>
                    <TableCell className="font-medium">{r.ruleName}</TableCell>
                    <TableCell className="font-bold">{r.riskScore}</TableCell>
                    <TableCell>
                      <SeverityBadge severity={r.severity} />
                    </TableCell>
                    <TableCell className="font-mono text-xs text-muted-foreground">
                      {r.baseScore} / {r.confidenceScore} / {r.frequencyScore} /{" "}
                      {r.severityScore}
                    </TableCell>
                    <TableCell className="text-xs text-muted-foreground">
                      {formatDateTime(r.assessedAt)}
                    </TableCell>
                    <TableCell className="text-right">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => setSelected(r)}
                      >
                        <Eye className="h-3.5 w-3.5" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={6}>
                    <EmptyState
                      title="No risk assessments found"
                      description="Risk scores appear here once threat detections are evaluated."
                    />
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>

          <div className="mt-4 flex items-center justify-between text-xs text-muted-foreground">
            <span>
              Page {(data?.page ?? 0) + 1} of {Math.max(1, data?.totalPages ?? 1)}
            </span>
            <div className="flex gap-2">
              <Button
                variant="outline"
                size="sm"
                disabled={!data || data.first}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                Previous
              </Button>
              <Button
                variant="outline"
                size="sm"
                disabled={!data || data.last}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      <Dialog
        open={Boolean(selected)}
        onOpenChange={(v) => !v && setSelected(null)}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{selected?.ruleName}</DialogTitle>
          </DialogHeader>
          {selected && (
            <div className="space-y-3 text-xs">
              <div className="flex items-center justify-between">
                <span className="text-muted-foreground">Severity</span>
                <SeverityBadge severity={selected.severity} />
              </div>
              <div className="grid grid-cols-2 gap-2 rounded-lg border bg-muted/30 p-3">
                <div>
                  <span className="text-muted-foreground">Total Score: </span>
                  <span className="font-bold">{selected.riskScore}</span>
                </div>
                <div>
                  <span className="text-muted-foreground">Base Score: </span>
                  <span className="font-mono">{selected.baseScore}</span>
                </div>
                <div>
                  <span className="text-muted-foreground">Confidence: </span>
                  <span className="font-mono">{selected.confidenceScore}</span>
                </div>
                <div>
                  <span className="text-muted-foreground">Frequency: </span>
                  <span className="font-mono">{selected.frequencyScore}</span>
                </div>
                <div>
                  <span className="text-muted-foreground">
                    Severity Weight:{" "}
                  </span>
                  <span className="font-mono">{selected.severityScore}</span>
                </div>
                <div>
                  <span className="text-muted-foreground">Assessed: </span>
                  <span>{formatDateTime(selected.assessedAt)}</span>
                </div>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
