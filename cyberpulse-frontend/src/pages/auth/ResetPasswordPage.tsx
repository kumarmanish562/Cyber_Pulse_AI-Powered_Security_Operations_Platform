import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { toast } from "sonner";
import { z } from "zod";
import {
  PasswordInput,
  PasswordStrengthBar,
} from "@/components/soc/PasswordInput";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { api, getApiErrorMessage } from "@/lib/api";

const schema = z
  .object({
    token: z.string().trim().min(1, "Reset token is required"),
    newPassword: z
      .string()
      .min(8, "Password must be at least 8 characters")
      .max(100),
    confirmPassword: z.string().min(1, "Please confirm your password"),
  })
  .refine((d) => d.newPassword === d.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
  });

type FormValues = z.infer<typeof schema>;

export function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);

  const {
    register,
    watch,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { token: searchParams.get("token") ?? "" },
  });

  const passwordValue = watch("newPassword") ?? "";

  const onSubmit = async (values: FormValues) => {
    setSubmitting(true);
    setServerError(null);
    try {
      await api.post("/api/v1/auth/reset-password", {
        token: values.token,
        newPassword: values.newPassword,
      });
      toast.success("Password reset! Please sign in.");
      navigate("/login");
    } catch (error) {
      const msg = getApiErrorMessage(error, "Reset failed");
      setServerError(msg);
      toast.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="rounded-2xl border border-border/80 bg-card p-6 shadow-sm sm:p-8">
      <div className="space-y-1.5">
        <h1 className="text-2xl font-semibold tracking-[-0.025em]">
          Reset Password
        </h1>
        <p className="text-sm text-muted-foreground">
          Enter your recovery token and choose a strong new password.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-4">
        {serverError && (
          <Alert variant="destructive">
            <AlertDescription>{serverError}</AlertDescription>
          </Alert>
        )}

        <div className="space-y-1.5">
          <Label htmlFor="token" className="text-[13px]">
            Reset Token
          </Label>
          <Input
            id="token"
            className="h-10 rounded-xl font-mono text-xs"
            {...register("token")}
          />
          {errors.token && (
            <p className="text-xs text-destructive">{errors.token.message}</p>
          )}
        </div>

        <div className="space-y-1.5">
          <Label htmlFor="newPassword" className="text-[13px]">
            New Password
          </Label>
          <PasswordInput
            id="newPassword"
            className="h-10 rounded-xl"
            {...register("newPassword")}
          />
          <PasswordStrengthBar password={passwordValue} />
          {errors.newPassword && (
            <p className="text-xs text-destructive">
              {errors.newPassword.message}
            </p>
          )}
        </div>

        <div className="space-y-1.5">
          <Label htmlFor="confirmPassword" className="text-[13px]">
            Confirm New Password
          </Label>
          <PasswordInput
            id="confirmPassword"
            className="h-10 rounded-xl"
            {...register("confirmPassword")}
          />
          {errors.confirmPassword && (
            <p className="text-xs text-destructive">
              {errors.confirmPassword.message}
            </p>
          )}
        </div>

        <div className="pt-2">
          <Button
            type="submit"
            className="h-10 w-full rounded-full font-medium"
            disabled={submitting}
          >
            {submitting ? "Resetting..." : "Reset Password"}
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
    </div>
  );
}
