import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  SeverityBadge,
  formatDateTime,
} from "@/components/soc/SeverityBadge";
import { api } from "@/lib/api";
import type { PageResponse, RiskAssessment, Severity } from "@/types/api";

export function RiskAssessmentsPage() {
  const [page, setPage] = useState(0);
  const [severity, setSeverity] = useState<string>("");

  const { data, isLoading } = useQuery({
    queryKey: ["risk-assessments", page, severity],
    queryFn: async () => {
      const url = severity
        ? `/api/v1/risk-assessments/severity/${severity}?page=${page}&size=15`
        : `/api/v1/risk-assessments?page=${page}&size=15`;
      return (await api.get<PageResponse<RiskAssessment>>(url)).data;
    },
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">
            Risk Assessments
          </h1>
          <p className="text-sm text-muted-foreground">
            Calculated risk scores combining base severity, confidence, and
            frequency.
          </p>
        </div>

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
      </div>

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
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={5} className="text-center">
                    Loading risk assessments...
                  </TableCell>
                </TableRow>
              ) : data?.content.length ? (
                data.content.map((r) => (
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
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell
                    colSpan={5}
                    className="text-center text-muted-foreground"
                  >
                    No risk assessments found.
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
    </div>
  );
}
