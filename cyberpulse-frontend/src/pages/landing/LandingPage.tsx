import { ArrowRight, ShieldCheck } from "lucide-react";
import { Link } from "react-router-dom";

import { buttonVariants } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export function LandingPage() {
  return (
    <main className="min-h-screen">
      {/* Hero Section */}
      <section className="relative overflow-hidden">
        <div className="absolute inset-0 -z-10 bg-[radial-gradient(circle_at_top_right,rgba(59,130,246,0.12),transparent_35%),radial-gradient(circle_at_bottom_left,rgba(16,185,129,0.08),transparent_30%)]" />

        <div className="mx-auto flex min-h-[90vh] w-full max-w-7xl items-center px-6 py-24 lg:px-8">
          <div className="max-w-4xl">
            {/* Badge */}
            <div className="mb-8 inline-flex items-center gap-2 rounded-full border bg-background/80 px-4 py-2 text-sm font-medium shadow-sm backdrop-blur">
              <ShieldCheck className="h-4 w-4" />
              AI-Powered Security Operations Platform
            </div>

            {/* Heading */}
            <h1 className="text-5xl font-bold tracking-tight sm:text-6xl lg:text-7xl">
              Detect threats.
              <br />
              <span className="text-muted-foreground">
                Assess risk.
              </span>
              <br />
              Respond faster.
            </h1>

            {/* Description */}
            <p className="mt-8 max-w-2xl text-lg leading-8 text-muted-foreground">
              CyberPulse is a modern Security Operations Center platform
              designed to help security teams detect threats, investigate
              security events, assess risk, manage incidents, and respond
              efficiently.
            </p>

            {/* CTA */}
            <div className="mt-10 flex flex-wrap gap-4">
              <Link
                to="/signup"
                className={cn(
                  buttonVariants({ size: "lg" }),
                  "gap-2",
                )}
              >
                Get Started
                <ArrowRight className="h-4 w-4" />
              </Link>

              <Link
                to="/login"
                className={buttonVariants({
                  size: "lg",
                  variant: "outline",
                })}
              >
                Sign In
              </Link>
            </div>

            {/* Trust indicators */}
            <div className="mt-12 flex flex-wrap gap-x-8 gap-y-3 text-sm text-muted-foreground">
              <span>✓ Role-based access control</span>
              <span>✓ Security event management</span>
              <span>✓ Risk assessment</span>
              <span>✓ Incident management</span>
            </div>
          </div>
        </div>
      </section>

      {/* Platform Overview */}
      <section className="border-t bg-muted/30">
        <div className="mx-auto w-full max-w-7xl px-6 py-24 lg:px-8">
          <div className="max-w-2xl">
            <p className="text-sm font-semibold uppercase tracking-wider text-muted-foreground">
              Security Operations
            </p>

            <h2 className="mt-3 text-3xl font-bold tracking-tight sm:text-4xl">
              One platform for your security workflow
            </h2>

            <p className="mt-4 text-muted-foreground">
              CyberPulse brings security events, threat detection, risk
              assessment, incidents, notifications, and audit activity into
              one operational platform.
            </p>
          </div>

          <div className="mt-12 grid gap-6 md:grid-cols-2 lg:grid-cols-4">
            <FeatureCard
              title="Threat Detection"
              description="Identify suspicious activity and security threats from incoming events."
            />

            <FeatureCard
              title="Risk Assessment"
              description="Evaluate detected threats and prioritize security risks."
            />

            <FeatureCard
              title="Incident Management"
              description="Track, investigate, and manage security incidents throughout their lifecycle."
            />

            <FeatureCard
              title="Audit & Visibility"
              description="Maintain operational visibility into security actions and system activity."
            />
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section>
        <div className="mx-auto w-full max-w-7xl px-6 py-24 lg:px-8">
          <div className="text-center">
            <p className="text-sm font-semibold uppercase tracking-wider text-muted-foreground">
              Workflow
            </p>

            <h2 className="mt-3 text-3xl font-bold tracking-tight sm:text-4xl">
              From detection to response
            </h2>

            <p className="mx-auto mt-4 max-w-2xl text-muted-foreground">
              A structured security workflow helps teams move from raw
              security events to actionable incident response.
            </p>
          </div>

          <div className="mt-14 grid gap-6 md:grid-cols-4">
            <WorkflowStep
              number="01"
              title="Collect"
              description="Security events enter the CyberPulse platform."
            />

            <WorkflowStep
              number="02"
              title="Detect"
              description="Threat detection identifies suspicious activity."
            />

            <WorkflowStep
              number="03"
              title="Assess"
              description="Detected threats are evaluated for risk and severity."
            />

            <WorkflowStep
              number="04"
              title="Respond"
              description="Security teams investigate and manage incidents."
            />
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="border-t bg-muted/30">
        <div className="mx-auto max-w-7xl px-6 py-24 text-center lg:px-8">
          <ShieldCheck className="mx-auto h-10 w-10" />

          <h2 className="mt-6 text-3xl font-bold tracking-tight sm:text-4xl">
            Start securing your operations
          </h2>

          <p className="mx-auto mt-4 max-w-xl text-muted-foreground">
            Access the CyberPulse security operations platform and manage your
            security workflow from a centralized dashboard.
          </p>

          <div className="mt-8">
            <Link
              to="/signup"
              className={cn(
                buttonVariants({ size: "lg" }),
                "gap-2",
              )}
            >
              Create Account
              <ArrowRight className="h-4 w-4" />
            </Link>
          </div>
        </div>
      </section>
    </main>
  );
}

function FeatureCard({
  title,
  description,
}: {
  title: string;
  description: string;
}) {
  return (
    <div className="rounded-xl border bg-background p-6 shadow-sm transition-shadow hover:shadow-md">
      <div className="mb-4 flex h-10 w-10 items-center justify-center rounded-lg border">
        <ShieldCheck className="h-5 w-5" />
      </div>

      <h3 className="text-lg font-semibold">{title}</h3>

      <p className="mt-2 text-sm leading-6 text-muted-foreground">
        {description}
      </p>
    </div>
  );
}

function WorkflowStep({
  number,
  title,
  description,
}: {
  number: string;
  title: string;
  description: string;
}) {
  return (
    <div className="rounded-xl border bg-background p-6">
      <span className="text-sm font-semibold text-muted-foreground">
        {number}
      </span>

      <h3 className="mt-4 text-xl font-semibold">{title}</h3>

      <p className="mt-2 text-sm leading-6 text-muted-foreground">
        {description}
      </p>
    </div>
  );
}