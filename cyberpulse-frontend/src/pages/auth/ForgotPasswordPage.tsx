import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { CheckCircle2 } from "lucide-react";
import { Link } from "react-router-dom";
import { toast } from "sonner";
import { z } from "zod";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Button, buttonVariants } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { api, getApiErrorMessage } from "@/lib/api";
import { cn } from "@/lib/utils";

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
    <div className="rounded-2xl border border-border/80 bg-card p-6 shadow-sm sm:p-8">
      <div className="space-y-1.5">
        <h1 className="text-2xl font-semibold tracking-[-0.025em]">
          Forgot Your Password?
        </h1>
        <p className="text-sm text-muted-foreground">
          Enter your registered email address and we&apos;ll help you regain
          access to your account.
        </p>
      </div>

      {submitted ? (
        <div className="mt-6 space-y-5">
          <Alert className="rounded-xl">
            <CheckCircle2 className="h-4 w-4 text-emerald-500" />
            <AlertTitle>Check your inbox</AlertTitle>
            <AlertDescription>
              If an account exists for that email, a reset link has been sent
              (valid for 30 minutes).
            </AlertDescription>
          </Alert>
          <Link
            to="/login"
            className={cn(
              buttonVariants({ variant: "outline" }),
              "h-10 w-full rounded-full font-medium",
            )}
          >
            Back to Login
          </Link>
        </div>
      ) : (
        <form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-4">
          <div className="space-y-1.5">
            <Label htmlFor="email" className="text-[13px]">
              Registered Email
            </Label>
            <Input
              id="email"
              type="email"
              placeholder="analyst@cyberpulse.io"
              className="h-10 rounded-xl"
              {...register("email")}
            />
            {errors.email && (
              <p className="text-xs text-destructive">{errors.email.message}</p>
            )}
          </div>

          <div className="pt-2">
            <Button
              type="submit"
              className="h-10 w-full rounded-full font-medium"
              disabled={submitting}
            >
              {submitting ? "Sending..." : "Send Reset Link"}
            </Button>
          </div>

          <div className="border-t border-border/60 pt-5 text-center">
            <Link
              to="/login"
              className="text-sm text-muted-foreground transition-colors hover:text-foreground hover:underline"
            >
              Back to Login
            </Link>
          </div>
        </form>
      )}
    </div>
  );
}
