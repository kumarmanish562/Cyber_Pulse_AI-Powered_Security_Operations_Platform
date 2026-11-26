import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Flame, Play } from "lucide-react";
import { Link } from "react-router-dom";
import { toast } from "sonner";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import { Button, buttonVariants } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { api, getApiErrorMessage } from "@/lib/api";
import type { AttackScenario, SimulationResponse } from "@/types/api";
import { formatDateTime } from "@/utils/format";

const SCENARIOS: Array<{
  id: AttackScenario;
  title: string;
  mitre: string;
  severity: string;
  description: string;
}> = [
  {
    id: "SSH_BRUTE_FORCE",
    title: "SSH Brute-Force Burst",
    mitre: "TA0006 · T1110",
    severity: "HIGH",
    description:
      "Injects 12 failed SSH login events from 198.51.100.77 to test BruteForceRuleEvaluator, Risk Scoring, Incident Creation, and Auto IP Block.",
  },
  {
    id: "PORT_SCAN_SWEEP",
    title: "Perimeter Port Scan Sweep",
    mitre: "TA0043 · T1046",
    severity: "HIGH",
    description:
      "Injects 6 TCP SYN port scan events from 203.0.113.45 against edge-firewall to trigger PortScanRuleEvaluator.",
  },
  {
    id: "IMPOSSIBLE_TRAVEL",
    title: "Multi-IP Impossible Travel",
    mitre: "TA0001 · T1078",
    severity: "MEDIUM",
    description:
      "Injects 3 successful logins for soc_executive across distinct global IPs to trigger SuspiciousLoginRuleEvaluator.",
  },
];

export function AttackSimulatorPage() {
  const queryClient = useQueryClient();
  const [lastResult, setLastResult] = useState<SimulationResponse | null>(null);

  const runMutation = useMutation({
    mutationFn: async (scenario: AttackScenario) =>
      (
        await api.post<SimulationResponse>("/api/v1/simulator/run", {
          scenario,
        })
      ).data,
    onSuccess: (data) => {
      setLastResult(data);
      queryClient.invalidateQueries({ queryKey: ["events"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      toast.success(`Simulation ${data.scenario} launched`);
    },
    onError: (err) => toast.error(getApiErrorMessage(err, "Simulation failed")),
  });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="flex items-center gap-2 text-2xl font-bold tracking-tight">
          <Flame className="h-6 w-6 text-orange-500" />
          <span>Attack Simulator &amp; SOC Readiness Lab</span>
        </h1>
        <p className="text-sm text-muted-foreground">
          Safely inject controlled attack telemetry to validate detection rules,
          risk scoring, incidents, IP blocking, and live SSE alerts end-to-end.
        </p>
      </div>

      {lastResult && (
        <Alert>
          <AlertTitle>
            Simulation Executed: {lastResult.scenario} (
            {lastResult.eventsInjected} events)
          </AlertTitle>
          <AlertDescription className="mt-2 space-y-3 text-xs">
            <p>{lastResult.summary}</p>
            <p className="text-muted-foreground">
              Triggered at {formatDateTime(lastResult.triggeredAt)} · Attacker
              IP: <code className="font-mono">{lastResult.attackerIp}</code>
            </p>
            <div className="flex flex-wrap gap-2">
              <Link
                to="/detections"
                className={buttonVariants({ variant: "outline", size: "sm" })}
              >
                View Detections
              </Link>
              <Link
                to="/incidents"
                className={buttonVariants({ variant: "outline", size: "sm" })}
              >
                View Incidents
              </Link>
              <Link
                to="/blocklist"
                className={buttonVariants({ variant: "outline", size: "sm" })}
              >
                View IP Blocklist
              </Link>
            </div>
          </AlertDescription>
        </Alert>
      )}

      <div className="grid gap-4 md:grid-cols-3">
        {SCENARIOS.map((s) => (
          <Card key={s.id} className="flex flex-col justify-between">
            <CardHeader>
              <div className="flex items-center justify-between gap-2">
                <Badge variant="outline" className="font-mono text-[10px]">
                  {s.mitre}
                </Badge>
                <Badge variant="secondary">{s.severity}</Badge>
              </div>
              <CardTitle className="pt-2 text-lg">{s.title}</CardTitle>
              <CardDescription>{s.description}</CardDescription>
            </CardHeader>
            <CardContent />
            <CardFooter>
              <Button
                className="w-full"
                disabled={runMutation.isPending}
                onClick={() => runMutation.mutate(s.id)}
              >
                <Play className="mr-2 h-4 w-4" />
                Launch Simulation
              </Button>
            </CardFooter>
          </Card>
        ))}
      </div>
    </div>
  );
}
