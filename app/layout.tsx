import type { Metadata } from "next";

import { ApolloWrapper } from "@/graphql/apollo-wrapper";
import Navbar from "@/components/layout/navbar";
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
        className={`${jost.variable} ${kalam.variable} font-jost bg-background min-h-screen antialiased`}
      >
        <Navbar />
        <ApolloWrapper>{children}</ApolloWrapper>
      </body>
    </html>
  );
}
