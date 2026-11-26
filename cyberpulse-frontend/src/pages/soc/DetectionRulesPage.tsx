import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Plus, ShieldCheck, SlidersHorizontal, Trash2 } from "lucide-react";
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
import { PageHeader } from "@/components/soc/PageHeader";
import { SeverityBadge } from "@/components/soc/SeverityBadge";
import { StatCard } from "@/components/soc/StatCard";
import { api, getApiErrorMessage } from "@/lib/api";
import type {
  CreateDetectionRulePayload,
  DetectionRule,
  DetectionRuleType,
  PageResponse,
  Severity,
} from "@/types/api";

const RULE_TYPES: DetectionRuleType[] = [
  "BRUTE_FORCE",
  "SUSPICIOUS_LOGIN",
  "PORT_SCAN",
];
const SEVERITIES: Severity[] = ["LOW", "MEDIUM", "HIGH", "CRITICAL"];

export function DetectionRulesPage() {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState<CreateDetectionRulePayload>({
    name: "",
    ruleType: "BRUTE_FORCE",
    description: "",
    severity: "HIGH",
    thresholdValue: 5,
    timeWindowSeconds: 300,
    enabled: true,
  });

  const { data, isLoading } = useQuery({
    queryKey: ["rules"],
    queryFn: async () =>
      (await api.get<PageResponse<DetectionRule>>("/api/v1/rules?size=50"))
        .data,
  });

  const createMutation = useMutation({
    mutationFn: (payload: CreateDetectionRulePayload) =>
      api.post("/api/v1/rules", payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["rules"] });
      toast.success("Detection rule created");
      setOpen(false);
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const toggleMutation = useMutation({
    mutationFn: ({ id, enabled }: { id: string; enabled: boolean }) =>
      api.patch(`/api/v1/rules/${id}`, { enabled }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["rules"] });
      toast.success("Rule updated");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => api.delete(`/api/v1/rules/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["rules"] });
      toast.success("Rule deleted");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const rules = data?.content ?? [];
  const enabledCount = rules.filter((r) => r.enabled).length;

  return (
    <div className="space-y-6">
      <PageHeader
        title="Detection Rules"
        description="Manage thresholds and time windows for automated threat detection."
        breadcrumbs={[
          { label: "Overview", to: "/dashboard" },
          { label: "Detection Rules" },
        ]}
        actions={
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger render={<Button />}>
              <Plus className="mr-2 h-4 w-4" />
              New Rule
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Create Detection Rule</DialogTitle>
              </DialogHeader>
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  createMutation.mutate(form);
                }}
                className="space-y-4"
              >
                <div className="space-y-1">
                  <Label>Rule Name</Label>
                  <Input
                    required
                    value={form.name}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, name: e.target.value }))
                    }
                    placeholder="SSH Brute Force Threshold"
                  />
                </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1">
                  <Label>Rule Type</Label>
                  <select
                    className="flex h-9 w-full rounded-md border bg-background px-3 text-sm"
                    value={form.ruleType}
                    onChange={(e) =>
                      setForm((f) => ({
                        ...f,
                        ruleType: e.target.value as DetectionRuleType,
                      }))
                    }
                  >
                    {RULE_TYPES.map((t) => (
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
                  <Label>Threshold</Label>
                  <Input
                    type="number"
                    min={1}
                    value={form.thresholdValue}
                    onChange={(e) =>
                      setForm((f) => ({
                        ...f,
                        thresholdValue: Number(e.target.value),
                      }))
                    }
                  />
                </div>
                <div className="space-y-1">
                  <Label>Window (seconds)</Label>
                  <Input
                    type="number"
                    min={1}
                    value={form.timeWindowSeconds}
                    onChange={(e) =>
                      setForm((f) => ({
                        ...f,
                        timeWindowSeconds: Number(e.target.value),
                      }))
                    }
                  />
                </div>
              </div>

              <div className="space-y-1">
                <Label>Description</Label>
                <Input
                  value={form.description ?? ""}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, description: e.target.value }))
                  }
                />
              </div>

              <DialogFooter>
                <Button type="submit" disabled={createMutation.isPending}>
                  {createMutation.isPending ? "Creating..." : "Create Rule"}
                </Button>
              </DialogFooter>
            </form>
          </DialogContent>
        </Dialog>
        }
      />

      <div className="grid gap-4 sm:grid-cols-2">
        <StatCard
          title="Configured Rules"
          value={data?.totalElements ?? rules.length}
          subtitle="Total detection evaluators"
          icon={<SlidersHorizontal className="h-4 w-4 text-blue-500" />}
          loading={isLoading}
          accentClassName="border-t-blue-500/70"
        />
        <StatCard
          title="Active Rules"
          value={enabledCount}
          subtitle="Currently evaluating telemetry"
          icon={<ShieldCheck className="h-4 w-4 text-emerald-500" />}
          loading={isLoading}
          accentClassName="border-t-emerald-500/70"
        />
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Configured Rules</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>Severity</TableHead>
                <TableHead>Threshold</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center">
                    Loading rules...
                  </TableCell>
                </TableRow>
              ) : data?.content.length ? (
                data.content.map((rule) => (
                  <TableRow key={rule.id}>
                    <TableCell className="font-medium">{rule.name}</TableCell>
                    <TableCell className="font-mono text-xs">
                      {rule.ruleType}
                    </TableCell>
                    <TableCell>
                      <SeverityBadge severity={rule.severity} />
                    </TableCell>
                    <TableCell className="text-xs">
                      {rule.thresholdValue} / {rule.timeWindowSeconds}s
                    </TableCell>
                    <TableCell>
                      <Badge variant={rule.enabled ? "default" : "secondary"}>
                        {rule.enabled ? "Enabled" : "Disabled"}
                      </Badge>
                    </TableCell>
                    <TableCell className="space-x-2 text-right">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() =>
                          toggleMutation.mutate({
                            id: rule.id,
                            enabled: !rule.enabled,
                          })
                        }
                      >
                        {rule.enabled ? "Disable" : "Enable"}
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => deleteMutation.mutate(rule.id)}
                      >
                        <Trash2 className="h-4 w-4 text-destructive" />
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
                    No detection rules configured.
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
