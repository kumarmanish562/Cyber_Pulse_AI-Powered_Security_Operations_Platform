import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Copy, KeyRound, Plus } from "lucide-react";
import { toast } from "sonner";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
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
import { api, getApiErrorMessage } from "@/lib/api";
import type { CreateApiKeyResponse, ServiceApiKey } from "@/types/api";
import { formatDateTime } from "@/utils/format";

export function ApiKeysPage() {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [name, setName] = useState("");
  const [scopes, setScopes] = useState("EVENT_WRITE");
  const [createdRawKey, setCreatedRawKey] = useState<string | null>(null);

  const { data: keys, isLoading } = useQuery({
    queryKey: ["api-keys"],
    queryFn: async () =>
      (await api.get<ServiceApiKey[]>("/api/v1/api-keys")).data,
  });

  const createMutation = useMutation({
    mutationFn: () =>
      api.post<CreateApiKeyResponse>("/api/v1/api-keys", { name, scopes }),
    onSuccess: (res) => {
      queryClient.invalidateQueries({ queryKey: ["api-keys"] });
      setCreatedRawKey(res.data.rawKey);
      setName("");
      setOpen(false);
      toast.success("Service API key created");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const revokeMutation = useMutation({
    mutationFn: (id: string) => api.delete(`/api/v1/api-keys/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["api-keys"] });
      toast.success("API key revoked");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const copyKey = async (value: string) => {
    await navigator.clipboard.writeText(value);
    toast.success("Copied API key to clipboard");
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Service API Keys"
        description="Issue scoped X-API-Key tokens for FluentBit, Filebeat, and SIEM log shippers."
        breadcrumbs={[
          { label: "Overview", to: "/dashboard" },
          { label: "Service API Keys" },
        ]}
        actions={
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger render={<Button />}>
              <Plus className="mr-2 h-4 w-4" />
              Create API Key
            </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Create Service API Key</DialogTitle>
            </DialogHeader>
            <form
              onSubmit={(e) => {
                e.preventDefault();
                createMutation.mutate();
              }}
              className="space-y-4"
            >
              <div className="space-y-1">
                <Label>Key Name</Label>
                <Input
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="fluentbit-prod-edge"
                />
              </div>
              <div className="space-y-1">
                <Label>Scopes (comma-separated)</Label>
                <Input
                  required
                  value={scopes}
                  onChange={(e) => setScopes(e.target.value)}
                  placeholder="EVENT_WRITE"
                />
              </div>
              <DialogFooter>
                <Button type="submit" disabled={createMutation.isPending}>
                  {createMutation.isPending ? "Generating..." : "Generate Key"}
                </Button>
              </DialogFooter>
            </form>
          </DialogContent>
        </Dialog>
        }
      />

      {createdRawKey && (
        <Alert>
          <KeyRound className="h-4 w-4" />
          <AlertTitle>Save this API key now — it will not be shown again</AlertTitle>
          <AlertDescription className="mt-2 flex flex-wrap items-center gap-2">
            <code className="rounded bg-muted px-2 py-1 font-mono text-xs">
              {createdRawKey}
            </code>
            <Button
              size="sm"
              variant="outline"
              onClick={() => copyKey(createdRawKey)}
            >
              <Copy className="mr-1 h-3 w-3" />
              Copy
            </Button>
          </AlertDescription>
        </Alert>
      )}

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Issued Keys</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Prefix</TableHead>
                <TableHead>Scopes</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Last Used</TableHead>
                <TableHead className="text-right">Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center">
                    Loading API keys...
                  </TableCell>
                </TableRow>
              ) : keys?.length ? (
                keys.map((k) => (
                  <TableRow key={k.id}>
                    <TableCell className="font-medium">{k.name}</TableCell>
                    <TableCell className="font-mono text-xs">
                      {k.keyPrefix}...
                    </TableCell>
                    <TableCell className="font-mono text-xs">
                      {k.scopes}
                    </TableCell>
                    <TableCell>
                      <Badge variant={k.active ? "default" : "secondary"}>
                        {k.active ? "Active" : "Revoked"}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-xs text-muted-foreground">
                      {formatDateTime(k.lastUsedAt)}
                    </TableCell>
                    <TableCell className="text-right">
                      {k.active && (
                        <Button
                          variant="destructive"
                          size="sm"
                          onClick={() => revokeMutation.mutate(k.id)}
                        >
                          Revoke
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell
                    colSpan={6}
                    className="text-center text-muted-foreground"
                  >
                    No service API keys issued yet.
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
