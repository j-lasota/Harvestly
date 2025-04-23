import type { Metadata } from "next";

import Navbar from "@/app/components/navbar";
import { jost, kalam } from "./fonts";
import "./globals.css";

export const metadata: Metadata = {
  title: "Harvestly",
  description: "Local sellers and local products",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${jost.variable} ${kalam.variable} font-jost bg-background antialiased`}
      >
        <Navbar />
        {children}
      </body>
    </html>
  );
}
