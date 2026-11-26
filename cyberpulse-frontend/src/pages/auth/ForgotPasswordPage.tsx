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

export function ForgotPasswordPage() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-2xl">Forgot password</CardTitle>
        <CardDescription>
          Recover access to your CyberPulse SOC account.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Alert>
          <AlertTitle>Administrator-assisted reset</AlertTitle>
          <AlertDescription>
            Self-service email resets require SMTP configuration. Please contact
            your SOC Administrator to reset your credentials.
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
