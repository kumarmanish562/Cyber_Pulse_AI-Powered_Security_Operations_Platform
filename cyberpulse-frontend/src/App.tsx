import { Button } from "@/components/ui/button";

function App() {
  return (
    <main className="flex min-h-svh items-center justify-center">
      <div className="text-center">
        <h1 className="mb-4 text-4xl font-bold">
          CyberPulse
        </h1>

        <p className="mb-6 text-muted-foreground">
          AI-Powered Security Operations Platform
        </p>

        <Button>
          Get Started
        </Button>
      </div>
    </main>
  );
}

export default App;