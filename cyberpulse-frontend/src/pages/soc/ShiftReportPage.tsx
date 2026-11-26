import { useQuery } from "@tanstack/react-query";
import { Download } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { PageHeader } from "@/components/soc/PageHeader";
import { LoadingSpinner } from "@/components/soc/StateViews";
import { api } from "@/lib/api";
import type { ShiftHandoverReport } from "@/types/api";
import { downloadExportFile } from "@/utils/download";
import { formatDateTime } from "@/utils/format";

export function ShiftReportPage() {
  const { data: report, isLoading } = useQuery({
    queryKey: ["reports", "shift-handover"],
    queryFn: async () =>
      (await api.get<ShiftHandoverReport>("/api/v1/reports/shift-handover"))
        .data,
  });

  return (
    <div className="space-y-6">
      <PageHeader
        title="SOC Shift Handover Report"
        description="24-hour executive posture, SLA compliance, and MITRE ATT&CK handover summary."
        breadcrumbs={[
          { label: "Overview", to: "/dashboard" },
          { label: "Shift Reports" },
        ]}
        actions={
          <Button
            onClick={() =>
              downloadExportFile(
                "/api/v1/reports/shift-handover/download",
                "soc-shift-handover.md",
              ).catch(() => toast.error("Failed to download report"))
            }
          >
            <Download className="mr-2 h-4 w-4" />
            Download Markdown (.md)
          </Button>
        }
      />

      {isLoading || !report ? (
        <Card>
          <CardContent className="p-6">
            <LoadingSpinner label="Generating 24-hour SOC shift handover report..." />
          </CardContent>
        </Card>
      ) : (
        <>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
            <MetricCard label="Events (24h)" value={report.totalEvents24h} />
            <MetricCard
              label="Detections (24h)"
              value={report.totalDetections24h}
            />
            <MetricCard label="Open Incidents" value={report.openIncidents} />
            <MetricCard
              label="SLA Compliance"
              value={`${report.slaCompliancePercent}%`}
            />
            <MetricCard
              label="Blocked IPs"
              value={report.activeBlockedIps}
            />
          </div>

          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <CardTitle className="text-base">
                  Handover Brief Preview
                </CardTitle>
                <Badge variant="outline">
                  Generated {formatDateTime(report.generatedAt)}
                </Badge>
              </div>
            </CardHeader>
            <CardContent>
              <pre className="overflow-x-auto rounded-md border bg-muted/40 p-4 font-mono text-xs leading-relaxed whitespace-pre-wrap">
                {report.markdownSummary}
              </pre>
            </CardContent>
          </Card>
        </>
      )}
    </div>
  );
}

function MetricCard({ label, value }: { label: string; value: string | number }) {
  return (
    <Card>
      <CardHeader className="pb-2">
        <CardTitle className="text-xs font-medium text-muted-foreground">
          {label}
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">{value}</div>
      </CardContent>
    </Card>
  );
}
