// import { getTranslations } from "next-intl/server";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";

import { auth } from "@/auth";

// TODO: Replace mock data with real data
export default async function AddStorePage() {
  const session = await auth();
  const userId = session?.user?.id;

  return (
    <ContainerWrapper
      comp="main"
      className="mt-10 mb-16 flex min-h-screen flex-col gap-16 md:mt-10"
    ></ContainerWrapper>
  );
}
