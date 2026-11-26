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
import { formatDateTime } from "@/components/soc/SeverityBadge";
import { api } from "@/lib/api";
import type { PageResponse, ThreatDetection } from "@/types/api";

export function ThreatDetectionsPage() {
  const [page, setPage] = useState(0);

  const { data, isLoading } = useQuery({
    queryKey: ["detections", page],
    queryFn: async () =>
      (
        await api.get<PageResponse<ThreatDetection>>(
          `/api/v1/detections?page=${page}&size=15`,
        )
      ).data,
  });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Threat Detections</h1>
        <p className="text-sm text-muted-foreground">
          Alerts triggered by active detection rules against incoming security
          events.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Detected Threats</CardTitle>
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
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={5} className="text-center">
                    Loading detections...
                  </TableCell>
                </TableRow>
              ) : data?.content.length ? (
                data.content.map((d) => (
                  <TableRow key={d.id}>
                    <TableCell className="font-medium">{d.ruleName}</TableCell>
                    <TableCell className="font-mono text-xs">
                      {d.threatType}
                    </TableCell>
                    <TableCell>{d.confidenceScore}%</TableCell>
                    <TableCell className="font-mono text-xs">
                      {d.eventId}
                    </TableCell>
                    <TableCell className="text-xs text-muted-foreground">
                      {formatDateTime(d.createdAt)}
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell
                    colSpan={5}
                    className="text-center text-muted-foreground"
                  >
                    No threat detections recorded.
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
