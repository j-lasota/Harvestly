import { notFound } from "next/navigation";

import { allCategoriesQuery, storeBySlugEditQuery } from "@/graphql/query";
import EditStoreClientPage from "./components/client-page";
import { getClient } from "@/graphql/apollo-client";
import { auth } from "@/auth";

export default async function EditStorePage({
  params,
}: Readonly<{
  params: Promise<{
    storeSlug: string;
  }>;
}>) {
  const session = await auth();
  const userId = session?.user?.id;

  if (!userId) return notFound();

  const { data: productsData } = await getClient().query({
    query: allCategoriesQuery,
  });

  const { storeSlug } = await params;
  const { data: storeData } = await getClient().query({
    query: storeBySlugEditQuery,
    variables: { slug: storeSlug },
  });

  if (!storeData.storeBySlug) return notFound();

  return (
    <EditStoreClientPage
      store={storeData.storeBySlug}
      products={(productsData.products ?? []).filter(
        (
          p
        ): p is { id: string; name: string; category: "FRUIT" | "VEGETABLE" } =>
          !!p
      )}
    />
  );
}
