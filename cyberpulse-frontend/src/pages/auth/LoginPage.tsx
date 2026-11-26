import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { z } from "zod";
import { PasswordInput } from "@/components/soc/PasswordInput";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useAuth } from "@/context/AuthContext";
import { getApiErrorMessage } from "@/lib/api";

const loginSchema = z.object({
  usernameOrEmail: z.string().trim().min(1, "Username or email is required"),
  password: z.string().min(1, "Password is required"),
  totpCode: z.string().optional(),
});

type LoginFormValues = z.infer<typeof loginSchema>;

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [submitting, setSubmitting] = useState(false);
  const [rememberMe, setRememberMe] = useState(true);
  const [mfaRequired, setMfaRequired] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);

  const from =
    (location.state as { from?: { pathname?: string } } | null)?.from
      ?.pathname || "/dashboard";

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (values: LoginFormValues) => {
    setSubmitting(true);
    setServerError(null);
    try {
      const result = await login(values);
      if (result.mfaRequired) {
        setMfaRequired(true);
        toast.info("Enter your 6-digit authenticator code");
        return;
      }
      toast.success("Signed in to CyberPulse");
      navigate(from, { replace: true });
    } catch (error) {
      const message = getApiErrorMessage(error, "Invalid credentials");
      setServerError(message);
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="rounded-2xl border border-border/80 bg-card p-6 shadow-sm sm:p-8">
      <div className="space-y-1.5">
        <h1 className="text-2xl font-semibold tracking-[-0.025em]">
          {mfaRequired
            ? "Two-Factor Verification"
            : "Welcome Back to CyberPulse"}
        </h1>
        <p className="text-sm text-muted-foreground">
          {mfaRequired
            ? "Enter the 6-digit code from your authenticator app."
            : "Sign in to your security operations workspace."}
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-4">
        {serverError && (
          <Alert variant="destructive">
            <AlertDescription>{serverError}</AlertDescription>
          </Alert>
        )}

        {!mfaRequired ? (
          <>
            <div className="space-y-1.5">
              <Label htmlFor="usernameOrEmail" className="text-[13px]">
                Username or Email
              </Label>
              <Input
                id="usernameOrEmail"
                autoComplete="username"
                placeholder="analyst@cyberpulse.io"
                className="h-10 rounded-xl"
                {...register("usernameOrEmail")}
              />
              {errors.usernameOrEmail && (
                <p className="text-xs text-destructive">
                  {errors.usernameOrEmail.message}
                </p>
              )}
            </div>

            <div className="space-y-1.5">
              <div className="flex items-center justify-between">
                <Label htmlFor="password" className="text-[13px]">
                  Password
                </Label>
                <Link
                  to="/forgot-password"
                  className="text-xs text-muted-foreground transition-colors hover:text-foreground"
                >
                  Forgot password?
                </Link>
              </div>
              <PasswordInput
                id="password"
                autoComplete="current-password"
                className="h-10 rounded-xl"
                {...register("password")}
              />
              {errors.password && (
                <p className="text-xs text-destructive">
                  {errors.password.message}
                </p>
              )}
            </div>

            <label className="flex cursor-pointer items-center gap-2.5 pt-1 text-xs text-muted-foreground select-none">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
                className="h-4 w-4 rounded border-border accent-foreground"
              />
              <span>Remember me on this workstation</span>
            </label>
          </>
        ) : (
          <div className="space-y-1.5">
            <Label htmlFor="totpCode" className="text-[13px]">
              6-Digit Authenticator Code
            </Label>
            <Input
              id="totpCode"
              inputMode="numeric"
              maxLength={6}
              placeholder="123456"
              autoFocus
              className="h-10 rounded-xl font-mono tracking-widest"
              {...register("totpCode")}
            />
          </div>
        )}

        <div className="pt-2">
          <Button
            type="submit"
            className="h-10 w-full rounded-full font-medium"
            disabled={submitting}
          >
            {submitting
              ? "Verifying..."
              : mfaRequired
                ? "Verify & Sign In"
                : "Sign In"}
          </Button>
        </div>

        <div className="border-t border-border/60 pt-5 text-center">
          {mfaRequired ? (
            <button
              type="button"
              onClick={() => {
                setMfaRequired(false);
                setServerError(null);
              }}
              className="text-xs text-muted-foreground transition-colors hover:text-foreground"
            >
              Back to credentials
            </button>
          ) : (
            <p className="text-sm text-muted-foreground">
              Don&apos;t have an account?{" "}
              <Link
                to="/signup"
                className="font-medium text-foreground hover:underline"
              >
                Create account
              </Link>
            </p>
          )}
        </div>
      </form>
    </div>
  );
}
