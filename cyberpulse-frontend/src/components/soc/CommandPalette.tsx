import { useEffect, useState } from "react";
import { Command } from "lucide-react";
import { useNavigate } from "react-router-dom";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { useTheme } from "@/context/ThemeContext";

const COMMANDS = [
  { label: "Go to Overview Dashboard", to: "/dashboard", group: "Navigate" },
  { label: "Go to Security Events", to: "/events", group: "Navigate" },
  { label: "Go to Threat Detections", to: "/detections", group: "Navigate" },
  { label: "Go to Detection Rules", to: "/rules", group: "Navigate" },
  { label: "Go to Risk Assessments", to: "/risk-assessments", group: "Navigate" },
  { label: "Go to Incidents", to: "/incidents", group: "Navigate" },
  { label: "Go to IP Blocklist", to: "/blocklist", group: "Navigate" },
  { label: "Go to Threat Intel Lookup", to: "/threat-intel", group: "Navigate" },
  { label: "Go to Attack Simulator", to: "/simulator", group: "Navigate" },
  { label: "Go to Shift Handover Report", to: "/reports", group: "Navigate" },
  { label: "Go to Audit Logs", to: "/audit-logs", group: "Navigate" },
  { label: "Go to Users & RBAC", to: "/users", group: "Navigate" },
  { label: "Go to Service API Keys", to: "/api-keys", group: "Navigate" },
  { label: "Go to Account Security (2FA)", to: "/security", group: "Navigate" },
];

export function CommandPalette() {
  const [open, setOpen] = useState(false);
  const [filter, setFilter] = useState("");
  const navigate = useNavigate();
  const { toggleTheme } = useTheme();

  useEffect(() => {
    const onKeyDown = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === "k") {
        e.preventDefault();
        setOpen((prev) => !prev);
      }
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, []);

  const filtered = COMMANDS.filter((c) =>
    c.label.toLowerCase().includes(filter.trim().toLowerCase()),
  );

  return (
    <>
      <button
        type="button"
        onClick={() => setOpen(true)}
        className="hidden items-center gap-1.5 rounded-md border bg-muted/40 px-2.5 py-1.5 text-xs text-muted-foreground hover:bg-muted md:inline-flex"
      >
        <Command className="h-3.5 w-3.5" />
        <span>Command</span>
        <kbd className="rounded border bg-background px-1 text-[10px]">
          Ctrl+K
        </kbd>
      </button>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle className="text-sm">SOC Command Palette</DialogTitle>
          </DialogHeader>
          <div className="space-y-3">
            <Input
              autoFocus
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
              placeholder="Type a command or page..."
            />
            <div className="max-h-64 space-y-1 overflow-y-auto text-sm">
              {filtered.map((cmd) => (
                <button
                  key={cmd.to}
                  type="button"
                  onClick={() => {
                    setOpen(false);
                    setFilter("");
                    navigate(cmd.to);
                  }}
                  className="flex w-full items-center justify-between rounded px-2.5 py-1.5 text-left text-xs hover:bg-muted"
                >
                  <span>{cmd.label}</span>
                  <span className="text-[10px] text-muted-foreground">
                    {cmd.group}
                  </span>
                </button>
              ))}
              <button
                type="button"
                onClick={() => {
                  toggleTheme();
                  setOpen(false);
                }}
                className="flex w-full items-center justify-between rounded px-2.5 py-1.5 text-left text-xs hover:bg-muted"
              >
                <span>Toggle Dark / Light Theme</span>
                <span className="text-[10px] text-muted-foreground">
                  Action
                </span>
              </button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}
