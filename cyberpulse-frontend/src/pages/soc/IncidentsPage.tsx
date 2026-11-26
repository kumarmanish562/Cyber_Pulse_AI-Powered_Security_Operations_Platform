import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Plus, Sparkles } from "lucide-react";
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
import {
  IncidentStatusBadge,
  SeverityBadge,
} from "@/components/soc/SeverityBadge";
import { formatDateTime } from "@/utils/format";
import { api, getApiErrorMessage } from "@/lib/api";
import type {
  AiTriageResponse,
  Incident,
  IncidentNote,
  IncidentSeverity,
  IncidentStatus,
  PageResponse,
} from "@/types/api";

const STATUSES: IncidentStatus[] = [
  "OPEN",
  "INVESTIGATING",
  "RESOLVED",
  "CLOSED",
];
const SEVERITIES: IncidentSeverity[] = ["LOW", "MEDIUM", "HIGH", "CRITICAL"];

export function IncidentsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState("");
  const [createOpen, setCreateOpen] = useState(false);
  const [selectedIncident, setSelectedIncident] = useState<Incident | null>(
    null,
  );
  const [aiTriage, setAiTriage] = useState<AiTriageResponse | null>(null);
  const [noteContent, setNoteContent] = useState("");
  const [newIncident, setNewIncident] = useState({
    title: "",
    description: "",
    severity: "HIGH" as IncidentSeverity,
  });

  const { data, isLoading } = useQuery({
    queryKey: ["incidents", page, statusFilter],
    queryFn: async () => {
      const url = statusFilter
        ? `/api/v1/incidents/status/${statusFilter}?page=${page}&size=15`
        : `/api/v1/incidents?page=${page}&size=15`;
      return (await api.get<PageResponse<Incident>>(url)).data;
    },
  });

  const { data: notes } = useQuery({
    queryKey: ["incident-notes", selectedIncident?.id],
    enabled: Boolean(selectedIncident),
    queryFn: async () =>
      (
        await api.get<IncidentNote[]>(
          `/api/v1/incidents/${selectedIncident!.id}/notes`,
        )
      ).data,
  });

  const createMutation = useMutation({
    mutationFn: () => api.post<Incident>("/api/v1/incidents", newIncident),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["incidents"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      toast.success("Incident created");
      setCreateOpen(false);
      setNewIncident({ title: "", description: "", severity: "HIGH" });
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const statusMutation = useMutation({
    mutationFn: ({ id, status }: { id: string; status: IncidentStatus }) =>
      api.patch<Incident>(`/api/v1/incidents/${id}/status`, { status }),
    onSuccess: (res) => {
      queryClient.invalidateQueries({ queryKey: ["incidents"] });
      setSelectedIncident(res.data);
      toast.success("Incident status updated");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const addNoteMutation = useMutation({
    mutationFn: ({ id, content }: { id: string; content: string }) =>
      api.post(`/api/v1/incidents/${id}/notes`, { content }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["incident-notes", selectedIncident?.id],
      });
      setNoteContent("");
      toast.success("Investigation note added");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const aiTriageMutation = useMutation({
    mutationFn: (id: string) =>
      api.post<AiTriageResponse>(`/api/v1/incidents/${id}/ai-triage`),
    onSuccess: (res) => {
      setAiTriage(res.data);
      toast.success("AI triage analysis complete");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Incidents</h1>
          <p className="text-sm text-muted-foreground">
            Triage, investigate, and resolve security incidents.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <select
            className="h-9 rounded-md border bg-background px-3 text-sm"
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value);
              setPage(0);
            }}
          >
            <option value="">All Statuses</option>
            {STATUSES.map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </select>

          <Dialog open={createOpen} onOpenChange={setCreateOpen}>
            <DialogTrigger render={<Button />}>
              <Plus className="mr-2 h-4 w-4" />
              New Incident
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Create Security Incident</DialogTitle>
              </DialogHeader>
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  createMutation.mutate();
                }}
                className="space-y-4"
              >
                <div className="space-y-1">
                  <Label>Title</Label>
                  <Input
                    required
                    value={newIncident.title}
                    onChange={(e) =>
                      setNewIncident((p) => ({ ...p, title: e.target.value }))
                    }
                    placeholder="Suspicious credential stuffing spike"
                  />
                </div>
                <div className="space-y-1">
                  <Label>Severity</Label>
                  <select
                    className="flex h-9 w-full rounded-md border bg-background px-3 text-sm"
                    value={newIncident.severity}
                    onChange={(e) =>
                      setNewIncident((p) => ({
                        ...p,
                        severity: e.target.value as IncidentSeverity,
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
                <div className="space-y-1">
                  <Label>Description</Label>
                  <Input
                    value={newIncident.description}
                    onChange={(e) =>
                      setNewIncident((p) => ({
                        ...p,
                        description: e.target.value,
                      }))
                    }
                  />
                </div>
                <DialogFooter>
                  <Button type="submit" disabled={createMutation.isPending}>
                    {createMutation.isPending ? "Creating..." : "Create"}
                  </Button>
                </DialogFooter>
              </form>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Incident Queue</CardTitle>
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
                <TableHead className="text-right">Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center">
                    Loading incidents...
                  </TableCell>
                </TableRow>
              ) : data?.content.length ? (
                data.content.map((inc) => (
                  <TableRow key={inc.id}>
                    <TableCell className="font-mono text-xs">
                      {inc.incidentNumber}
                    </TableCell>
                    <TableCell className="font-medium">{inc.title}</TableCell>
                    <TableCell>
                      <SeverityBadge severity={inc.severity} />
                    </TableCell>
                    <TableCell>
                      <IncidentStatusBadge status={inc.status} />
                    </TableCell>
                    <TableCell className="text-xs text-muted-foreground">
                      {formatDateTime(inc.createdAt)}
                    </TableCell>
                    <TableCell className="text-right">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => {
                          setAiTriage(null);
                          setSelectedIncident(inc);
                        }}
                      >
                        Inspect
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
                    No incidents found.
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

      {/* Incident Detail Dialog */}
      <Dialog
        open={Boolean(selectedIncident)}
        onOpenChange={(isOpen) => !isOpen && setSelectedIncident(null)}
      >
        <DialogContent className="max-w-lg">
          {selectedIncident && (
            <>
              <DialogHeader>
                <DialogTitle className="flex items-center gap-2">
                  <span className="font-mono text-sm text-muted-foreground">
                    {selectedIncident.incidentNumber}
                  </span>
                  <span>{selectedIncident.title}</span>
                </DialogTitle>
              </DialogHeader>

              <div className="space-y-4 text-sm">
                <p className="text-muted-foreground">
                  {selectedIncident.description || "No description provided."}
                </p>

                <div className="flex flex-wrap items-center gap-3">
                  <SeverityBadge severity={selectedIncident.severity} />
                  <IncidentStatusBadge status={selectedIncident.status} />
                  <select
                    className="ml-auto h-8 rounded-md border bg-background px-2 text-xs"
                    value={selectedIncident.status}
                    onChange={(e) =>
                      statusMutation.mutate({
                        id: selectedIncident.id,
                        status: e.target.value as IncidentStatus,
                      })
                    }
                  >
                    {STATUSES.map((s) => (
                      <option key={s} value={s}>
                        {s}
                      </option>
                    ))}
                  </select>
                </div>

                {/* AI Triage & Remediation Panel */}
                <div className="space-y-2 rounded-md border bg-muted/30 p-3">
                  <div className="flex items-center justify-between gap-2">
                    <div className="flex items-center gap-2 font-semibold">
                      <Sparkles className="h-4 w-4 text-primary" />
                      <span>AI Triage &amp; Remediation</span>
                    </div>
                    <Button
                      type="button"
                      size="sm"
                      variant="secondary"
                      disabled={aiTriageMutation.isPending}
                      onClick={() =>
                        aiTriageMutation.mutate(selectedIncident.id)
                      }
                    >
                      {aiTriageMutation.isPending
                        ? "Analyzing..."
                        : "Run AI Triage"}
                    </Button>
                  </div>

                  {aiTriage && (
                    <div className="space-y-2 pt-1 text-xs">
                      <div className="flex flex-wrap items-center gap-2">
                        <Badge variant="outline">{aiTriage.provider}</Badge>
                        <span className="font-medium text-muted-foreground">
                          {aiTriage.priorityAssessment}
                        </span>
                      </div>
                      <p>{aiTriage.summary}</p>
                      <p className="text-muted-foreground">
                        <span className="font-semibold text-foreground">
                          Likely Vector:{" "}
                        </span>
                        {aiTriage.likelyAttackVector}
                      </p>
                      <div>
                        <div className="mb-1 font-semibold">
                          Recommended Actions:
                        </div>
                        <ul className="list-disc space-y-1 pl-4 text-muted-foreground">
                          {aiTriage.recommendedActions.map((action, idx) => (
                            <li key={idx}>{action}</li>
                          ))}
                        </ul>
                      </div>
                    </div>
                  )}
                </div>

                <div className="space-y-2 border-t pt-3">
                  <h4 className="font-semibold">Investigation Notes</h4>
                  <div className="max-h-40 space-y-2 overflow-y-auto">
                    {notes?.length ? (
                      notes.map((n) => (
                        <div key={n.id} className="rounded-md border p-2 text-xs">
                          <p>{n.content}</p>
                          <span className="text-[10px] text-muted-foreground">
                            {formatDateTime(n.createdAt)}
                          </span>
                        </div>
                      ))
                    ) : (
                      <p className="text-xs text-muted-foreground">
                        No notes yet.
                      </p>
                    )}
                  </div>

                  <form
                    onSubmit={(e) => {
                      e.preventDefault();
                      if (!noteContent.trim()) return;
                      addNoteMutation.mutate({
                        id: selectedIncident.id,
                        content: noteContent.trim(),
                      });
                    }}
                    className="flex gap-2"
                  >
                    <Input
                      value={noteContent}
                      onChange={(e) => setNoteContent(e.target.value)}
                      placeholder="Add investigation note..."
                    />
                    <Button type="submit" size="sm">
                      Add
                    </Button>
                  </form>
                </div>
              </div>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
