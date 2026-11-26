import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link } from "react-router-dom";
import { toast } from "sonner";
import { z } from "zod";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button, buttonVariants } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { api, getApiErrorMessage } from "@/lib/api";

const schema = z.object({
  email: z.string().trim().email("Valid email address is required"),
});

type FormValues = z.infer<typeof schema>;

export function ForgotPasswordPage() {
  const [submitted, setSubmitted] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  const onSubmit = async (values: FormValues) => {
    setSubmitting(true);
    try {
      await api.post("/api/v1/auth/forgot-password", values);
      setSubmitted(true);
      toast.success("Reset instructions sent if account exists");
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Unable to process request"));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-2xl">Forgot password</CardTitle>
        <CardDescription>
          Enter your account email to receive a password reset link.
        </CardDescription>
      </CardHeader>

      {submitted ? (
        <>
          <CardContent>
            <Alert>
              <AlertDescription>
                If an account exists for that email, a reset link has been sent
                (valid for 30 minutes).
              </AlertDescription>
            </Alert>
          </CardContent>
          <CardFooter>
            <Link
              to="/login"
              className={buttonVariants({
                variant: "outline",
                className: "w-full",
              })}
            >
              Back to Sign In
            </Link>
          </CardFooter>
        </>
      ) : (
        <form onSubmit={handleSubmit(onSubmit)}>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="analyst@cyberpulse.io"
                {...register("email")}
              />
              {errors.email && (
                <p className="text-xs text-destructive">
                  {errors.email.message}
                </p>
              )}
            </div>
          </CardContent>
          <CardFooter className="flex flex-col gap-3">
            <Button type="submit" className="w-full" disabled={submitting}>
              {submitting ? "Sending..." : "Send Reset Link"}
            </Button>
            <Link
              to="/login"
              className="text-xs text-muted-foreground hover:underline"
            >
              Back to Sign In
            </Link>
          </CardFooter>
        </form>
      )}
    </Card>
  );
}
