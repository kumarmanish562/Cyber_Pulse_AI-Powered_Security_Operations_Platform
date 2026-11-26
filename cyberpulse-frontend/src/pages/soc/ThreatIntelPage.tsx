import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { Search } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { PageHeader } from "@/components/soc/PageHeader";
import { api, getApiErrorMessage } from "@/lib/api";
import type { IpThreatIntelResponse } from "@/types/api";
import { formatDateTime } from "@/utils/format";

const VERDICT_STYLE: Record<IpThreatIntelResponse["riskVerdict"], string> = {
  CLEAN: "border-emerald-500/40 bg-emerald-500/15 text-emerald-500",
  SUSPICIOUS: "border-amber-500/40 bg-amber-500/15 text-amber-500",
  MALICIOUS: "border-red-500/40 bg-red-500/15 text-red-500",
};

export function ThreatIntelPage() {
  const [ip, setIp] = useState("");
  const [result, setResult] = useState<IpThreatIntelResponse | null>(null);

  const lookupMutation = useMutation({
    mutationFn: async (targetIp: string) =>
      (
        await api.get<IpThreatIntelResponse>(
          `/api/v1/threat-intel/ip/${encodeURIComponent(targetIp.trim())}`,
        )
      ).data,
    onSuccess: (data) => setResult(data),
    onError: (err) => toast.error(getApiErrorMessage(err, "Lookup failed")),
  });

  return (
    <div className="space-y-6">
      <PageHeader
        title="Threat Intelligence Lookup"
        description="Inspect IP reputation across internal SOC telemetry, active blocklist, and AbuseIPDB enrichment."
        breadcrumbs={[
          { label: "Overview", to: "/dashboard" },
          { label: "Threat Intel" },
        ]}
      />

      <Card className="max-w-2xl">
        <CardHeader>
          <CardTitle className="text-base">IP Reputation Check</CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          <form
            onSubmit={(e) => {
              e.preventDefault();
              if (!ip.trim()) return;
              lookupMutation.mutate(ip);
            }}
            className="flex gap-2"
          >
            <Input
              value={ip}
              onChange={(e) => setIp(e.target.value)}
              placeholder="Enter IPv4 or IPv6 (e.g., 198.51.100.25)"
            />
            <Button type="submit" disabled={lookupMutation.isPending}>
              <Search className="mr-2 h-4 w-4" />
              {lookupMutation.isPending ? "Checking..." : "Lookup"}
            </Button>
          </form>

          {result && (
            <div className="space-y-4 rounded-md border p-4 text-sm">
              <div className="flex flex-wrap items-center justify-between gap-2">
                <span className="font-mono text-base font-bold">
                  {result.ipAddress}
                </span>
                <div className="flex items-center gap-2">
                  <Badge variant="outline">{result.externalProvider}</Badge>
                  <Badge
                    variant="outline"
                    className={VERDICT_STYLE[result.riskVerdict]}
                  >
                    {result.riskVerdict}
                  </Badge>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3 text-xs sm:grid-cols-4">
                <div className="rounded border p-2">
                  <div className="text-muted-foreground">Total Events</div>
                  <div className="text-lg font-bold">{result.totalEvents}</div>
                </div>
                <div className="rounded border p-2">
                  <div className="text-muted-foreground">Failed Logins</div>
                  <div className="text-lg font-bold">{result.failedLogins}</div>
                </div>
                <div className="rounded border p-2">
                  <div className="text-muted-foreground">Port Scans</div>
                  <div className="text-lg font-bold">{result.portScans}</div>
                </div>
                <div className="rounded border p-2">
                  <div className="text-muted-foreground">Blocklisted</div>
                  <div className="text-lg font-bold">
                    {result.currentlyBlocked ? "YES" : "NO"}
                  </div>
                </div>
              </div>

              {result.blockReason && (
                <p className="text-xs text-muted-foreground">
                  <span className="font-semibold text-foreground">
                    Block Reason:{" "}
                  </span>
                  {result.blockReason}
                </p>
              )}

              {result.externalAbuseConfidenceScore !== null && (
                <p className="text-xs text-muted-foreground">
                  External Abuse Confidence Score:{" "}
                  <span className="font-semibold text-foreground">
                    {result.externalAbuseConfidenceScore}%
                  </span>
                </p>
              )}

              <div className="text-[11px] text-muted-foreground">
                Checked at {formatDateTime(result.checkedAt)}
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
