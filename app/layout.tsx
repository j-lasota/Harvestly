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
    <html lang="en" className="h-full">
      <body
        className={`${jost.variable} ${kalam.variable} font-jost bg-background h-full antialiased`}
      >
        <div className="flex h-full flex-col">
          <Navbar />
          <div className="relative flex-1">
            <ApolloWrapper>{children}</ApolloWrapper>
          </div>
        </div>
      </body>
    </html>
  );
}
