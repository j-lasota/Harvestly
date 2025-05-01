import { getTranslations } from "next-intl/server";
import { redirect } from "next/navigation";
import Image from "next/image";
import React from "react";

import { SignIn } from "@/components/auth/signin-button";
import loginImg from "@/public/login-img.png";
import { auth } from "@/auth";

export default async function SignInPage() {
  const session = await auth();
  const t = await getTranslations("Auth");

  if (session) {
    return redirect("/");
  }

  return (
    <main className="grid h-[calc(100vh-5rem)] flex-1 lg:grid-cols-2">
      <section className="bg-accent hidden flex-col lg:flex">
        <div className="flex flex-1 flex-col items-center justify-center gap-2">
          <h1 className="text-4xl font-medium">{t("join")}</h1>
          <p className="text-3xl">
            {t("enjoy")}{" "}
            <span className="font-kalam text-primary font-bold">
              {t("healthy")}
            </span>{" "}
            {t("food")}
          </p>
        </div>
        <Image src={loginImg} alt="Login image" className="w-full" />
      </section>
      <section className="flex flex-col items-center justify-center gap-8">
        <p className="text-4xl font-medium md:text-5xl">{t("login")}</p>
        <SignIn />
      </section>
    </main>
  );
}
