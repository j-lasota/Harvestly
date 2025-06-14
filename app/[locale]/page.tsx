import { getTranslations } from "next-intl/server";
import Image from "next/image";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { Button } from "@/components/ui/button";
import { Link } from "@/i18n/navigation";
import hero from "@/public/hero.jpg";

export default async function Home() {
  const t = await getTranslations("page.home");

  return (
    <ContainerWrapper comp="main" className="flex flex-col gap-16">
      <section className="grid items-center gap-8 md:grid-cols-2">
        <div className="flex flex-col gap-4 md:max-w-md">
          <h1 className="text-4xl font-medium sm:text-5xl lg:text-6xl">
            Harvestly
          </h1>
          <p className="text-xl sm:text-2xl lg:text-3xl">
            {t.rich("hero", {
              s: (chunks) => (
                <span className="font-kalam text-primary font-bold">
                  {chunks}
                </span>
              ),
            })}
          </p>

          <Button asChild size="xl" className="mt-5 max-w-xs">
            <Link href="/products">{t("action")}</Link>
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
