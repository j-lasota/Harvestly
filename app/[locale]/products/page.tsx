import { getTranslations } from "next-intl/server";
import React from "react";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { ProductSection } from "@/components/product-section";
import { getClient } from "@/graphql/apollo-client";
import { gql } from "@apollo/client";

// Test query
// const createQuery = graphql(`
//   mutation createProduct($name: String!, $category: ProductCategory!) {
//     createProduct(name: $name, category: $category) {
//       id
//       name
//     }
//   }
// `);

const allProductsQuery = gql(
  `
    query AllProducts {
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
  `
);

export default async function ProductsPage() {
  // const { data: created } = await getClient().mutate({
  //   mutation: createQuery,
  //   variables: { name: "Pomidor", category: "FRUIT" },
  // });

  const t = await getTranslations("Products");

  const { data } = await getClient().query({
    query: allProductsQuery,
  });

  return (
    <ContainerWrapper
      comp="main"
      className="mt-10 mb-16 flex min-h-screen flex-col gap-8 md:mt-10"
    >
      <h1 className="text-2xl font-medium sm:text-3xl lg:text-4xl">
        {t("title")}
      </h1>

      {data.ownProducts && <ProductSection products={data.ownProducts} />}
    </ContainerWrapper>
  );
}
