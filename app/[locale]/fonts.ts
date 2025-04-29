import { Jost, Kalam } from "next/font/google";

export const jost = Jost({
  subsets: ["latin"],
  variable: "--font-jost",
});

export const kalam = Kalam({
  weight: ["300", "400", "700"],
  variable: "--font-kalam",
});
