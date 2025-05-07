import { notFound } from "next/navigation";
import { BadgeCheck } from "lucide-react";
import React from "react";

import { ProductCard, productCardFragment } from "@/components/product-card";
import { OpinionCard, opinionCardFragment } from "@/components/opinion-card";
import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { ImageMapPreview } from "./components/image-map-preview";
import AddToFavButton from "./components/add-to-fav-button";
import { getClient } from "@/graphql/apollo-client";
import AddOpinion from "./components/add-opinion";
import { graphql } from "@/graphql";
import { auth } from "@/auth";

const shopBySlugQuery = graphql(
  `
    query shopBySlug($slug: String!) {
      shopBySlug(slug: $slug) {
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
          ...OpinionCard
        }
        businessHours {
          dayOfWeek
          openingTime
          closingTime
        }
        ownProducts {
          id
          ...ProductCard
        }
      }
    }
  `,
  [productCardFragment, opinionCardFragment]
);

export default async function ShopPage({
  params,
}: Readonly<{
  params: Promise<{
    shopSlug: string;
  }>;
}>) {
  const session = await auth();
  const { shopSlug } = await params;
  const { data } = await getClient().query({
    query: shopBySlugQuery,
    variables: { slug: shopSlug },
  });

  if (!data || !data.shopBySlug) return notFound();

  return (
    <ContainerWrapper
      comp="main"
      className="mt-10 mb-16 flex min-h-screen flex-col gap-8 md:mt-10"
    >
      <div className="grid gap-8 md:grid-cols-2">
        <ImageMapPreview
          src={data.shopBySlug.imageUrl}
          name={data.shopBySlug.name}
          market={{
            lat: data.shopBySlug.latitude,
            lng: data.shopBySlug.longitude,
          }}
        />

        <div className="flex flex-col gap-2">
          <h1 className="mt-2 flex items-center gap-2 text-2xl font-medium sm:text-3xl lg:text-4xl">
            {data.shopBySlug.name}
            {data.shopBySlug.verified && (
              <BadgeCheck size={32} strokeWidth={2} className="text-primary" />
            )}
          </h1>
          <AddToFavButton shopId={data.shopBySlug.id} />
          <p className="font-kalam mb-4 text-lg">
            {data.shopBySlug.description}
          </p>

          <div className="inline-flex gap-1">
            <h2 className="font-semibold">Lokalizacja:</h2>
            {data.shopBySlug.address}, {data.shopBySlug.city}
          </div>

          {data.shopBySlug.businessHours && (
            <div className="flex flex-col gap-1">
              <h2 className="font-semibold">Godziny pracy:</h2>
              {data.shopBySlug.businessHours.map(
                (d) =>
                  d && (
                    <p key={d.dayOfWeek}>
                      <span className="capitalize">{d.dayOfWeek}</span>:{" "}
                      {d.openingTime} - {d.closingTime}
                    </p>
                  )
              )}
            </div>
          )}
        </div>
      </div>

      {/* // TODO: Add pagination */}
      <section className="grid grid-cols-1 gap-10 md:grid-cols-2">
        {data.shopBySlug.ownProducts &&
          data.shopBySlug.ownProducts.map(
            (product) =>
              product && (
                <ProductCard key={product.id} data={product}></ProductCard>
              )
          )}
      </section>

      <section className="flex max-w-3xl flex-col gap-4">
        <h3 className="mt-4 text-2xl font-semibold">Opinie:</h3>
        {session?.user && <AddOpinion slug={shopSlug} />}
        {data.shopBySlug.opinions ? (
          data.shopBySlug.opinions.map((opinion) => (
            <OpinionCard key={opinion.id} data={opinion}></OpinionCard>
          ))
        ) : (
          <p>Brak opinii</p>
        )}
      </section>
    </ContainerWrapper>
  );
}
