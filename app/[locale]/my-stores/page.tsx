// import { getTranslations } from "next-intl/server";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";

import StoreGrid from "@/components/store-grid";
import { auth } from "@/auth";

const allMyStoresQuery = graphql(`
  query AllMyStores($userId: ID!) {
    userById(id: $userId) {
      stores {
        id
        name
        slug
      }
      favoriteStores {
        id
        name
        slug
      }
    }
  }
`);

// TODO: Replace mock data with real data
export default async function MyStoresPage() {
  const session = await auth();
  const userId = session?.user?.id;

  const { data } = await getClient().query({
    query: allMyStoresQuery,
    variables: { userId: userId! },
  });

  return (
    <ContainerWrapper
      comp="main"
      className="mt-10 mb-16 flex min-h-screen flex-col gap-16 md:mt-10"
    >
      {data.userById && data.userById.stores && (
        <StoreGrid
          title="Moje sklepy"
          stores={data.userById.stores.filter((s) => s !== null)}
        />
      )}

      {data.userById && data.userById.stores && (
        <StoreGrid
          title="Polubione sklepy"
          stores={data.userById.favoriteStores.filter((s) => s !== null)}
        />
      )}
    </ContainerWrapper>
  );
}
