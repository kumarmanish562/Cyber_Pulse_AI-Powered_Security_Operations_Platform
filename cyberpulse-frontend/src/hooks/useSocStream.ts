import { useEffect } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { tokenStorage } from "@/lib/api";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL?.trim() || "http://localhost:8080";

/**
 * Subscribes to /api/v1/stream over Server-Sent Events using fetch() so
 * Authorization Bearer tokens are sent in headers rather than URLs.
 */
export function useSocStream(enabled: boolean) {
  const queryClient = useQueryClient();

  useEffect(() => {
    if (!enabled) return;
    const token = tokenStorage.getAccessToken();
    if (!token) return;

    const controller = new AbortController();
    let reconnectTimer: number | undefined;

    async function connect() {
      try {
        const response = await fetch(`${API_BASE_URL}/api/v1/stream`, {
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "text/event-stream",
          },
          signal: controller.signal,
        });

        if (!response.ok || !response.body) {
          throw new Error(`SSE status ${response.status}`);
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = "";

        while (true) {
          const { value, done } = await reader.read();
          if (done) break;

          buffer += decoder.decode(value, { stream: true });
          const frames = buffer.split("\n\n");
          buffer = frames.pop() ?? "";

          for (const frame of frames) {
            const eventLine = frame
              .split("\n")
              .find((l) => l.startsWith("event:"));
            const eventName = eventLine?.slice(6).trim();

            if (eventName === "detection") {
              queryClient.invalidateQueries({ queryKey: ["detections"] });
              queryClient.invalidateQueries({ queryKey: ["dashboard"] });
              toast.warning("New threat detection triggered");
            } else if (eventName === "incident") {
              queryClient.invalidateQueries({ queryKey: ["incidents"] });
              queryClient.invalidateQueries({ queryKey: ["notifications"] });
              queryClient.invalidateQueries({ queryKey: ["dashboard"] });
              toast.error("New security incident created");
            } else if (eventName === "notification") {
              queryClient.invalidateQueries({ queryKey: ["notifications"] });
            }
          }
        }
      } catch {
        if (!controller.signal.aborted) {
          reconnectTimer = window.setTimeout(connect, 5000);
        }
      }
    }

    connect();

    return () => {
      controller.abort();
      if (reconnectTimer) {
        window.clearTimeout(reconnectTimer);
      }
    };
  }, [enabled, queryClient]);
}
