import { getTranslations } from "next-intl/server";
import Image from "next/image";
import Link from "next/link";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { Button } from "@/components/ui/button";
import hero from "@/public/hero.jpg";

export default async function Home() {
  const t = await getTranslations("home");

  return (
    <ContainerWrapper
      comp="main"
      className="mt-12 flex min-h-screen flex-col gap-16 md:mt-20"
    >
      <section className="grid items-center gap-8 md:grid-cols-2">
        <div className="flex flex-col gap-4 md:max-w-md">
          <h1 className="text-4xl font-medium sm:text-5xl lg:text-6xl">
            Harvestly
          </h1>
          <p className="text-xl sm:text-2xl lg:text-3xl">
            <span className="font-kalam text-primary font-bold">
              {t("health")}
            </span>{" "}
            {t("starts")}{" "}
            <span className="font-kalam text-primary font-bold">
              {t("locally")}
            </span>{" "}
            - {t("choose")}{" "}
            <span className="font-kalam text-primary font-bold">
              {t("fresh")}
            </span>{" "}
            {t("fromFarmers")}
          </p>

          <Button size="xl" className="mt-5 max-w-xs" asChild>
            <Link href="/products">{t("button")}</Link>
          </Button>
        </div>
        <Image
          src={hero}
          alt="Logo"
          className="w-full max-w-lg justify-self-end rounded-3xl drop-shadow-lg"
        />
      </section>
    </ContainerWrapper>
  );
}
