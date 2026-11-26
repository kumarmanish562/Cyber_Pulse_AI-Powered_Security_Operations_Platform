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
  Sparkles,
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
    accent: "text-red-400",
  },
  {
    label: "Active Incidents",
    value: "07",
    delta: "2 High / 1 Critical",
    icon: AlertTriangle,
    accent: "text-orange-400",
  },
  {
    label: "Risk Score",
    value: "84 / 100",
    delta: "Dynamic weighted model",
    icon: Gauge,
    accent: "text-amber-400",
  },
  {
    label: "Events Analyzed",
    value: "4.2M",
    delta: "Async pipeline active",
    icon: Activity,
    accent: "text-cyan-400",
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
    <div className="min-h-screen bg-background text-foreground">
      <LandingNavbar />

      <main>
        {/* Hero Section */}
        <section className="relative overflow-hidden border-b border-border/60">
          {/* Atmospheric SOC background */}
          <div className="pointer-events-none absolute inset-0 -z-10 bg-[radial-gradient(ellipse_at_top_right,rgba(56,189,248,0.14),transparent_45%),radial-gradient(ellipse_at_bottom_left,rgba(139,92,246,0.12),transparent_45%)]" />
          <div className="pointer-events-none absolute inset-0 -z-10 bg-[linear-gradient(to_right,rgba(148,163,184,0.05)_1px,transparent_1px),linear-gradient(to_bottom,rgba(148,163,184,0.05)_1px,transparent_1px)] bg-[size:48px_48px]" />

          <div className="mx-auto grid w-full max-w-7xl items-center gap-12 px-4 py-16 sm:px-6 lg:grid-cols-12 lg:px-8 lg:py-24">
            {/* Left Column */}
            <div className="animate-in fade-in slide-in-from-bottom-4 duration-700 lg:col-span-6">
              <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-primary/30 bg-primary/10 px-3.5 py-1.5 text-xs font-semibold tracking-wider text-primary uppercase">
                <Sparkles className="h-3.5 w-3.5" />
                AI-Powered Security Operations Center
              </div>

              <h1 className="text-4xl font-bold tracking-tight sm:text-5xl lg:text-6xl">
                See Every Threat.
                <span className="mt-1 block bg-gradient-to-r from-cyan-400 via-blue-400 to-violet-400 bg-clip-text text-transparent">
                  Stop It Before It Spreads.
                </span>
              </h1>

              <p className="mt-6 max-w-xl text-base leading-relaxed text-muted-foreground sm:text-lg">
                Intelligent Security. Faster Response. Stronger Defense.
                CyberPulse unifies security event monitoring, AI-assisted
                threat triage, risk scoring, and automated incident containment
                in one modern SOC platform.
              </p>

              <div className="mt-8 flex flex-wrap gap-3.5">
                <Link
                  to="/signup"
                  className={cn(
                    buttonVariants({ size: "lg" }),
                    "gap-2 shadow-lg shadow-primary/20",
                  )}
                >
                  Explore Platform
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

              <div className="mt-8 flex flex-wrap gap-x-6 gap-y-2.5 text-xs font-medium text-muted-foreground sm:text-sm">
                <span className="flex items-center gap-1.5">
                  <CheckCircle2 className="h-4 w-4 text-cyan-400" />
                  Real-time threat intelligence
                </span>
                <span className="flex items-center gap-1.5">
                  <CheckCircle2 className="h-4 w-4 text-violet-400" />
                  AI-powered security analysis
                </span>
                <span className="flex items-center gap-1.5">
                  <CheckCircle2 className="h-4 w-4 text-emerald-400" />
                  Enterprise-grade security
                </span>
              </div>
            </div>

            {/* Right Column: Live SOC Preview */}
            <div className="animate-in fade-in slide-in-from-bottom-6 duration-700 lg:col-span-6">
              <HeroSocPreview />
            </div>
          </div>
        </section>

        {/* Security Metrics Section (Illustrative Demo Data) */}
        <section
          id="metrics"
          className="border-b border-border/60 bg-muted/20 py-14"
        >
          <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8">
            <div className="mb-6 flex flex-wrap items-center justify-between gap-2">
              <p className="text-xs font-semibold tracking-wider text-muted-foreground uppercase">
                SOC Telemetry Snapshot
              </p>
              <Badge variant="outline" className="font-mono text-[10px]">
                Illustrative Demo Metrics
              </Badge>
            </div>

            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
              {DEMO_METRICS.map((metric) => {
                const Icon = metric.icon;
                return (
                  <div
                    key={metric.label}
                    className="group rounded-xl border border-border/70 bg-card/70 p-5 transition-all hover:-translate-y-0.5 hover:border-primary/40"
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-xs font-medium text-muted-foreground">
                        {metric.label}
                      </span>
                      <Icon className={cn("h-4 w-4", metric.accent)} />
                    </div>
                    <div className="mt-3 text-3xl font-bold tracking-tight">
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

        {/* Platform Features Section */}
        <section id="features" className="border-b border-border/60 py-20">
          <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8">
            <div className="max-w-2xl">
              <p className="text-xs font-semibold tracking-wider text-primary uppercase">
                Platform Capabilities
              </p>
              <h2 className="mt-2 text-3xl font-bold tracking-tight sm:text-4xl">
                Everything Your Security Team Needs.
              </h2>
              <p className="mt-3 text-muted-foreground">
                Purpose-built modules for high-throughput event ingestion,
                detection engineering, risk scoring, and coordinated response.
              </p>
            </div>

            <div className="mt-12 grid gap-5 md:grid-cols-2 lg:grid-cols-3">
              {PLATFORM_FEATURES.map((feature) => {
                const Icon = feature.icon;
                return (
                  <div
                    key={feature.title}
                    className="group relative flex flex-col justify-between rounded-xl border border-border/70 bg-card/70 p-6 transition-all duration-200 hover:-translate-y-0.5 hover:border-primary/50 hover:shadow-lg hover:shadow-primary/5"
                  >
                    <div>
                      <div className="flex items-center justify-between">
                        <div className="flex h-10 w-10 items-center justify-center rounded-lg border border-primary/30 bg-primary/10 text-primary transition-colors group-hover:bg-primary group-hover:text-primary-foreground">
                          <Icon className="h-5 w-5" />
                        </div>
                        <Badge
                          variant="secondary"
                          className="font-mono text-[10px]"
                        >
                          {feature.tag}
                        </Badge>
                      </div>
                      <h3 className="mt-5 text-lg font-semibold tracking-tight">
                        {feature.title}
                      </h3>
                      <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
                        {feature.description}
                      </p>
                    </div>
                    <div className="mt-5 h-px w-full bg-gradient-to-r from-primary/40 via-cyan-400/20 to-transparent opacity-60 transition-opacity group-hover:opacity-100" />
                  </div>
                );
              })}
            </div>
          </div>
        </section>

        {/* How It Works Section */}
        <section
          id="workflow"
          className="border-b border-border/60 bg-muted/20 py-20"
        >
          <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8">
            <div className="max-w-2xl">
              <p className="text-xs font-semibold tracking-wider text-primary uppercase">
                End-to-End SOC Pipeline
              </p>
              <h2 className="mt-2 text-3xl font-bold tracking-tight sm:text-4xl">
                From Raw Telemetry to Automated Containment.
              </h2>
            </div>

            <div className="mt-12 grid gap-4 md:grid-cols-5">
              {WORKFLOW_STEPS.map((item, idx) => (
                <div
                  key={item.step}
                  className="relative rounded-xl border border-border/70 bg-card/75 p-5"
                >
                  <div className="flex items-center justify-between">
                    <span className="font-mono text-xs font-bold text-cyan-400">
                      {item.step}
                    </span>
                    {idx < WORKFLOW_STEPS.length - 1 && (
                      <ArrowRight className="hidden h-3.5 w-3.5 text-muted-foreground md:block" />
                    )}
                  </div>
                  <h3 className="mt-3 text-sm font-semibold">{item.title}</h3>
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
          className="border-b border-border/60 py-20"
        >
          <div className="mx-auto grid w-full max-w-7xl items-center gap-10 px-4 sm:px-6 lg:grid-cols-12 lg:px-8">
            <div className="lg:col-span-5">
              <div className="inline-flex items-center gap-2 rounded-full border border-violet-500/30 bg-violet-500/10 px-3 py-1 text-xs font-semibold text-violet-400">
                <BrainCircuit className="h-3.5 w-3.5" />
                CyberPulse AI Engine
              </div>
              <h2 className="mt-4 text-3xl font-bold tracking-tight sm:text-4xl">
                Security Intelligence, Powered by AI.
              </h2>
              <p className="mt-4 text-muted-foreground">
                Turn noisy security alerts into clear, actionable investigation
                briefs using local Ollama or cloud LLM triage with deterministic
                playbook fallback.
              </p>

              <ul className="mt-6 space-y-3 text-sm">
                {AI_CAPABILITIES.map((cap) => (
                  <li key={cap} className="flex items-center gap-2.5">
                    <CheckCircle2 className="h-4 w-4 shrink-0 text-violet-400" />
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
        <section className="relative overflow-hidden border-b border-border/60 py-20">
          <div className="pointer-events-none absolute inset-0 -z-10 bg-[radial-gradient(circle_at_center,rgba(59,130,246,0.18),transparent_60%)]" />
          <div className="mx-auto max-w-4xl px-4 text-center sm:px-6 lg:px-8">
            <h2 className="text-3xl font-bold tracking-tight sm:text-4xl">
              Your Next Security Incident Shouldn&apos;t Be a Surprise.
            </h2>
            <p className="mx-auto mt-4 max-w-2xl text-muted-foreground">
              Bring threat detection, investigation, and incident response into
              one intelligent security platform.
            </p>
            <div className="mt-8 flex flex-wrap justify-center gap-4">
              <Link to="/signup" className={buttonVariants({ size: "lg" })}>
                Get Started
                <ArrowRight className="ml-2 h-4 w-4" />
              </Link>
              <Link
                to="/login"
                className={buttonVariants({ size: "lg", variant: "outline" })}
              >
                Sign In
              </Link>
            </div>
          </div>
        </section>
      </main>

      {/* Enterprise Footer */}
      <footer className="bg-muted/20 py-12">
        <div className="mx-auto grid w-full max-w-7xl gap-8 px-4 sm:grid-cols-2 sm:px-6 lg:grid-cols-4 lg:px-8">
          <div>
            <div className="flex items-center gap-2">
              <ShieldCheck className="h-5 w-5 text-primary" />
              <span className="font-bold">CyberPulse</span>
            </div>
            <p className="mt-3 text-xs leading-relaxed text-muted-foreground">
              AI-powered Security Operations Center platform for threat
              detection, risk assessment, and incident response.
            </p>
          </div>

          <div>
            <h4 className="text-xs font-semibold uppercase tracking-wider">
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
            <h4 className="text-xs font-semibold uppercase tracking-wider">
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
            <h4 className="text-xs font-semibold uppercase tracking-wider">
              Documentation &amp; Contact
            </h4>
            <ul className="mt-3 space-y-2 text-xs text-muted-foreground">
              <li>OpenAPI / Swagger (`/swagger-ui.html`)</li>
              <li>Prometheus Metrics (`/actuator/prometheus`)</li>
              <li>Contact: security@cyberpulse.local</li>
            </ul>
          </div>
        </div>

        <div className="mx-auto mt-10 flex w-full max-w-7xl flex-wrap items-center justify-between gap-4 border-t border-border/60 px-4 pt-6 text-xs text-muted-foreground sm:px-6 lg:px-8">
          <span>© {new Date().getFullYear()} CyberPulse SOC Platform.</span>
          <span>Intelligent Security. Faster Response. Stronger Defense.</span>
        </div>
      </footer>
    </div>
  );
}
