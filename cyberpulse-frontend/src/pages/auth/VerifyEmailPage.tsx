import { MailCheck } from "lucide-react";
import { Link } from "react-router-dom";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { buttonVariants } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export function VerifyEmailPage() {
  return (
    <div className="rounded-2xl border border-border/80 bg-card p-6 shadow-sm sm:p-8">
      <div className="space-y-1.5">
        <div className="mb-3 flex h-10 w-10 items-center justify-center rounded-xl border border-border bg-muted/40 text-foreground">
          <MailCheck className="h-5 w-5" />
        </div>
        <h1 className="text-2xl font-semibold tracking-[-0.025em]">
          Email Verification
        </h1>
        <p className="text-sm text-muted-foreground">
          Account activation status for CyberPulse SOC.
        </p>
      </div>

      <div className="mt-6 space-y-5">
        <Alert className="rounded-xl">
          <AlertTitle>Instant workspace provisioning</AlertTitle>
          <AlertDescription>
            Accounts registered in this environment are provisioned immediately
            with VIEWER permissions. Contact your SOC Administrator for role
            elevation or SMTP verification policies.
          </AlertDescription>
        </Alert>

        <Link
          to="/login"
          className={cn(
            buttonVariants(),
            "h-10 w-full rounded-full font-medium",
          )}
        >
          Return to Sign In
        </Link>
      </div>
    </div>
  );
}
