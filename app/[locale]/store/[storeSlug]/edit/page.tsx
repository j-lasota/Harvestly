import EditStoreClientPage from "./components/client-page";
import { allCategoriesQuery } from "@/graphql/query";
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

  const { data: productsData } = await getClient().query({
    query: allCategoriesQuery,
  });

  const { storeSlug } = await params;

  console.log(storeSlug, userId);

  return (
    <EditStoreClientPage
      products={(productsData.products ?? []).filter(
        (
          p
        ): p is { id: string; name: string; category: "FRUIT" | "VEGETABLE" } =>
          !!p
      )}
    />
  );
}
