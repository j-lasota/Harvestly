import { notFound } from "next/navigation";
import Image from "next/image";
import React from "react";

import { ProductCard, productCardFragment } from "@/components/product-card";
import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { getClient } from "@/graphql/apollo-client";
import placeholder from "@/public/placeholder.jpeg";
import { graphql } from "@/graphql";
import { BadgeCheck } from "lucide-react";

// TODO: Replace to slug - add to backend
const shopBySlugQuery = graphql(
  `
    query shopBySlug($id: ID!) {
      shopById(id: $id) {
        id
        name
        city
        address
        imageUrl
        description
        verified
        opinions {
          id
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
  [productCardFragment]
);

export default async function ShopPage({
  params,
}: Readonly<{
  params: Promise<{
    shopSlug: string;
  }>;
}>) {
  const { shopSlug } = await params;
  const { data } = await getClient().query({
    query: shopBySlugQuery,
    variables: { id: shopSlug },
  });

  if (!data || !data.shopById) return notFound();

  return (
    <ContainerWrapper
      comp="main"
      className="mt-10 mb-16 flex min-h-screen flex-col gap-8 md:mt-10"
    >
      <div className="grid gap-4 md:grid-cols-3">
        <Image
          src={data.shopById.imageUrl ?? placeholder}
          alt={`Image of ${data.shopById.name}`}
          className="aspect-video w-full rounded-xl object-cover"
        />
        <div className="flex flex-col gap-2 md:col-span-2">
          <h1 className="mt-2 flex items-center gap-2 text-2xl font-medium sm:text-3xl lg:text-4xl">
            {data.shopById.name}
            {data.shopById.verified && (
              <BadgeCheck size={32} strokeWidth={2} className="text-primary" />
            )}
          </h1>
          <p className="font-kalam mb-4 text-lg">{data.shopById.description}</p>

          <div className="inline-flex gap-1">
            <h2 className="font-semibold">Lokalizacja:</h2>
            {data.shopById.address}, {data.shopById.city}
          </div>

          {data.shopById.businessHours && (
            <div className="flex flex-col gap-1">
              <h2 className="font-semibold">Godziny pracy:</h2>
              {data.shopById.businessHours.map(
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
        {data.shopById.ownProducts &&
          data.shopById.ownProducts.map(
            (product) =>
              product && (
                <ProductCard key={product.id} data={product}></ProductCard>
              )
          )}
      </section>

      <section>
        <h3>Opinie:</h3>
        {/* // TODO: Add opinions */}
      </section>
    </ContainerWrapper>
  );
}
