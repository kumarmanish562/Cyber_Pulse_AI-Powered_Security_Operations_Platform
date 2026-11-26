import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { z } from "zod";
import { PasswordInput } from "@/components/soc/PasswordInput";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
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
    <Card className="border-border/70 bg-card/90 shadow-xl backdrop-blur">
      <CardHeader>
        <CardTitle className="text-2xl">
          {mfaRequired
            ? "Two-Factor Verification"
            : "Welcome Back to CyberPulse"}
        </CardTitle>
        <CardDescription>
          {mfaRequired
            ? "Enter the 6-digit code from your authenticator app."
            : "Sign in to your security operations workspace."}
        </CardDescription>
      </CardHeader>

      <form onSubmit={handleSubmit(onSubmit)}>
        <CardContent className="space-y-4">
          {serverError && (
            <Alert variant="destructive">
              <AlertDescription>{serverError}</AlertDescription>
            </Alert>
          )}

          {!mfaRequired ? (
            <>
              <div className="space-y-2">
                <Label htmlFor="usernameOrEmail">Username or Email</Label>
                <Input
                  id="usernameOrEmail"
                  autoComplete="username"
                  placeholder="analyst@cyberpulse.io"
                  {...register("usernameOrEmail")}
                />
                {errors.usernameOrEmail && (
                  <p className="text-xs text-destructive">
                    {errors.usernameOrEmail.message}
                  </p>
                )}
              </div>

              <div className="space-y-2">
                <div className="flex items-center justify-between">
                  <Label htmlFor="password">Password</Label>
                  <Link
                    to="/forgot-password"
                    className="text-xs text-primary hover:underline"
                  >
                    Forgot password?
                  </Link>
                </div>
                <PasswordInput
                  id="password"
                  autoComplete="current-password"
                  {...register("password")}
                />
                {errors.password && (
                  <p className="text-xs text-destructive">
                    {errors.password.message}
                  </p>
                )}
              </div>

              <label className="flex cursor-pointer items-center gap-2 text-xs text-muted-foreground">
                <input
                  type="checkbox"
                  checked={rememberMe}
                  onChange={(e) => setRememberMe(e.target.checked)}
                  className="h-3.5 w-3.5 rounded border-border accent-primary"
                />
                <span>Remember me on this workstation</span>
              </label>
            </>
          ) : (
            <div className="space-y-2">
              <Label htmlFor="totpCode">6-Digit Authenticator Code</Label>
              <Input
                id="totpCode"
                inputMode="numeric"
                maxLength={6}
                placeholder="123456"
                autoFocus
                {...register("totpCode")}
              />
            </div>
          )}
        </CardContent>

        <CardFooter className="flex flex-col gap-4">
          <Button type="submit" className="w-full" disabled={submitting}>
            {submitting
              ? "Verifying..."
              : mfaRequired
                ? "Verify & Sign In"
                : "Sign In"}
          </Button>
          {mfaRequired ? (
            <button
              type="button"
              onClick={() => {
                setMfaRequired(false);
                setServerError(null);
              }}
              className="text-xs text-muted-foreground hover:underline"
            >
              Back to credentials
            </button>
          ) : (
            <p className="text-center text-sm text-muted-foreground">
              Don&apos;t have an account?{" "}
              <Link
                to="/signup"
                className="font-medium text-primary hover:underline"
              >
                Create account
              </Link>
            </p>
          )}
        </CardFooter>
      </form>
    </Card>
  );
}
