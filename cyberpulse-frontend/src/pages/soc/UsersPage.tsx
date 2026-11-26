import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
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
import { api, getApiErrorMessage } from "@/lib/api";
import type { UserResponse } from "@/types/api";

const AVAILABLE_ROLES = ["ADMIN", "SOC_ANALYST", "VIEWER"];

export function UsersPage() {
  const queryClient = useQueryClient();

  const { data: users, isLoading } = useQuery({
    queryKey: ["users"],
    queryFn: async () => (await api.get<UserResponse[]>("/api/v1/users")).data,
  });

  const assignRoleMutation = useMutation({
    mutationFn: ({ userId, roleName }: { userId: string; roleName: string }) =>
      api.post(`/api/v1/users/${userId}/roles`, { roleName }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      toast.success("Role assigned");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  const removeRoleMutation = useMutation({
    mutationFn: ({ userId, roleName }: { userId: string; roleName: string }) =>
      api.delete(`/api/v1/users/${userId}/roles/${roleName}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      toast.success("Role removed");
    },
    onError: (err) => toast.error(getApiErrorMessage(err)),
  });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Users & RBAC</h1>
        <p className="text-sm text-muted-foreground">
          Manage platform users and role-based access control assignments.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Directory</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Username</TableHead>
                <TableHead>Email</TableHead>
                <TableHead>Assigned Roles</TableHead>
                <TableHead className="text-right">Assign Role</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={4} className="text-center">
                    Loading users...
                  </TableCell>
                </TableRow>
              ) : users?.length ? (
                users.map((u) => (
                  <TableRow key={u.id}>
                    <TableCell className="font-medium">{u.username}</TableCell>
                    <TableCell className="text-muted-foreground">
                      {u.email}
                    </TableCell>
                    <TableCell className="space-x-1">
                      {u.roles.map((role) => (
                        <Badge
                          key={role}
                          variant="secondary"
                          className="cursor-pointer"
                          title="Click to remove role"
                          onClick={() =>
                            removeRoleMutation.mutate({
                              userId: u.id,
                              roleName: role,
                            })
                          }
                        >
                          {role} ×
                        </Badge>
                      ))}
                    </TableCell>
                    <TableCell className="space-x-1 text-right">
                      {AVAILABLE_ROLES.filter((r) => !u.roles.includes(r)).map(
                        (role) => (
                          <Button
                            key={role}
                            variant="outline"
                            size="sm"
                            onClick={() =>
                              assignRoleMutation.mutate({
                                userId: u.id,
                                roleName: role,
                              })
                            }
                          >
                            + {role}
                          </Button>
                        ),
                      )}
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell
                    colSpan={4}
                    className="text-center text-muted-foreground"
                  >
                    No users found.
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
