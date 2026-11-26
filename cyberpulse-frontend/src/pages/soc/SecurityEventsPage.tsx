import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Activity, Eye, Globe, Plus, Search, ShieldAlert } from "lucide-react";
import { toast } from "sonner";
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
import { PageHeader } from "@/components/soc/PageHeader";
import { SeverityBadge } from "@/components/soc/SeverityBadge";
import { StatCard } from "@/components/soc/StatCard";
import { EmptyState, LoadingSpinner } from "@/components/soc/StateViews";
import { formatDateTime } from "@/utils/format";
import { api, getApiErrorMessage } from "@/lib/api";
import type {
  CreateSecurityEventPayload,
  EventType,
  SecurityEvent,
  Severity,
  SpringPage,
} from "@/types/api";

const EVENT_TYPES: EventType[] = [
  "LOGIN_FAILED",
  "LOGIN_SUCCESS",
  "PORT_SCAN",
  "MALWARE_DETECTED",
  "SUSPICIOUS_LOGIN",
  "BRUTE_FORCE",
  "UNAUTHORIZED_ACCESS",
  "FILE_MODIFIED",
  "PRIVILEGE_ESCALATION",
];

const SEVERITIES: Severity[] = ["LOW", "MEDIUM", "HIGH", "CRITICAL"];

export function SecurityEventsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [severityFilter, setSeverityFilter] = useState("");
  const [eventTypeFilter, setEventTypeFilter] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedEvent, setSelectedEvent] = useState<SecurityEvent | null>(
    null,
  );
  const [open, setOpen] = useState(false);

  const [form, setForm] = useState<CreateSecurityEventPayload>({
    eventType: "LOGIN_FAILED",
    sourceIp: "192.168.1.50",
    username: "",
    service: "auth-gateway",
    message: "",
    severity: "MEDIUM",
    eventTime: new Date().toISOString(),
  });

  const { data, isLoading } = useQuery({
    queryKey: ["events", page, severityFilter, eventTypeFilter],
    queryFn: async () => {
      const params = new URLSearchParams({ page: String(page), size: "15" });
      if (severityFilter) params.set("severity", severityFilter);
      if (eventTypeFilter) params.set("eventType", eventTypeFilter);
      return (
        await api.get<SpringPage<SecurityEvent>>(`/api/v1/events?${params}`)
      ).data;
    },
  });

  const createMutation = useMutation({
    mutationFn: (payload: CreateSecurityEventPayload) =>
      api.post<SecurityEvent>("/api/v1/events", payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["events"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      toast.success("Security event ingested");
      setOpen(false);
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    createMutation.mutate({
      ...form,
      eventTime: new Date().toISOString(),
    });
  };

  const filteredEvents = (data?.content ?? []).filter((ev) => {
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase();
    return (
      ev.eventType.toLowerCase().includes(q) ||
      (ev.sourceIp ?? "").toLowerCase().includes(q) ||
      (ev.username ?? "").toLowerCase().includes(q) ||
      (ev.service ?? "").toLowerCase().includes(q) ||
      (ev.message ?? "").toLowerCase().includes(q)
    );
  });

  const highCritCount = (data?.content ?? []).filter(
    (e) => e.severity === "HIGH" || e.severity === "CRITICAL",
  ).length;
  const uniqueIps = new Set(
    (data?.content ?? []).map((e) => e.sourceIp).filter(Boolean),
  ).size;

  return (
    <div className="space-y-6">
      <PageHeader
        title="Security Events"
        description="Ingest and filter telemetry across authentication, network, and host sources."
        breadcrumbs={[
          { label: "Overview", to: "/dashboard" },
          { label: "Security Events" },
        ]}
        actions={
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger render={<Button />}>
              <Plus className="mr-2 h-4 w-4" />
              Ingest Event
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Ingest Security Event</DialogTitle>
              </DialogHeader>
              <form onSubmit={handleCreate} className="space-y-4">
                <div className="grid grid-cols-2 gap-3">
                  <div className="space-y-1">
                    <Label>Event Type</Label>
                    <select
                      className="flex h-9 w-full rounded-md border bg-background px-3 text-sm"
                      value={form.eventType}
                      onChange={(e) =>
                        setForm((f) => ({
                          ...f,
                          eventType: e.target.value as EventType,
                        }))
                      }
                    >
                      {EVENT_TYPES.map((t) => (
                        <option key={t} value={t}>
                          {t}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="space-y-1">
                    <Label>Severity</Label>
                    <select
                      className="flex h-9 w-full rounded-md border bg-background px-3 text-sm"
                      value={form.severity}
                      onChange={(e) =>
                        setForm((f) => ({
                          ...f,
                          severity: e.target.value as Severity,
                        }))
                      }
                    >
                      {SEVERITIES.map((s) => (
                        <option key={s} value={s}>
                          {s}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div className="space-y-1">
                    <Label>Source IP</Label>
                    <Input
                      value={form.sourceIp ?? ""}
                      onChange={(e) =>
                        setForm((f) => ({ ...f, sourceIp: e.target.value }))
                      }
                      placeholder="192.168.1.50"
                    />
                  </div>
                  <div className="space-y-1">
                    <Label>Username</Label>
                    <Input
                      value={form.username ?? ""}
                      onChange={(e) =>
                        setForm((f) => ({ ...f, username: e.target.value }))
                      }
                      placeholder="jdoe"
                    />
                  </div>
                </div>

                <div className="space-y-1">
                  <Label>Service</Label>
                  <Input
                    value={form.service ?? ""}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, service: e.target.value }))
                    }
                    placeholder="auth-gateway"
                  />
                </div>

                <div className="space-y-1">
                  <Label>Message</Label>
                  <Input
                    value={form.message ?? ""}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, message: e.target.value }))
                    }
                    placeholder="Multiple failed login attempts"
                  />
                </div>

                <DialogFooter>
                  <Button type="submit" disabled={createMutation.isPending}>
                    {createMutation.isPending ? "Ingesting..." : "Submit Event"}
                  </Button>
                </DialogFooter>
              </form>
            </DialogContent>
          </Dialog>
        }
      />

      <div className="grid gap-4 sm:grid-cols-3">
        <StatCard
          title="Total Events"
          value={data?.totalElements ?? 0}
          subtitle="Across all telemetry streams"
          icon={<Activity className="h-4 w-4 text-blue-500" />}
          loading={isLoading}
          accentClassName="border-t-blue-500/70"
        />
        <StatCard
          title="High / Critical (Page)"
          value={highCritCount}
          subtitle="Elevated severity events in view"
          icon={<ShieldAlert className="h-4 w-4 text-red-500" />}
          loading={isLoading}
          accentClassName="border-t-red-500/70"
        />
        <StatCard
          title="Unique Source IPs"
          value={uniqueIps}
          subtitle="Distinct origins on current page"
          icon={<Globe className="h-4 w-4 text-cyan-500" />}
          loading={isLoading}
          accentClassName="border-t-cyan-500/70"
        />
      </div>

      <Card>
        <CardHeader className="flex flex-row flex-wrap items-center justify-between gap-3">
          <CardTitle className="text-base">Event Stream</CardTitle>
          <div className="flex flex-wrap gap-2">
            <div className="relative">
              <Search className="pointer-events-none absolute top-2.5 left-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search IP, user, service..."
                className="h-9 w-48 pl-8 text-sm"
              />
            </div>
            <select
              className="h-9 rounded-md border bg-background px-3 text-sm"
              value={eventTypeFilter}
              onChange={(e) => {
                setEventTypeFilter(e.target.value);
                setPage(0);
              }}
            >
              <option value="">All Event Types</option>
              {EVENT_TYPES.map((t) => (
                <option key={t} value={t}>
                  {t}
                </option>
              ))}
            </select>
            <select
              className="h-9 rounded-md border bg-background px-3 text-sm"
              value={severityFilter}
              onChange={(e) => {
                setSeverityFilter(e.target.value);
                setPage(0);
              }}
            >
              <option value="">All Severities</option>
              {SEVERITIES.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          </div>
        </CardHeader>

        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Type</TableHead>
                <TableHead>Severity</TableHead>
                <TableHead>Source IP</TableHead>
                <TableHead>User</TableHead>
                <TableHead>Service</TableHead>
                <TableHead>Event Time</TableHead>
                <TableHead className="text-right">Details</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={7}>
                    <LoadingSpinner label="Loading security events..." />
                  </TableCell>
                </TableRow>
              ) : filteredEvents.length ? (
                filteredEvents.map((ev) => (
                  <TableRow key={ev.id}>
                    <TableCell className="font-mono text-xs">
                      {ev.eventType}
                    </TableCell>
                    <TableCell>
                      <SeverityBadge severity={ev.severity} />
                    </TableCell>
                    <TableCell className="font-mono text-xs">
                      {ev.sourceIp ?? "—"}
                    </TableCell>
                    <TableCell>{ev.username ?? "—"}</TableCell>
                    <TableCell>{ev.service ?? "—"}</TableCell>
                    <TableCell className="text-xs text-muted-foreground">
                      {formatDateTime(ev.eventTime)}
                    </TableCell>
                    <TableCell className="text-right">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => setSelectedEvent(ev)}
                      >
                        <Eye className="h-3.5 w-3.5" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={7}>
                    <EmptyState
                      title="No security events found"
                      description="Ingest an event or adjust your active filters."
                    />
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>

          <div className="mt-4 flex items-center justify-between text-xs text-muted-foreground">
            <span>
              Page {(data?.number ?? 0) + 1} of {Math.max(1, data?.totalPages ?? 1)} (
              {data?.totalElements ?? 0} total)
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
        open={Boolean(selectedEvent)}
        onOpenChange={(v) => !v && setSelectedEvent(null)}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle className="font-mono text-sm">
              {selectedEvent?.eventType}
            </DialogTitle>
          </DialogHeader>
          {selectedEvent && (
            <div className="space-y-3 text-sm">
              <div className="flex items-center justify-between">
                <span className="text-muted-foreground">Severity</span>
                <SeverityBadge severity={selectedEvent.severity} />
              </div>
              <div className="grid grid-cols-2 gap-2 rounded-lg border bg-muted/30 p-3 text-xs">
                <div>
                  <span className="text-muted-foreground">Source IP: </span>
                  <span className="font-mono">
                    {selectedEvent.sourceIp ?? "—"}
                  </span>
                </div>
                <div>
                  <span className="text-muted-foreground">Username: </span>
                  <span>{selectedEvent.username ?? "—"}</span>
                </div>
                <div>
                  <span className="text-muted-foreground">Service: </span>
                  <span>{selectedEvent.service ?? "—"}</span>
                </div>
                <div>
                  <span className="text-muted-foreground">Time: </span>
                  <span>{formatDateTime(selectedEvent.eventTime)}</span>
                </div>
              </div>
              <div>
                <div className="mb-1 text-xs font-medium text-muted-foreground">
                  Message
                </div>
                <div className="rounded-lg border bg-muted/20 p-3 text-xs">
                  {selectedEvent.message || "No message payload."}
                </div>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
