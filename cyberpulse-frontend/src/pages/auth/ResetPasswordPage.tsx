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

export function ResetPasswordPage() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-2xl">Reset password</CardTitle>
        <CardDescription>
          Set a new password for your CyberPulse SOC account.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Alert>
          <AlertTitle>Password reset unavailable</AlertTitle>
          <AlertDescription>
            Token-based password reset is disabled until an SMTP provider is
            configured. Contact your SOC Administrator for assistance.
          </AlertDescription>
        </Alert>
      </CardContent>
      <CardFooter>
        <Link
          to="/login"
          className={buttonVariants({ variant: "outline", className: "w-full" })}
        >
          Back to Sign In
        </Link>
      </CardFooter>
    </Card>
  );
}
