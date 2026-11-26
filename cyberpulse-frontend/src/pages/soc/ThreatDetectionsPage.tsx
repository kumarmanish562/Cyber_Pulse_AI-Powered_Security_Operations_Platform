import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Eye, Gauge, Search, ShieldAlert, ShieldCheck } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { PageHeader } from "@/components/soc/PageHeader";
import { StatCard } from "@/components/soc/StatCard";
import { EmptyState, LoadingSpinner } from "@/components/soc/StateViews";
import { formatDateTime } from "@/utils/format";
import { api } from "@/lib/api";
import type { PageResponse, ThreatDetection } from "@/types/api";

export function ThreatDetectionsPage() {
  const [page, setPage] = useState(0);
  const [searchQuery, setSearchQuery] = useState("");
  const [selected, setSelected] = useState<ThreatDetection | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ["detections", page],
    queryFn: async () =>
      (
        await api.get<PageResponse<ThreatDetection>>(
          `/api/v1/detections?page=${page}&size=15`,
        )
      ).data,
  });

  const items = data?.content ?? [];
  const filtered = items.filter((d) => {
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase();
    return (
      d.ruleName.toLowerCase().includes(q) ||
      d.threatType.toLowerCase().includes(q) ||
      d.eventId.toLowerCase().includes(q)
    );
  });

  const highConf = items.filter((d) => d.confidenceScore >= 80).length;
  const avgConf = items.length
    ? Math.round(
        items.reduce((acc, d) => acc + d.confidenceScore, 0) / items.length,
      )
    : 0;

  return (
    <div className="space-y-6">
      <PageHeader
        title="Threat Detections"
        description="Alerts triggered by active detection rules against incoming security events."
        breadcrumbs={[
          { label: "Overview", to: "/dashboard" },
          { label: "Threat Detections" },
        ]}
      />

      <div className="grid gap-4 sm:grid-cols-3">
        <StatCard
          title="Total Detections"
          value={data?.totalElements ?? 0}
          subtitle="Rule-correlated alerts"
          icon={<ShieldAlert className="h-4 w-4 text-cyan-500" />}
          loading={isLoading}
          accentClassName="border-t-cyan-500/70"
        />
        <StatCard
          title="High Confidence (>=80%)"
          value={highConf}
          subtitle="Current page detections"
          icon={<ShieldCheck className="h-4 w-4 text-emerald-500" />}
          loading={isLoading}
          accentClassName="border-t-emerald-500/70"
        />
        <StatCard
          title="Average Confidence"
          value={`${avgConf}%`}
          subtitle="Mean confidence score in view"
          icon={<Gauge className="h-4 w-4 text-violet-500" />}
          loading={isLoading}
          accentClassName="border-t-violet-500/70"
        />
      </div>

      <Card>
        <CardHeader className="flex flex-row flex-wrap items-center justify-between gap-3">
          <CardTitle className="text-base">Detected Threats</CardTitle>
          <div className="relative">
            <Search className="pointer-events-none absolute top-2.5 left-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search rule, threat type..."
              className="h-9 w-56 pl-8 text-sm"
            />
          </div>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Rule</TableHead>
                <TableHead>Threat Type</TableHead>
                <TableHead>Confidence</TableHead>
                <TableHead>Event ID</TableHead>
                <TableHead>Detected At</TableHead>
                <TableHead className="text-right">Details</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={6}>
                    <LoadingSpinner label="Loading detections..." />
                  </TableCell>
                </TableRow>
              ) : filtered.length ? (
                filtered.map((d) => (
                  <TableRow key={d.id}>
                    <TableCell className="font-medium">{d.ruleName}</TableCell>
                    <TableCell className="font-mono text-xs">
                      {d.threatType}
                    </TableCell>
                    <TableCell>
                      <Badge
                        variant={
                          d.confidenceScore >= 80 ? "default" : "secondary"
                        }
                      >
                        {d.confidenceScore}%
                      </Badge>
                    </TableCell>
                    <TableCell className="font-mono text-xs">
                      {d.eventId}
                    </TableCell>
                    <TableCell className="text-xs text-muted-foreground">
                      {formatDateTime(d.createdAt)}
                    </TableCell>
                    <TableCell className="text-right">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => setSelected(d)}
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
                      title="No threat detections recorded"
                      description="Ingest security events or run the Attack Simulator to trigger detections."
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
              <div className="grid grid-cols-2 gap-2 rounded-lg border bg-muted/30 p-3">
                <div>
                  <span className="text-muted-foreground">Threat Type: </span>
                  <span className="font-mono font-medium">
                    {selected.threatType}
                  </span>
                </div>
                <div>
                  <span className="text-muted-foreground">Confidence: </span>
                  <span className="font-semibold">
                    {selected.confidenceScore}%
                  </span>
                </div>
                <div className="col-span-2">
                  <span className="text-muted-foreground">Event ID: </span>
                  <span className="font-mono">{selected.eventId}</span>
                </div>
                <div className="col-span-2">
                  <span className="text-muted-foreground">Detected At: </span>
                  <span>{formatDateTime(selected.createdAt)}</span>
                </div>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
