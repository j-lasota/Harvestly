import { NextIntlClientProvider, hasLocale } from "next-intl";
import { SessionProvider } from "next-auth/react";
import { notFound } from "next/navigation";
import type { Metadata } from "next";

import { ApolloWrapper } from "@/graphql/apollo-wrapper";
import Header from "@/components/layout/header";
import { routing } from "@/i18n/routing";
import { jost, kalam } from "../fonts";
import "../globals.css";

export const metadata: Metadata = {
  title: "Harvestly",
  description: "Local sellers and local products",
};

export default async function RootLayout({
  children,
  params,
}: Readonly<{
  children: React.ReactNode;
  params: Promise<{
    locale: string;
  }>;
}>) {
  const { locale } = await params;
  if (!hasLocale(routing.locales, locale)) {
    notFound();
  }

  return (
    <html lang={locale} className="h-full">
      <body
        className={`${jost.variable} ${kalam.variable} font-jost bg-background h-full antialiased`}
      >
        <SessionProvider>
          <NextIntlClientProvider>
            <div className="flex h-full flex-col">
              <Header />
              <div className="relative flex-1">
                <ApolloWrapper>{children}</ApolloWrapper>
              </div>
            </div>
          </NextIntlClientProvider>
        </SessionProvider>
      </body>
    </html>
  );
}
