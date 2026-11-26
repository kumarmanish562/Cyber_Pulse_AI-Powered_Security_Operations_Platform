import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  Activity,
  AlertTriangle,
  Ban,
  Bell,
  FileSearch,
  FileText,
  Flame,
  Gauge,
  KeyRound,
  LayoutDashboard,
  Lock,
  LogOut,
  Menu,
  Radar,
  Search,
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
import { Input } from "@/components/ui/input";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { CommandPalette } from "@/components/soc/CommandPalette";
import { ThemeToggle } from "@/components/theme/ThemeToggle";
import { useAuth } from "@/context/AuthContext";
import { useSocStream } from "@/hooks/useSocStream";
import { api } from "@/lib/api";
import { cn } from "@/lib/utils";
import type {
  GlobalSearchResponse,
  NotificationItem,
  PageResponse,
} from "@/types/api";

const NAV_GROUPS = [
  {
    label: "Core Operations",
    items: [
      { to: "/dashboard", label: "Overview", icon: LayoutDashboard },
      { to: "/events", label: "Security Events", icon: Activity },
      { to: "/detections", label: "Threat Detections", icon: ShieldAlert },
      { to: "/rules", label: "Detection Rules", icon: SlidersHorizontal },
      { to: "/risk-assessments", label: "Risk Assessments", icon: Gauge },
      { to: "/incidents", label: "Incidents", icon: AlertTriangle },
    ],
  },
  {
    label: "Intelligence & Containment",
    items: [
      { to: "/blocklist", label: "IP Blocklist", icon: Ban },
      { to: "/threat-intel", label: "Threat Intel", icon: Radar },
      { to: "/simulator", label: "Attack Simulator", icon: Flame },
      { to: "/reports", label: "Shift Reports", icon: FileText },
    ],
  },
  {
    label: "Governance & Access",
    items: [
      { to: "/audit-logs", label: "Audit Logs", icon: FileSearch },
      { to: "/users", label: "Users & RBAC", icon: Users },
      { to: "/api-keys", label: "Service API Keys", icon: KeyRound },
      { to: "/security", label: "Security (2FA)", icon: Lock },
    ],
  },
];

export function DashboardLayout() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");

  useSocStream(isAuthenticated);

  const { data: searchResults } = useQuery({
    queryKey: ["global-search", searchQuery],
    enabled: searchQuery.trim().length >= 2,
    queryFn: async () =>
      (
        await api.get<GlobalSearchResponse>(
          `/api/v1/search?q=${encodeURIComponent(searchQuery.trim())}`,
        )
      ).data,
  });

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
    <nav className="flex flex-col gap-5 px-3 py-4">
      {NAV_GROUPS.map((group) => (
        <div key={group.label} className="space-y-1">
          <div className="px-3 pb-1 text-[10px] font-semibold tracking-wider text-muted-foreground/80 uppercase">
            {group.label}
          </div>
          {group.items.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={onNavigate}
                className={({ isActive }) =>
                  cn(
                    "flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-all",
                    isActive
                      ? "bg-primary/12 text-primary shadow-[inset_2px_0_0_0_var(--color-primary)]"
                      : "text-muted-foreground hover:bg-muted/70 hover:text-foreground",
                  )
                }
              >
                <Icon className="h-4 w-4 shrink-0" />
                <span className="truncate">{item.label}</span>
              </NavLink>
            );
          })}
        </div>
      ))}
    </nav>
  );

  const initials = (user?.username || "OP").slice(0, 2).toUpperCase();

  return (
    <div className="flex min-h-screen bg-muted/20 text-foreground">
      {/* Desktop Sidebar */}
      <aside className="hidden w-64 shrink-0 border-r border-border/60 bg-background/95 backdrop-blur lg:flex lg:flex-col">
        <div className="flex h-16 items-center gap-2.5 border-b border-border/60 px-5">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-blue-500 to-cyan-400 text-white shadow-sm shadow-blue-500/25">
            <ShieldCheck className="h-4.5 w-4.5" />
          </div>
          <Link to="/dashboard" className="flex flex-col">
            <span className="text-sm font-semibold tracking-tight">
              CyberPulse
            </span>
            <span className="text-[10px] font-medium tracking-wider text-muted-foreground uppercase">
              SOC Platform
            </span>
          </Link>
        </div>
        <div className="flex-1 overflow-y-auto">{navList()}</div>
        <div className="border-t border-border/60 p-4 text-xs text-muted-foreground">
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
            <span className="hidden text-sm font-medium text-muted-foreground md:inline">
              Security Operations Center
            </span>
          </div>

          <div className="relative mx-4 hidden w-full max-w-sm sm:block">
            <Search className="pointer-events-none absolute top-2.5 left-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search incidents, users, IPs..."
              className="h-9 pl-8 text-sm"
            />
            {searchQuery.trim().length >= 2 && searchResults && (
              <div className="absolute top-10 right-0 left-0 z-50 max-h-80 overflow-y-auto rounded-md border bg-background p-2 shadow-lg">
                {searchResults.incidents.length === 0 &&
                searchResults.events.length === 0 &&
                searchResults.blockedIps.length === 0 ? (
                  <p className="p-2 text-center text-xs text-muted-foreground">
                    No matching SOC records
                  </p>
                ) : (
                  <div className="space-y-2 text-xs">
                    {searchResults.incidents.map((i) => (
                      <button
                        key={i.id}
                        type="button"
                        onClick={() => {
                          setSearchQuery("");
                          navigate("/incidents");
                        }}
                        className="flex w-full items-center justify-between rounded px-2 py-1.5 text-left hover:bg-muted"
                      >
                        <span className="truncate font-medium">
                          {i.incidentNumber}: {i.title}
                        </span>
                        <Badge variant="outline">{i.severity}</Badge>
                      </button>
                    ))}
                    {searchResults.events.map((ev) => (
                      <button
                        key={ev.id}
                        type="button"
                        onClick={() => {
                          setSearchQuery("");
                          navigate("/events");
                        }}
                        className="flex w-full items-center justify-between rounded px-2 py-1.5 text-left hover:bg-muted"
                      >
                        <span className="truncate">
                          {ev.eventType} ({ev.username || ev.service || "event"})
                        </span>
                        <Badge variant="secondary">{ev.severity}</Badge>
                      </button>
                    ))}
                    {searchResults.blockedIps.map((b) => (
                      <button
                        key={b.id}
                        type="button"
                        onClick={() => {
                          setSearchQuery("");
                          navigate("/blocklist");
                        }}
                        className="flex w-full items-center justify-between rounded px-2 py-1.5 text-left hover:bg-muted"
                      >
                        <span className="font-mono">{b.ipAddress}</span>
                        <Badge variant="destructive">BLOCKED</Badge>
                      </button>
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>

          <div className="flex items-center gap-3">
            <CommandPalette />
            <Badge
              variant="outline"
              className="hidden items-center gap-1.5 border-emerald-500/40 bg-emerald-500/10 text-emerald-500 sm:inline-flex"
            >
              <span className="h-2 w-2 animate-pulse rounded-full bg-emerald-500" />
              LIVE
            </Badge>
            <ThemeToggle />
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

            <DropdownMenu>
              <DropdownMenuTrigger
                render={
                  <Button
                    variant="outline"
                    size="sm"
                    className="gap-2 pl-1.5"
                  />
                }
              >
                <span className="flex h-6 w-6 items-center justify-center rounded-md bg-primary/15 text-[11px] font-semibold text-primary">
                  {initials}
                </span>
                <span className="hidden max-w-28 truncate text-xs font-medium md:inline">
                  {user?.username || "Analyst"}
                </span>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-56">
                <DropdownMenuLabel className="space-y-1">
                  <div className="text-xs font-semibold">{user?.username}</div>
                  <div className="truncate text-[11px] font-normal text-muted-foreground">
                    {user?.email}
                  </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={() => navigate("/security")}>
                  <Lock className="mr-2 h-3.5 w-3.5" />
                  Security &amp; 2FA
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => navigate("/reports")}>
                  <FileText className="mr-2 h-3.5 w-3.5" />
                  Shift Reports
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  onClick={handleLogout}
                  className="text-red-500 focus:text-red-500"
                >
                  <LogOut className="mr-2 h-3.5 w-3.5" />
                  Sign out
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </header>

        <main className="flex-1 p-4 lg:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
