import { useState } from "react";
import { ArrowRight, Menu, ShieldCheck, X } from "lucide-react";
import { Link } from "react-router-dom";
import { ThemeToggle } from "@/components/theme/ThemeToggle";
import { buttonVariants } from "@/components/ui/button";
import { cn } from "@/lib/utils";

const NAV_LINKS = [
  { href: "#metrics", label: "Metrics" },
  { href: "#features", label: "Platform" },
  { href: "#workflow", label: "Workflow" },
  { href: "#ai-intelligence", label: "AI Engine" },
];

export function LandingNavbar() {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 w-full px-4 pt-3 sm:px-6">
      <div className="mx-auto flex h-13 w-full max-w-5xl items-center justify-between rounded-full border border-border/80 bg-background/80 px-3 shadow-lg shadow-black/5 backdrop-blur-xl sm:px-4 dark:shadow-black/40">
        {/* Brand */}
        <Link to="/" className="flex items-center gap-2 pl-1">
          <div className="flex h-7 w-7 items-center justify-center rounded-full bg-foreground text-background">
            <ShieldCheck className="h-3.5 w-3.5" />
          </div>
          <span className="text-sm font-semibold tracking-tight">
            CyberPulse
          </span>
        </Link>

        {/* Segmented Center Pill Nav */}
        <nav className="hidden items-center gap-0.5 rounded-full border border-border/60 bg-muted/50 p-1 md:flex">
          {NAV_LINKS.map((item) => (
            <a
              key={item.href}
              href={item.href}
              className="rounded-full px-3.5 py-1 text-xs font-medium text-muted-foreground transition-colors hover:bg-background hover:text-foreground"
            >
              {item.label}
            </a>
          ))}
        </nav>

        {/* Actions */}
        <div className="hidden items-center gap-1.5 md:flex">
          <ThemeToggle />
          <Link
            to="/login"
            className={cn(
              buttonVariants({ variant: "ghost", size: "sm" }),
              "h-8 rounded-full px-3.5 text-xs font-medium",
            )}
          >
            Sign In
          </Link>
          <Link
            to="/signup"
            className={cn(
              buttonVariants({ size: "sm" }),
              "h-8 rounded-full bg-foreground px-4 text-xs font-medium text-background hover:bg-foreground/90",
            )}
          >
            Get Started
            <ArrowRight className="ml-1.5 h-3.5 w-3.5" />
          </Link>
        </div>

        {/* Mobile Controls */}
        <div className="flex items-center gap-1.5 md:hidden">
          <ThemeToggle />
          <button
            type="button"
            aria-label="Toggle navigation menu"
            onClick={() => setMobileOpen((prev) => !prev)}
            className={cn(
              buttonVariants({ variant: "outline", size: "icon" }),
              "h-8 w-8 rounded-full",
            )}
          >
            {mobileOpen ? (
              <X className="h-4 w-4" />
            ) : (
              <Menu className="h-4 w-4" />
            )}
          </button>
        </div>
      </div>

      {/* Mobile Floating Drawer */}
      {mobileOpen && (
        <div className="mx-auto mt-2 w-full max-w-5xl rounded-2xl border border-border/80 bg-background/95 p-4 shadow-xl backdrop-blur-xl md:hidden">
          <nav className="flex flex-col gap-1">
            {NAV_LINKS.map((item) => (
              <a
                key={item.href}
                href={item.href}
                onClick={() => setMobileOpen(false)}
                className="rounded-xl px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
              >
                {item.label}
              </a>
            ))}
            <div className="mt-3 flex gap-2 border-t border-border/60 pt-3">
              <Link
                to="/login"
                className={cn(
                  buttonVariants({ variant: "outline", size: "sm" }),
                  "h-9 flex-1 rounded-full",
                )}
              >
                Sign In
              </Link>
              <Link
                to="/signup"
                className={cn(
                  buttonVariants({ size: "sm" }),
                  "h-9 flex-1 rounded-full bg-foreground text-background hover:bg-foreground/90",
                )}
              >
                Get Started
              </Link>
            </div>
          </nav>
        </div>
      )}
    </header>
  );
}
