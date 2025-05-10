import { getTranslations } from "next-intl/server";
import { notFound } from "next/navigation";
import { BadgeCheck } from "lucide-react";
import React from "react";

import { OpinionCard, OpinionCardProps } from "@/components/opinion-card";
import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { ImageMapPreview } from "./components/image-map-preview";
import { ProductSection } from "@/components/product-section";
import AddToFavButton from "./components/add-to-fav-button";
import { getClient } from "@/graphql/apollo-client";
import AddOpinion from "./components/add-opinion";
import { gql } from "@apollo/client";
import { auth } from "@/auth";

const storeBySlugQuery = gql(
  `
    query storeBySlug($slug: String!) {
      storeBySlug(slug: $slug) {
        id
        name
        city
        address
        latitude
        longitude
        imageUrl
        description
        verified
        opinions {
          id
          description
          stars
          user {
            firstName
          }
        }
        businessHours {
          dayOfWeek
          openingTime
          closingTime
        }
        ownProducts {
          id
          product {
            name
          }
          price
          quantity
          imageUrl
          store {
            slug
            name
          }
        }
      }
    }
  `
);

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
  const session = await auth();
  const { storeSlug } = await params;
  const { data } = await getClient().query({
    query: storeBySlugQuery,
    variables: { slug: storeSlug },
  });
  const t = await getTranslations("Store");

  if (!data || !data.storeBySlug) return notFound();

  return (
    <ContainerWrapper
      comp="main"
      className="mt-10 mb-16 flex min-h-screen flex-col gap-8 md:mt-10"
    >
      <div className="grid gap-8 md:grid-cols-2">
        <ImageMapPreview
          src={data.storeBySlug.imageUrl}
          name={data.storeBySlug.name}
          market={{
            lat: data.storeBySlug.latitude,
            lng: data.storeBySlug.longitude,
          }}
        />

        <div className="flex flex-col gap-2">
          <h1 className="mt-2 flex items-center gap-2 text-2xl font-medium sm:text-3xl lg:text-4xl">
            {data.storeBySlug.name}
            {data.storeBySlug.verified && (
              <BadgeCheck size={32} strokeWidth={2} className="text-primary" />
            )}
          </h1>
          <AddToFavButton storeId={data.storeBySlug.id} />
          <p className="font-kalam mb-4 text-lg">
            {data.storeBySlug.description}
          </p>

          <div className="inline-flex gap-1">
            <h2 className="font-semibold">{t("location")}:</h2>
            {data.storeBySlug.address}, {data.storeBySlug.city}
          </div>

          {data.storeBySlug.businessHours && (
            <div className="flex w-full max-w-max flex-col gap-1">
              <h2 className="font-semibold">{t("businesshours")}:</h2>
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

      {/* // TODO: Add pagination */}
      <ProductSection products={data.storeBySlug.ownProducts} />

      <section className="flex max-w-3xl flex-col gap-4">
        <h3 className="mt-4 text-2xl font-semibold">Opinie:</h3>
        {session?.user && <AddOpinion slug={storeSlug} />}
        {data.storeBySlug.opinions ? (
          data.storeBySlug.opinions.map((opinion: OpinionCardProps) => (
            <OpinionCard key={opinion.id} {...opinion}></OpinionCard>
          ))
        ) : (
          <p>Brak opinii</p>
        )}
      </section>
    </ContainerWrapper>
  );
}
