import {
  useState,
  type FormEvent,
} from "react";

import {
  Link,
  useNavigate,
} from "react-router-dom";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

import { useAuth } from "@/hooks/useAuth";

export function LoginPage() {
  const navigate = useNavigate();

  const { login } = useAuth();

  const [usernameOrEmail, setUsernameOrEmail] =
    useState("");

  const [password, setPassword] =
    useState("");

  const [error, setError] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      await login({
        usernameOrEmail:
          usernameOrEmail.trim(),
        password,
      });

      navigate("/dashboard", {
        replace: true,
      });
    } catch (error: any) {
      console.error(
        "Login error:",
        error?.response?.data ?? error,
      );

      setError(
        error?.response?.data?.message ??
          "Invalid username/email or password.",
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center px-6">
      <div className="w-full max-w-md rounded-2xl border bg-card p-8 shadow-sm">

        <div className="mb-8">
          <h1 className="text-2xl font-bold">
            Sign in to CyberPulse
          </h1>

          <p className="mt-2 text-muted-foreground">
            Access your Security Operations Center.
          </p>
        </div>

        {error && (
          <div className="mb-6 rounded-lg border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
            {error}
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          className="space-y-5"
        >
          <div className="space-y-2">
            <Label htmlFor="usernameOrEmail">
              Email or Username
            </Label>

            <Input
              id="usernameOrEmail"
              type="text"
              value={usernameOrEmail}
              onChange={(event) =>
                setUsernameOrEmail(
                  event.target.value,
                )
              }
              placeholder="Email or username"
              autoComplete="username"
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="password">
              Password
            </Label>

            <Input
              id="password"
              type="password"
              value={password}
              onChange={(event) =>
                setPassword(
                  event.target.value,
                )
              }
              placeholder="Enter your password"
              autoComplete="current-password"
              required
            />
          </div>

          <Button
            type="submit"
            className="w-full"
            disabled={loading}
          >
            {loading
              ? "Signing in..."
              : "Sign In"}
          </Button>
        </form>

        <div className="mt-6 flex justify-between text-sm">
          <Link
            to="/forgot-password"
            className="text-muted-foreground hover:text-foreground"
          >
            Forgot password?
          </Link>

          <Link
            to="/signup"
            className="font-medium hover:underline"
          >
            Create account
          </Link>
        </div>
      </div>
    </main>
  );
}