import { getTranslations } from "next-intl/server";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { allCategoriesQuery, allProductsQuery } from "@/graphql/query";
import { ProductsSection } from "@/components/ui/products-section";
import { getClient } from "@/graphql/apollo-client";

export default async function ProductsPage() {
  const t = await getTranslations("page.products");

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
        <ProductsSection
          products={products.ownProducts.filter((p) => p !== null)}
          categories={categories.products.filter((p) => p !== null)}
          withAveragePrice
        />
      )}
    </ContainerWrapper>
  );
}
