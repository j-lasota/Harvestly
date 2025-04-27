import { ApolloWrapper } from "@/graphql/apollo-wrapper";
import { SessionProvider } from "next-auth/react";
import type { Metadata } from "next";

import Header from "@/components/layout/header";
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
    <html lang="en" className="h-full">
      <body
        className={`${jost.variable} ${kalam.variable} font-jost bg-background h-full antialiased`}
      >
        <SessionProvider>
          <div className="flex h-full flex-col">
            <Header />
            <div className="relative flex-1">
              <ApolloWrapper>{children}</ApolloWrapper>
            </div>
          </div>
        </SessionProvider>
      </body>
    </html>
  );
}
