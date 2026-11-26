import {
  Activity,
  AlertTriangle,
  ArrowRight,
  BrainCircuit,
  CheckCircle2,
  Gauge,
  Lock,
  Radar,
  ShieldAlert,
  ShieldCheck,
} from "lucide-react";
import { Link } from "react-router-dom";
import { AiIntelligencePreview } from "@/components/landing/AiIntelligencePreview";
import { HeroSocPreview } from "@/components/landing/HeroSocPreview";
import { LandingNavbar } from "@/components/landing/LandingNavbar";
import { Badge } from "@/components/ui/badge";
import { buttonVariants } from "@/components/ui/button";
import { cn } from "@/lib/utils";

const DEMO_METRICS = [
  {
    label: "Threats Detected",
    value: "1,284",
    delta: "+14% vs last 24h",
    icon: ShieldAlert,
  },
  {
    label: "Active Incidents",
    value: "07",
    delta: "2 High / 1 Critical",
    icon: AlertTriangle,
  },
  {
    label: "Risk Score",
    value: "84 / 100",
    delta: "Dynamic weighted model",
    icon: Gauge,
  },
  {
    label: "Events Analyzed",
    value: "4.2M",
    delta: "Async pipeline active",
    icon: Activity,
  },
];

const PLATFORM_FEATURES = [
  {
    title: "Threat Detection",
    description:
      "Identify suspicious activities and potential threats across brute-force, port scan, and login anomalies.",
    icon: Radar,
    tag: "Rule Engine",
  },
  {
    title: "Security Event Monitoring",
    description:
      "Monitor security events from a centralized interface with batch ingestion and real-time SSE streaming.",
    icon: Activity,
    tag: "Live Telemetry",
  },
  {
    title: "Intelligent Risk Assessment",
    description:
      "Prioritize threats based on severity, confidence, and frequency with transparent score breakdowns.",
    icon: Gauge,
    tag: "0–100 Scoring",
  },
  {
    title: "Incident Management",
    description:
      "Track, investigate, and manage security incidents from triage through containment and closure.",
    icon: AlertTriangle,
    tag: "SLA Tracked",
  },
  {
    title: "AI Security Intelligence",
    description:
      "Use AI-assisted analysis to understand security events, map MITRE tactics, and accelerate containment.",
    icon: BrainCircuit,
    tag: "LLM Triage",
  },
  {
    title: "Access Control",
    description:
      "Manage users, roles, service API keys, and TOTP two-factor authentication securely.",
    icon: Lock,
    tag: "RBAC + 2FA",
  },
];

const WORKFLOW_STEPS = [
  {
    step: "01",
    title: "Collect Security Events",
    detail: "Ingest telemetry via REST, batch collectors, or X-API-Key shippers.",
  },
  {
    step: "02",
    title: "Analyze Threat Intelligence",
    detail: "Evaluate detection rules asynchronously and enrich source IPs.",
  },
  {
    step: "03",
    title: "Assess Risk",
    detail: "Calculate multi-factor risk scores and map MITRE ATT&CK techniques.",
  },
  {
    step: "04",
    title: "Investigate Incidents",
    detail: "Triage with AI root-cause summaries, notes, and SLA tracking.",
  },
  {
    step: "05",
    title: "Respond to Threats",
    detail: "Execute one-click SOAR playbooks, block IPs, and dispatch alerts.",
  },
];

const AI_CAPABILITIES = [
  "AI-powered event analysis and correlation",
  "Suspicious activity identification across multi-IP logins",
  "Executive threat summarization for rapid handovers",
  "Automated risk prioritization and SLA awareness",
  "Step-by-step investigation and containment assistance",
];

export function LandingPage() {
  return (
    <div className="min-h-screen bg-background text-foreground antialiased">
      <LandingNavbar />

      <main>
        {/* Editorial Hero Section */}
        <section className="border-b border-border/60 pt-16 pb-20 sm:pt-24 sm:pb-28">
          <div className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8">
            <div className="mx-auto max-w-3xl text-center">
              <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-border bg-muted/40 px-3.5 py-1 text-xs font-medium text-muted-foreground">
                <span className="h-1.5 w-1.5 rounded-full bg-emerald-500" />
                AI-Powered Security Operations Center
              </div>

              <h1 className="text-4xl font-semibold tracking-[-0.035em] sm:text-6xl lg:text-[64px] lg:leading-[1.06]">
                See Every Threat.
                <span className="mt-1 block text-muted-foreground">
                  Stop It Before It Spreads.
                </span>
              </h1>

              <p className="mx-auto mt-6 max-w-2xl text-base leading-relaxed text-muted-foreground sm:text-lg">
                Intelligent Security. Faster Response. Stronger Defense.
                CyberPulse unifies security event monitoring, AI-assisted
                threat triage, risk scoring, and automated incident containment
                in one disciplined SOC workspace.
              </p>

              <div className="mt-9 flex flex-wrap items-center justify-center gap-3">
                <Link
                  to="/signup"
                  className={cn(
                    buttonVariants({ size: "lg" }),
                    "h-11 rounded-full bg-foreground px-7 text-sm font-medium text-background hover:bg-foreground/90",
                  )}
                >
                  Explore Platform
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Link>

                <Link
                  to="/login"
                  className={cn(
                    buttonVariants({ size: "lg", variant: "outline" }),
                    "h-11 rounded-full px-7 text-sm font-medium",
                  )}
                >
                  Sign In
                </Link>
              </div>

              <div className="mt-8 flex flex-wrap items-center justify-center gap-x-6 gap-y-2 text-xs text-muted-foreground">
                <span className="flex items-center gap-1.5">
                  <CheckCircle2 className="h-3.5 w-3.5 text-foreground/70" />
                  Real-time threat intelligence
                </span>
                <span className="flex items-center gap-1.5">
                  <CheckCircle2 className="h-3.5 w-3.5 text-foreground/70" />
                  AI-powered security analysis
                </span>
                <span className="flex items-center gap-1.5">
                  <CheckCircle2 className="h-3.5 w-3.5 text-foreground/70" />
                  Enterprise-grade security
                </span>
              </div>
            </div>

            {/* Product Showcase Frame */}
            <div className="mt-14 sm:mt-16">
              <HeroSocPreview />
            </div>
          </div>
        </section>

        {/* Security Metrics Strip (Hairline Bento Grid) */}
        <section id="metrics" className="border-b border-border/60 py-16">
          <div className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8">
            <div className="mb-6 flex flex-wrap items-center justify-between gap-2">
              <p className="text-xs font-medium tracking-wider text-muted-foreground uppercase">
                SOC Telemetry Snapshot
              </p>
              <Badge
                variant="outline"
                className="rounded-full font-mono text-[10px] text-muted-foreground"
              >
                Illustrative Demo Metrics
              </Badge>
            </div>

            <div className="grid divide-y divide-border overflow-hidden rounded-2xl border border-border bg-card/40 sm:grid-cols-2 sm:divide-x lg:grid-cols-4 lg:divide-y-0">
              {DEMO_METRICS.map((metric) => {
                const Icon = metric.icon;
                return (
                  <div
                    key={metric.label}
                    className="p-6 transition-colors hover:bg-muted/30"
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-xs font-medium text-muted-foreground">
                        {metric.label}
                      </span>
                      <Icon className="h-4 w-4 text-muted-foreground" />
                    </div>
                    <div className="mt-3 text-3xl font-semibold tracking-tight">
                      {metric.value}
                    </div>
                    <div className="mt-1 text-xs text-muted-foreground">
                      {metric.delta}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </section>

        {/* Platform Features Gallery */}
        <section id="features" className="border-b border-border/60 py-24">
          <div className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8">
            <div className="max-w-2xl">
              <p className="text-xs font-medium tracking-wider text-muted-foreground uppercase">
                Platform Capabilities
              </p>
              <h2 className="mt-2.5 text-3xl font-semibold tracking-[-0.03em] sm:text-4xl">
                Everything Your Security Team Needs.
              </h2>
              <p className="mt-3 text-base text-muted-foreground">
                Purpose-built modules for high-throughput event ingestion,
                detection engineering, risk scoring, and coordinated response.
              </p>
            </div>

            <div className="mt-12 grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {PLATFORM_FEATURES.map((feature) => {
                const Icon = feature.icon;
                return (
                  <div
                    key={feature.title}
                    className="group flex flex-col justify-between rounded-2xl border border-border/80 bg-card/40 p-6 transition-all duration-200 hover:border-foreground/25 hover:bg-card"
                  >
                    <div>
                      <div className="flex items-center justify-between">
                        <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-border bg-background text-foreground transition-colors group-hover:bg-foreground group-hover:text-background">
                          <Icon className="h-4.5 w-4.5" />
                        </div>
                        <Badge
                          variant="secondary"
                          className="rounded-full font-mono text-[10px]"
                        >
                          {feature.tag}
                        </Badge>
                      </div>
                      <h3 className="mt-5 text-base font-semibold tracking-tight">
                        {feature.title}
                      </h3>
                      <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
                        {feature.description}
                      </p>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </section>

        {/* How It Works Pipeline */}
        <section
          id="workflow"
          className="border-b border-border/60 bg-muted/20 py-24"
        >
          <div className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8">
            <div className="max-w-2xl">
              <p className="text-xs font-medium tracking-wider text-muted-foreground uppercase">
                End-to-End SOC Pipeline
              </p>
              <h2 className="mt-2.5 text-3xl font-semibold tracking-[-0.03em] sm:text-4xl">
                From Raw Telemetry to Automated Containment.
              </h2>
            </div>

            <div className="mt-12 grid divide-y divide-border overflow-hidden rounded-2xl border border-border bg-card md:grid-cols-5 md:divide-x md:divide-y-0">
              {WORKFLOW_STEPS.map((item) => (
                <div
                  key={item.step}
                  className="p-6 transition-colors hover:bg-muted/30"
                >
                  <span className="font-mono text-xs font-medium text-muted-foreground">
                    {item.step}
                  </span>
                  <h3 className="mt-3 text-sm font-semibold tracking-tight">
                    {item.title}
                  </h3>
                  <p className="mt-2 text-xs leading-relaxed text-muted-foreground">
                    {item.detail}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* AI Intelligence Section */}
        <section
          id="ai-intelligence"
          className="border-b border-border/60 py-24"
        >
          <div className="mx-auto grid w-full max-w-6xl items-center gap-12 px-4 sm:px-6 lg:grid-cols-12 lg:px-8">
            <div className="lg:col-span-5">
              <div className="inline-flex items-center gap-2 rounded-full border border-border bg-muted/40 px-3 py-1 text-xs font-medium text-muted-foreground">
                <BrainCircuit className="h-3.5 w-3.5 text-foreground" />
                CyberPulse AI Engine
              </div>
              <h2 className="mt-4 text-3xl font-semibold tracking-[-0.03em] sm:text-4xl">
                Security Intelligence, Powered by AI.
              </h2>
              <p className="mt-4 text-base leading-relaxed text-muted-foreground">
                Turn noisy security alerts into clear, actionable investigation
                briefs using local Ollama or cloud LLM triage with deterministic
                playbook fallback.
              </p>

              <ul className="mt-6 space-y-3 text-sm">
                {AI_CAPABILITIES.map((cap) => (
                  <li key={cap} className="flex items-center gap-2.5">
                    <CheckCircle2 className="h-4 w-4 shrink-0 text-foreground/75" />
                    <span>{cap}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="lg:col-span-7">
              <AiIntelligencePreview />
            </div>
          </div>
        </section>

        {/* Final CTA Section */}
        <section className="border-b border-border/60 py-24">
          <div className="mx-auto max-w-3xl px-4 text-center sm:px-6 lg:px-8">
            <h2 className="text-3xl font-semibold tracking-[-0.03em] sm:text-5xl">
              Your Next Security Incident Shouldn&apos;t Be a Surprise.
            </h2>
            <p className="mx-auto mt-4 max-w-xl text-base text-muted-foreground">
              Bring threat detection, investigation, and incident response into
              one intelligent security platform.
            </p>
            <div className="mt-8 flex flex-wrap justify-center gap-3">
              <Link
                to="/signup"
                className={cn(
                  buttonVariants({ size: "lg" }),
                  "h-11 rounded-full bg-foreground px-7 text-sm font-medium text-background hover:bg-foreground/90",
                )}
              >
                Get Started
                <ArrowRight className="ml-2 h-4 w-4" />
              </Link>
              <Link
                to="/login"
                className={cn(
                  buttonVariants({ size: "lg", variant: "outline" }),
                  "h-11 rounded-full px-7 text-sm font-medium",
                )}
              >
                Sign In
              </Link>
            </div>
          </div>
        </section>
      </main>

      {/* Minimal Enterprise Footer */}
      <footer className="py-14">
        <div className="mx-auto grid w-full max-w-6xl gap-8 px-4 sm:grid-cols-2 sm:px-6 lg:grid-cols-4 lg:px-8">
          <div>
            <div className="flex items-center gap-2">
              <div className="flex h-6 w-6 items-center justify-center rounded-md bg-foreground text-background">
                <ShieldCheck className="h-3.5 w-3.5" />
              </div>
              <span className="text-sm font-semibold">CyberPulse</span>
            </div>
            <p className="mt-3 text-xs leading-relaxed text-muted-foreground">
              AI-powered Security Operations Center platform for threat
              detection, risk assessment, and incident response.
            </p>
          </div>

          <div>
            <h4 className="text-xs font-semibold tracking-wider text-foreground uppercase">
              Platform
            </h4>
            <ul className="mt-3 space-y-2 text-xs text-muted-foreground">
              <li>
                <a href="#features" className="hover:text-foreground">
                  Features
                </a>
              </li>
              <li>
                <a href="#workflow" className="hover:text-foreground">
                  SOC Workflow
                </a>
              </li>
              <li>
                <a href="#ai-intelligence" className="hover:text-foreground">
                  AI Intelligence
                </a>
              </li>
              <li>
                <Link to="/dashboard" className="hover:text-foreground">
                  Operations Console
                </Link>
              </li>
            </ul>
          </div>

          <div>
            <h4 className="text-xs font-semibold tracking-wider text-foreground uppercase">
              Security &amp; Access
            </h4>
            <ul className="mt-3 space-y-2 text-xs text-muted-foreground">
              <li>
                <Link to="/login" className="hover:text-foreground">
                  Sign In
                </Link>
              </li>
              <li>
                <Link to="/signup" className="hover:text-foreground">
                  Create Account
                </Link>
              </li>
              <li>
                <Link to="/forgot-password" className="hover:text-foreground">
                  Password Reset
                </Link>
              </li>
            </ul>
          </div>

          <div>
            <h4 className="text-xs font-semibold tracking-wider text-foreground uppercase">
              Documentation &amp; Contact
            </h4>
            <ul className="mt-3 space-y-2 text-xs text-muted-foreground">
              <li>OpenAPI / Swagger (`/swagger-ui.html`)</li>
              <li>Prometheus Metrics (`/actuator/prometheus`)</li>
              <li>Contact: security@cyberpulse.local</li>
            </ul>
          </div>
        </div>

        <div className="mx-auto mt-12 flex w-full max-w-6xl flex-wrap items-center justify-between gap-4 border-t border-border/60 px-4 pt-6 text-xs text-muted-foreground sm:px-6 lg:px-8">
          <span>© {new Date().getFullYear()} CyberPulse SOC Platform.</span>
          <span>Intelligent Security. Faster Response. Stronger Defense.</span>
        </div>
      </footer>
    </div>
  );
}
