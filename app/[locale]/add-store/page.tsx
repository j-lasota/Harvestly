import { notFound, redirect } from "next/navigation";
import { auth } from "@/auth";

import AddStoreClientPage from "./components/client-page";
import { hasUserStoresQuery } from "@/graphql/query";
import { allCategoriesQuery } from "@/graphql/query";
import { getClient } from "@/graphql/apollo-client";

const TIER = [1, 3];

export default async function AddStorePage() {
  const session = await auth();
  const userId = session?.user?.id;

  if (!userId) return notFound();

  const { data } = await getClient().query({
    query: hasUserStoresQuery,
    variables: { userId },
  });

  const { data: productsData } = await getClient().query({
    query: allCategoriesQuery,
  });

  const user = data?.userById;

  const hasMaxStores =
    user?.tier !== null &&
    user?.stores &&
    user?.stores.length >= TIER[user.tier];

  if (hasMaxStores) {
    redirect("/my-stores");
  }

  return (
    <AddStoreClientPage
      products={(productsData.products ?? []).filter(
        (
          p
        ): p is { id: string; name: string; category: "FRUIT" | "VEGETABLE" } =>
          !!p
      )}
    />
  );
}
