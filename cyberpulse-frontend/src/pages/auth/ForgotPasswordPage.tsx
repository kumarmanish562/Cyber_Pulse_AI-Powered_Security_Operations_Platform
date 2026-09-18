export function ForgotPasswordPage() {
  return (
    <main className="flex min-h-screen items-center justify-center">
      <div className="text-center">
        <h1 className="text-2xl font-bold">
          Forgot Password
        </h1>

        <p className="mt-2 text-muted-foreground">
          Password recovery will be connected when
          the backend password-reset API is available.
        </p>
      </div>
    </main>
  );
}