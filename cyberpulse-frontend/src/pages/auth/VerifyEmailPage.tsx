import { MailCheck } from "lucide-react";
import { Link } from "react-router-dom";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { buttonVariants } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

export function VerifyEmailPage() {
  return (
    <Card className="border-border/70 bg-card/90 shadow-xl backdrop-blur">
      <CardHeader>
        <div className="mb-2 flex h-11 w-11 items-center justify-center rounded-lg border border-primary/40 bg-primary/15 text-primary">
          <MailCheck className="h-5 w-5" />
        </div>
        <CardTitle className="text-2xl">Email Verification</CardTitle>
        <CardDescription>
          Account activation status for CyberPulse SOC.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Alert>
          <AlertTitle>Instant workspace provisioning</AlertTitle>
          <AlertDescription>
            Accounts registered in this environment are provisioned immediately
            with VIEWER permissions. Contact your SOC Administrator for role
            elevation or SMTP verification policies.
          </AlertDescription>
        </Alert>
      </CardContent>
      <CardFooter>
        <Link to="/login" className={buttonVariants({ className: "w-full" })}>
          Return to Sign In
        </Link>
      </CardFooter>
    </Card>
  );
}
