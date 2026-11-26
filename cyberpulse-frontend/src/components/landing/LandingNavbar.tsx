import { useState } from "react";
import { Menu, ShieldCheck, X } from "lucide-react";
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
    <header className="sticky top-0 z-50 border-b border-border/60 bg-background/85 backdrop-blur-xl">
      <div className="mx-auto flex h-14 w-full max-w-6xl items-center justify-between px-4 sm:px-6 lg:px-8">
        <Link to="/" className="flex items-center gap-2.5">
          <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-foreground text-background">
            <ShieldCheck className="h-4 w-4" />
          </div>
          <span className="text-[15px] font-semibold tracking-tight">
            CyberPulse
          </span>
        </Link>

        <nav className="hidden items-center gap-7 md:flex">
          {NAV_LINKS.map((item) => (
            <a
              key={item.href}
              href={item.href}
              className="text-[13px] font-medium text-muted-foreground transition-colors hover:text-foreground"
            >
              {item.label}
            </a>
          ))}
        </nav>

        <div className="hidden items-center gap-2 md:flex">
          <ThemeToggle />
          <Link
            to="/login"
            className={cn(
              buttonVariants({ variant: "ghost", size: "sm" }),
              "rounded-full px-4 text-[13px]",
            )}
          >
            Sign In
          </Link>
          <Link
            to="/signup"
            className={cn(
              buttonVariants({ size: "sm" }),
              "rounded-full bg-foreground px-4 text-[13px] text-background hover:bg-foreground/90",
            )}
          >
            Get Started
          </Link>
        </div>

        <div className="flex items-center gap-2 md:hidden">
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

      {mobileOpen && (
        <div className="border-t border-border/60 bg-background/95 px-4 py-4 backdrop-blur-xl md:hidden">
          <nav className="flex flex-col gap-3">
            {NAV_LINKS.map((item) => (
              <a
                key={item.href}
                href={item.href}
                onClick={() => setMobileOpen(false)}
                className="text-sm font-medium text-muted-foreground hover:text-foreground"
              >
                {item.label}
              </a>
            ))}
            <div className="mt-2 flex gap-2 border-t border-border/60 pt-3">
              <Link
                to="/login"
                className={cn(
                  buttonVariants({ variant: "outline", size: "sm" }),
                  "flex-1 rounded-full",
                )}
              >
                Sign In
              </Link>
              <Link
                to="/signup"
                className={cn(
                  buttonVariants({ size: "sm" }),
                  "flex-1 rounded-full bg-foreground text-background hover:bg-foreground/90",
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
