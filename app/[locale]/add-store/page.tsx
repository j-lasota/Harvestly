import { notFound, redirect } from "next/navigation";
import { auth } from "@/auth";

import AddStoreClientPage from "./components/client-page";
import { hasUserStoresQuery } from "@/graphql/query";
import { getClient } from "@/graphql/apollo-client";

const TIER = [1, 5];

export default async function AddStorePage() {
  const session = await auth();
  const userId = session?.user?.id;

  if (!userId) return notFound();

  const { data } = await getClient().query({
    query: hasUserStoresQuery,
    variables: { userId },
  });

  if (
    data.userById &&
    data.userById.stores &&
    data.userById.tier &&
    data.userById.stores.length > TIER[data.userById.tier]
  )
    redirect("/my-stores");

  return <AddStoreClientPage />;
}
