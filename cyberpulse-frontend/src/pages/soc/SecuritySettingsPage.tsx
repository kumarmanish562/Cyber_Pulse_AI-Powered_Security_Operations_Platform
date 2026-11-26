import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { ShieldCheck } from "lucide-react";
import { toast } from "sonner";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useAuth } from "@/context/AuthContext";
import { api, getApiErrorMessage } from "@/lib/api";
import type { MfaSetupResponse } from "@/types/api";

export function SecuritySettingsPage() {
  const { user, refreshUser } = useAuth();
  const [setupData, setSetupData] = useState<MfaSetupResponse | null>(null);
  const [code, setCode] = useState("");

  const setupMutation = useMutation({
    mutationFn: async () =>
      (await api.post<MfaSetupResponse>("/api/v1/auth/mfa/setup")).data,
    onSuccess: (data) => {
      setSetupData(data);
      toast.info("Scan or enter the secret in your authenticator app");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const verifyMutation = useMutation({
    mutationFn: () => api.post("/api/v1/auth/mfa/verify", { code }),
    onSuccess: async () => {
      await refreshUser();
      setSetupData(null);
      setCode("");
      toast.success("Two-factor authentication enabled");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const disableMutation = useMutation({
    mutationFn: () => api.post("/api/v1/auth/mfa/disable", { code }),
    onSuccess: async () => {
      await refreshUser();
      setCode("");
      toast.success("Two-factor authentication disabled");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const mfaEnabled = Boolean(user?.mfaEnabled);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">
          Account Security &amp; 2FA
        </h1>
        <p className="text-sm text-muted-foreground">
          Protect your SOC account with RFC 6238 TOTP two-factor authentication.
        </p>
      </div>

      <Card className="max-w-xl">
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle className="flex items-center gap-2 text-lg">
              <ShieldCheck className="h-5 w-5 text-primary" />
              <span>TOTP Two-Factor Authentication</span>
            </CardTitle>
            <Badge variant={mfaEnabled ? "default" : "secondary"}>
              {mfaEnabled ? "Enabled" : "Disabled"}
            </Badge>
          </div>
          <CardDescription>
            Recommended for all ADMIN and SOC_ANALYST accounts.
          </CardDescription>
        </CardHeader>

        <CardContent className="space-y-4">
          {!mfaEnabled && !setupData && (
            <Button
              onClick={() => setupMutation.mutate()}
              disabled={setupMutation.isPending}
            >
              {setupMutation.isPending ? "Generating..." : "Enable 2FA"}
            </Button>
          )}

          {!mfaEnabled && setupData && (
            <div className="space-y-4">
              <Alert>
                <AlertTitle>Configure Authenticator App</AlertTitle>
                <AlertDescription className="mt-2 space-y-2 text-xs">
                  <p>
                    Add this secret key to Google Authenticator, Authy, or
                    1Password:
                  </p>
                  <code className="block rounded bg-muted p-2 font-mono text-xs break-all">
                    {setupData.secret}
                  </code>
                  <p className="text-muted-foreground break-all">
                    URI: {setupData.otpauthUri}
                  </p>
                </AlertDescription>
              </Alert>

              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  verifyMutation.mutate();
                }}
                className="flex items-end gap-2"
              >
                <div className="flex-1 space-y-1">
                  <Label>6-Digit Verification Code</Label>
                  <Input
                    required
                    maxLength={6}
                    inputMode="numeric"
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                    placeholder="123456"
                  />
                </div>
                <Button type="submit" disabled={verifyMutation.isPending}>
                  Verify &amp; Activate
                </Button>
              </form>
            </div>
          )}

          {mfaEnabled && (
            <form
              onSubmit={(e) => {
                e.preventDefault();
                disableMutation.mutate();
              }}
              className="flex items-end gap-2"
            >
              <div className="flex-1 space-y-1">
                <Label>Enter 6-Digit Code to Disable 2FA</Label>
                <Input
                  required
                  maxLength={6}
                  inputMode="numeric"
                  value={code}
                  onChange={(e) => setCode(e.target.value)}
                  placeholder="123456"
                />
              </div>
              <Button
                type="submit"
                variant="destructive"
                disabled={disableMutation.isPending}
              >
                Disable 2FA
              </Button>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
