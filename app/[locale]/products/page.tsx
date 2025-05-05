import React from "react";

import { ProductCard, productCardFragment } from "@/components/product-card";
import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";

// Test query
// const createQuery = graphql(`
//   mutation createProduct($name: String!, $category: ProductCategory!) {
//     createProduct(name: $name, category: $category) {
//       id
//       name
//     }
//   }
// `);

const allProductsQuery = graphql(
  `
    query AllProducts {
      ownProducts {
        id
        ...ProductCard
      }
    }
  `,
  [productCardFragment]
);

export default async function ProductsPage() {
  // const { data: created } = await getClient().mutate({
  //   mutation: createQuery,
  //   variables: { name: "Pomidor", category: "FRUIT" },
  // });

  const { data } = await getClient().query({
    query: allProductsQuery,
  });

  return (
    <ContainerWrapper
      comp="main"
      className="mt-10 flex min-h-screen flex-col gap-8 md:mt-10"
    >
      <h1 className="text-2xl font-medium sm:text-3xl lg:text-4xl">
        Dostępne produkty
      </h1>

      <section className="grid grid-cols-1 gap-10 md:grid-cols-2">
        {data.ownProducts &&
          data.ownProducts.map(
            (product) =>
              product && (
                <ProductCard key={product.id} data={product}></ProductCard>
              )
          )}
      </section>
    </ContainerWrapper>
  );
}
