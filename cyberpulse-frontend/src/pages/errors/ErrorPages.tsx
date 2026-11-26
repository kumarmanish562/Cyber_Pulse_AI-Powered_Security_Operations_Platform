import { AlertTriangle, Lock, ServerCrash } from "lucide-react";
import { Link } from "react-router-dom";
import { buttonVariants } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

export function UnauthorizedPage() {
  return (
    <StatusErrorShell
      code="403"
      title="Access Denied"
      description="You do not have the required RBAC permissions to view this resource."
      icon={<Lock className="h-6 w-6 text-amber-400" />}
    />
  );
}

export function NotFoundPage() {
  return (
    <StatusErrorShell
      code="404"
      title="Page Not Found"
      description="The requested CyberPulse route does not exist or has moved."
      icon={<AlertTriangle className="h-6 w-6 text-cyan-400" />}
    />
  );
}

export function ServerErrorPage() {
  return (
    <StatusErrorShell
      code="500"
      title="Internal Server Error"
      description="An unexpected application error occurred. Please check your correlation ID and retry."
      icon={<ServerCrash className="h-6 w-6 text-red-400" />}
    />
  );
}

function StatusErrorShell({
  code,
  title,
  description,
  icon,
}: {
  code: string;
  title: string;
  description: string;
  icon: React.ReactNode;
}) {
  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4 text-foreground">
      <Card className="w-full max-w-md border-border/70 bg-card/90 text-center shadow-xl">
        <CardHeader className="items-center">
          <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-full border border-border bg-muted/40">
            {icon}
          </div>
          <span className="font-mono text-xs font-semibold text-muted-foreground">
            HTTP {code}
          </span>
          <CardTitle className="text-2xl">{title}</CardTitle>
          <CardDescription>{description}</CardDescription>
        </CardHeader>
        <CardContent />
        <CardFooter className="flex justify-center gap-3">
          <Link to="/dashboard" className={buttonVariants({ size: "sm" })}>
            Go to Dashboard
          </Link>
          <Link
            to="/"
            className={buttonVariants({ variant: "outline", size: "sm" })}
          >
            Home
          </Link>
        </CardFooter>
      </Card>
    </div>
  );
}
