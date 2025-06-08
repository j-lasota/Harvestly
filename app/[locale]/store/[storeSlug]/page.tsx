import { getTranslations } from "next-intl/server";
import { BadgeCheck, Send } from "lucide-react";
import { notFound } from "next/navigation";

import { storeBySlugQuery, userFavoriteStoresQuery } from "@/graphql/query";
import { OpinionCard, OpinionCardProps } from "./components/opinion-card";
import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { ProductsSection } from "@/components/ui/products-section";
import AddVerificationButton from "./components/add-verification";
import { ImageMapPreview } from "./components/image-map-preview";
import AddToFavButton from "./components/add-to-fav-button";
import { getClient } from "@/graphql/apollo-client";
import AddOpinion from "./components/add-opinion";
import { Button } from "@/components/ui/button";
import { auth } from "@/auth";

interface BusinessHoursProps {
  dayOfWeek: string;
  openingTime: string;
  closingTime: string;
}

export default async function StorePage({
  params,
}: Readonly<{
  params: Promise<{
    storeSlug: string;
  }>;
}>) {
  const t = await getTranslations("page.store");
  const session = await auth();
  const userId = session?.user?.id;

  const { storeSlug } = await params;
  const { data } = await getClient().query({
    query: storeBySlugQuery,
    variables: { slug: storeSlug },
  });

  let UserData = null;
  if (session?.user) {
    const { data } = await getClient().query({
      query: userFavoriteStoresQuery,
      variables: { id: userId! },
    });
    UserData = data;
  }

  if (!data || !data.storeBySlug) return notFound();

  return (
    <ContainerWrapper
      comp="main"
      className="mt-10 mb-16 flex min-h-screen flex-col gap-8 md:mt-10"
    >
      <div className="grid gap-8 md:grid-cols-2">
        <div className="flex flex-col gap-1">
          <ImageMapPreview
            src={data.storeBySlug.imageUrl}
            name={data.storeBySlug.name}
            market={{
              lat: data.storeBySlug.latitude,
              lng: data.storeBySlug.longitude,
            }}
          />
          <Button
            variant="ghostPrimary"
            asChild
            className="gap-1.5 self-end font-normal"
          >
            <a href={`mailto:${data.storeBySlug.user.email}`}>
              {t("store.contact")}
              <Send size={16} strokeWidth={1.75} />
            </a>
          </Button>
        </div>

        <div className="flex flex-col gap-2">
          <h1 className="mt-2 flex items-center gap-2 text-2xl font-medium sm:text-3xl lg:text-4xl">
            {data.storeBySlug.name}
            {data.storeBySlug.verified && (
              <BadgeCheck size={32} strokeWidth={2} className="text-primary" />
            )}
          </h1>

          {session?.user && data.storeBySlug && (
            <div className="flex gap-2">
              <AddToFavButton
                storeId={data.storeBySlug.id}
                isFavorite={(UserData?.userById?.favoriteStores || []).some(
                  (store) => store.id && store.id === data.storeBySlug!.id
                )}
              />
              <AddVerificationButton
                storeId={data.storeBySlug.id}
                isVerifiedByUser={(data.storeBySlug?.verifications || []).some(
                  (verification) => verification.user.id === session.user?.id
                )}
              />
            </div>
          )}

          <p className="font-kalam mb-4 text-lg">
            {data.storeBySlug.description}
          </p>

          <div className="inline-flex gap-1">
            <h2 className="font-semibold">{t("location")}:</h2>
            {data.storeBySlug.address}, {data.storeBySlug.city}
          </div>

          {data.storeBySlug.businessHours && (
            <div className="flex w-full max-w-max flex-col gap-1">
              <h2 className="font-semibold">{t("businessHours")}:</h2>
              {data.storeBySlug.businessHours.map(
                (d: BusinessHoursProps) =>
                  d && (
                    <div
                      key={d.dayOfWeek}
                      className="flex justify-between gap-4"
                    >
                      <p>{t(`days.${d.dayOfWeek}`)}</p>
                      <p className="text-black/60">
                        {d.openingTime} - {d.closingTime}
                      </p>
                    </div>
                  )
              )}
            </div>
          )}
        </div>
      </div>

      {data.storeBySlug.ownProducts && (
        <ProductsSection products={data.storeBySlug.ownProducts} />
      )}

      <section className="flex max-w-3xl flex-col gap-4">
        <h3 className="mt-4 text-2xl font-semibold">{t("reviews.title")}</h3>
        {session?.user && <AddOpinion storeId={data.storeBySlug.id} />}
        {data.storeBySlug.opinions && data.storeBySlug.opinions.length > 0 ? (
          data.storeBySlug.opinions.map((opinion: OpinionCardProps) => (
            <OpinionCard key={opinion.id} {...opinion} />
          ))
        ) : (
          <p>{t("reviews.noReviews")}</p>
        )}
      </section>
    </ContainerWrapper>
  );
}
