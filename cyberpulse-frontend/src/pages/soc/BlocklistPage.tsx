import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Ban, Download, Plus } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { SeverityBadge } from "@/components/soc/SeverityBadge";
import { api, getApiErrorMessage } from "@/lib/api";
import type { BlockedIp, Severity } from "@/types/api";
import { downloadExportFile } from "@/utils/download";
import { formatDateTime } from "@/utils/format";

const SEVERITIES: Severity[] = ["LOW", "MEDIUM", "HIGH", "CRITICAL"];

export function BlocklistPage() {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [ipAddress, setIpAddress] = useState("");
  const [reason, setReason] = useState("");
  const [severity, setSeverity] = useState<Severity>("HIGH");

  const { data: items, isLoading } = useQuery({
    queryKey: ["blocklist"],
    queryFn: async () =>
      (await api.get<BlockedIp[]>("/api/v1/blocklist")).data,
  });

  const blockMutation = useMutation({
    mutationFn: () =>
      api.post<BlockedIp>("/api/v1/blocklist", { ipAddress, reason, severity }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["blocklist"] });
      toast.success("IP added to active blocklist");
      setIpAddress("");
      setReason("");
      setOpen(false);
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const unblockMutation = useMutation({
    mutationFn: (id: string) => api.delete(`/api/v1/blocklist/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["blocklist"] });
      toast.success("IP unblocked");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">IP Blocklist</h1>
          <p className="text-sm text-muted-foreground">
            Active containment list populated automatically from high-risk
            incidents or manually by SOC analysts.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() =>
              downloadExportFile(
                "/api/v1/blocklist/feed",
                "cyberpulse-blocklist.txt",
              ).catch(() => toast.error("Failed to download feed"))
            }
          >
            <Download className="mr-1 h-4 w-4" />
            Firewall Feed (.txt)
          </Button>

          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger render={<Button />}>
              <Plus className="mr-2 h-4 w-4" />
              Block IP
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle className="flex items-center gap-2">
                  <Ban className="h-4 w-4 text-destructive" />
                  Block Source IP
                </DialogTitle>
              </DialogHeader>
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  blockMutation.mutate();
                }}
                className="space-y-4"
              >
                <div className="space-y-1">
                  <Label>IP Address</Label>
                  <Input
                    required
                    value={ipAddress}
                    onChange={(e) => setIpAddress(e.target.value)}
                    placeholder="203.0.113.77"
                  />
                </div>
                <div className="space-y-1">
                  <Label>Severity</Label>
                  <select
                    className="flex h-9 w-full rounded-md border bg-background px-3 text-sm"
                    value={severity}
                    onChange={(e) => setSeverity(e.target.value as Severity)}
                  >
                    {SEVERITIES.map((s) => (
                      <option key={s} value={s}>
                        {s}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1">
                  <Label>Reason</Label>
                  <Input
                    required
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    placeholder="Repeated brute-force attempts"
                  />
                </div>
                <DialogFooter>
                  <Button type="submit" disabled={blockMutation.isPending}>
                    {blockMutation.isPending ? "Blocking..." : "Block IP"}
                  </Button>
                </DialogFooter>
              </form>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Active Containment List</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>IP Address</TableHead>
                <TableHead>Source</TableHead>
                <TableHead>Severity</TableHead>
                <TableHead>Reason</TableHead>
                <TableHead>Expires</TableHead>
                <TableHead className="text-right">Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center">
                    Loading blocklist...
                  </TableCell>
                </TableRow>
              ) : items?.length ? (
                items.map((item) => (
                  <TableRow key={item.id}>
                    <TableCell className="font-mono text-xs font-semibold">
                      {item.ipAddress}
                    </TableCell>
                    <TableCell>
                      <Badge variant="secondary">{item.source}</Badge>
                    </TableCell>
                    <TableCell>
                      <SeverityBadge severity={item.severity} />
                    </TableCell>
                    <TableCell className="max-w-xs truncate text-xs text-muted-foreground">
                      {item.reason}
                    </TableCell>
                    <TableCell className="text-xs text-muted-foreground">
                      {item.expiresAt
                        ? formatDateTime(item.expiresAt)
                        : "Permanent"}
                    </TableCell>
                    <TableCell className="text-right">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => unblockMutation.mutate(item.id)}
                      >
                        Unblock
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell
                    colSpan={6}
                    className="text-center text-muted-foreground"
                  >
                    No active blocked IPs.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}
