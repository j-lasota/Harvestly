import { getTranslations } from "next-intl/server";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { ProductSection } from "@/components/product-section";
import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";

const allProductsQuery = graphql(`
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
        city
      }
    }
  }
`);

const allCategoriesQuery = graphql(`
  query Category {
    products {
      name
      category
    }
  }
`);

export default async function ProductsPage() {
  const t = await getTranslations("products");

  const { data: products } = await getClient().query({
    query: allProductsQuery,
  });
  const { data: categories } = await getClient().query({
    query: allCategoriesQuery,
  });

  return (
    <ContainerWrapper
      comp="main"
      className="mt-10 mb-16 flex min-h-screen flex-col gap-4 md:mt-10"
    >
      <h1 className="text-2xl font-medium sm:text-3xl lg:text-4xl">
        {t("title")}
      </h1>

      {products.ownProducts && categories.products && (
        <ProductSection
          products={products.ownProducts.filter((p) => p !== null)}
          categories={categories.products.filter((p) => p !== null)}
        />
      )}
    </ContainerWrapper>
  );
}
