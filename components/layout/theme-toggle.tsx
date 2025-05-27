"use client";

import { useEffect, useState } from "react";
import { Contrast } from "lucide-react";

import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export default function ThemeToggle() {
  const [isContrast, setIsContrast] = useState(false);
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
    const contrast = localStorage.getItem("contrast") === "true";
    setIsContrast(contrast);
  }, []);

  useEffect(() => {
    if (!mounted) return;

    const html = document.documentElement;
    if (isContrast) {
      html.classList.add("contrast");
      localStorage.setItem("contrast", "true");
    } else {
      html.classList.remove("contrast");
      localStorage.setItem("contrast", "false");
    }
  }, [isContrast, mounted]);

  if (!mounted) {
    return null; // lub placeholder ładowania
  }

  return (
    <Button
      variant="ghost"
      size="icon"
      type="button"
      className="hover:bg-hover-background active:bg-active-background text-foreground[transition:background_20ms_ease-in,_color_0.15s] cursor-pointer rounded-md p-1.5"
      title={isContrast ? "Disable contrast mode" : "Enable contrast mode"}
      aria-label={isContrast ? "Disable contrast mode" : "Enable contrast mode"}
      onClick={() => setIsContrast(!isContrast)}
    >
      <Contrast className={cn("h-5 w-5", isContrast && "text-primary")} />
    </Button>
  );
}
