import { Outlet } from "react-router-dom";
import { ThemeToggle } from "@/components/theme/ThemeToggle";

export function PublicLayout() {
  return (
    <div className="relative min-h-screen bg-background text-foreground">
      <div className="fixed top-4 right-4 z-50">
        <ThemeToggle />
      </div>
      <Outlet />
    </div>
  );
}
