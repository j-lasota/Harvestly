import { notFound } from "next/navigation";
import { auth } from "@/auth";

import ProfileClientPage from "./components/client-page";
import { getClient } from "@/graphql/apollo-client";
import { userByIdQuery } from "@/graphql/query";

export default async function ProfilePage() {
  const session = await auth();
  const userId = session?.user?.id;

  if (!userId) return notFound();

  const { data } = await getClient().query({
    query: userByIdQuery,
    variables: { id: userId },
  });

  if (!data.userById) return notFound();

  return <ProfileClientPage data={data.userById} />;
}
