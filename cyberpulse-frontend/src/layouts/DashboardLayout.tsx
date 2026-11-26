import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  Activity,
  AlertTriangle,
  Bell,
  FileSearch,
  Gauge,
  KeyRound,
  LayoutDashboard,
  Lock,
  LogOut,
  Menu,
  ShieldAlert,
  ShieldCheck,
  SlidersHorizontal,
  Users,
} from "lucide-react";
import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { useAuth } from "@/context/AuthContext";
import { useSocStream } from "@/hooks/useSocStream";
import { api } from "@/lib/api";
import { cn } from "@/lib/utils";
import type { NotificationItem, PageResponse } from "@/types/api";

const NAV_ITEMS = [
  { to: "/dashboard", label: "Overview", icon: LayoutDashboard },
  { to: "/events", label: "Security Events", icon: Activity },
  { to: "/detections", label: "Threat Detections", icon: ShieldAlert },
  { to: "/rules", label: "Detection Rules", icon: SlidersHorizontal },
  { to: "/risk-assessments", label: "Risk Assessments", icon: Gauge },
  { to: "/incidents", label: "Incidents", icon: AlertTriangle },
  { to: "/audit-logs", label: "Audit Logs", icon: FileSearch },
  { to: "/users", label: "Users & RBAC", icon: Users },
  { to: "/api-keys", label: "Service API Keys", icon: KeyRound },
  { to: "/security", label: "Security (2FA)", icon: Lock },
];

export function DashboardLayout() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [mobileOpen, setMobileOpen] = useState(false);

  useSocStream(isAuthenticated);

  const { data: unreadCountData } = useQuery({
    queryKey: ["notifications", "unread-count"],
    queryFn: async () => {
      const res = await api.get<{ count: number }>(
        "/api/v1/notifications/unread/count",
      );
      return res.data;
    },
    refetchInterval: 30_000,
  });

  const { data: unreadNotifications } = useQuery({
    queryKey: ["notifications", "unread"],
    queryFn: async () => {
      const res = await api.get<PageResponse<NotificationItem>>(
        "/api/v1/notifications/unread?size=5",
      );
      return res.data;
    },
    refetchInterval: 30_000,
  });

  const markReadMutation = useMutation({
    mutationFn: (id: string) => api.patch(`/api/v1/notifications/${id}/read`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
  });

  const markAllReadMutation = useMutation({
    mutationFn: () => api.patch("/api/v1/notifications/read-all"),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
      toast.success("All notifications marked as read");
    },
  });

  const handleLogout = async () => {
    await logout();
    toast.success("Signed out");
    navigate("/login");
  };

  const unreadCount = unreadCountData?.count ?? 0;

  const navList = (onNavigate?: () => void) => (
    <nav className="flex flex-col gap-1 px-3 py-4">
      {NAV_ITEMS.map((item) => {
        const Icon = item.icon;
        return (
          <NavLink
            key={item.to}
            to={item.to}
            onClick={onNavigate}
            className={({ isActive }) =>
              cn(
                "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                isActive
                  ? "bg-primary text-primary-foreground"
                  : "text-muted-foreground hover:bg-muted hover:text-foreground",
              )
            }
          >
            <Icon className="h-4 w-4" />
            {item.label}
          </NavLink>
        );
      })}
    </nav>
  );

  return (
    <div className="flex min-h-screen bg-muted/20 text-foreground">
      {/* Desktop Sidebar */}
      <aside className="hidden w-64 shrink-0 border-r bg-background lg:flex lg:flex-col">
        <div className="flex h-16 items-center gap-2 border-b px-6">
          <ShieldCheck className="h-5 w-5 text-primary" />
          <Link to="/dashboard" className="font-semibold tracking-tight">
            CyberPulse SOC
          </Link>
        </div>
        <div className="flex-1 overflow-y-auto">{navList()}</div>
        <div className="border-t p-4 text-xs text-muted-foreground">
          <div className="font-medium text-foreground">{user?.username}</div>
          <div className="truncate">{user?.email}</div>
          <div className="mt-2 flex flex-wrap gap-1">
            {user?.roles.map((role) => (
              <Badge key={role} variant="secondary" className="text-[10px]">
                {role}
              </Badge>
            ))}
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <div className="flex flex-1 flex-col">
        <header className="flex h-16 items-center justify-between border-b bg-background px-4 lg:px-8">
          <div className="flex items-center gap-3">
            <Sheet open={mobileOpen} onOpenChange={setMobileOpen}>
              <SheetTrigger
                render={
                  <Button variant="ghost" size="icon" className="lg:hidden" />
                }
              >
                <Menu className="h-5 w-5" />
              </SheetTrigger>
              <SheetContent side="left" className="w-64 p-0">
                <div className="flex h-16 items-center gap-2 border-b px-6">
                  <ShieldCheck className="h-5 w-5 text-primary" />
                  <span className="font-semibold">CyberPulse SOC</span>
                </div>
                {navList(() => setMobileOpen(false))}
              </SheetContent>
            </Sheet>
            <span className="text-sm font-medium text-muted-foreground">
              Security Operations Center
            </span>
          </div>

          <div className="flex items-center gap-3">
            {/* Notifications Dropdown */}
            <DropdownMenu>
              <DropdownMenuTrigger
                render={
                  <Button variant="outline" size="icon" className="relative" />
                }
              >
                <Bell className="h-4 w-4" />
                {unreadCount > 0 && (
                  <span className="absolute -top-1 -right-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-red-500 px-1 text-[10px] font-bold text-white">
                    {unreadCount > 99 ? "99+" : unreadCount}
                  </span>
                )}
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-80">
                <DropdownMenuLabel className="flex items-center justify-between">
                  <span>Notifications ({unreadCount})</span>
                  {unreadCount > 0 && (
                    <button
                      type="button"
                      onClick={() => markAllReadMutation.mutate()}
                      className="text-xs font-normal text-primary hover:underline"
                    >
                      Mark all read
                    </button>
                  )}
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                {unreadNotifications?.content?.length ? (
                  unreadNotifications.content.map((n) => (
                    <DropdownMenuItem
                      key={n.id}
                      onClick={() => markReadMutation.mutate(n.id)}
                      className="flex flex-col items-start gap-1 py-2"
                    >
                      <span className="text-xs font-semibold">{n.title}</span>
                      <span className="line-clamp-2 text-xs text-muted-foreground">
                        {n.message}
                      </span>
                    </DropdownMenuItem>
                  ))
                ) : (
                  <div className="px-3 py-4 text-center text-xs text-muted-foreground">
                    No unread notifications
                  </div>
                )}
              </DropdownMenuContent>
            </DropdownMenu>

            <Button variant="ghost" size="sm" onClick={handleLogout}>
              <LogOut className="mr-2 h-4 w-4" />
              Sign out
            </Button>
          </div>
        </header>

        <main className="flex-1 p-4 lg:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
