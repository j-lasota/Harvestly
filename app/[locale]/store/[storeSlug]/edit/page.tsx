import { ContainerWrapper } from "@/components/layout/container-wrapper";

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

  const { storeSlug } = await params;

  console.log(storeSlug, userId);

  return (
    <ContainerWrapper
      comp="main"
      className="mt-10 mb-16 flex min-h-screen flex-col gap-8 md:mt-10"
    >
      <div>Edit</div>
    </ContainerWrapper>
  );
}
