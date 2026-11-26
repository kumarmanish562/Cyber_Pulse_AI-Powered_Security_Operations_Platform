import type { ReactNode } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { cn } from "@/lib/utils";

interface StatCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  icon?: ReactNode;
  loading?: boolean;
  accentClassName?: string;
}

export function StatCard({
  title,
  value,
  subtitle,
  icon,
  loading,
  accentClassName,
}: StatCardProps) {
  return (
    <Card
      className={cn(
        "rounded-2xl border-border/80 bg-card/60 shadow-none transition-colors hover:border-foreground/25 hover:bg-card",
        accentClassName,
      )}
    >
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <CardTitle className="text-xs font-medium tracking-wider text-muted-foreground uppercase">
          {title}
        </CardTitle>
        <div className="flex h-7 w-7 items-center justify-center rounded-lg border border-border/70 bg-background text-muted-foreground">
          {icon}
        </div>
      </CardHeader>
      <CardContent>
        {loading ? (
          <Skeleton className="h-8 w-24" />
        ) : (
          <div className="text-2xl font-semibold tracking-tight sm:text-3xl">
            {value}
          </div>
        )}
        {subtitle && (
          <p className="mt-1 text-xs text-muted-foreground">{subtitle}</p>
        )}
      </CardContent>
    </Card>
  );
}
