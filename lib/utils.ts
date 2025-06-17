import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function getShortTime(fullTime: string): string | undefined {
  if (!fullTime) return undefined;

  return fullTime.split(":").slice(0, 2).join(":");
}
