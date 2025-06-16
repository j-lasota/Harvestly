"use client";

import { useSession, signIn } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { useQuery } from "@apollo/client";
import { Frown } from "lucide-react";
import { useTranslations } from 'next-intl'; 

import {
  allReportedShopsQuery,
  allReportedOpinionsQuery,
} from "@/graphql/query";

import ReportedShopsList from "./components/reported-stores";
import ReportedOpinionsList from "./components/reported-opinions";

const ADMIN_PERMISSION = "manage:all";

export default function AdminPage() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const t = useTranslations('page.admin'); 

  const {
    data: shopsData,
    loading: shopsLoading,
    error: shopsError,
    refetch: refetchShops,
  } = useQuery(allReportedShopsQuery, {
    skip: status !== "authenticated" || !session?.user?.permissions?.includes(ADMIN_PERMISSION),
    fetchPolicy: 'network-only',
  });

  const {
    data: opinionsData,
    loading: opinionsLoading,
    error: opinionsError,
    refetch: refetchOpinions,
  } = useQuery(allReportedOpinionsQuery, {
    skip: status !== "authenticated" || !session?.user?.permissions?.includes(ADMIN_PERMISSION),
    fetchPolicy: 'network-only',
  });

  useEffect(() => {
    if (status === "loading") {
      return;
    }

    if (!session) {
      signIn(); 
      return;
    }

    const userPermissions = session.user?.permissions || [];
    const isAdmin = userPermissions.includes(ADMIN_PERMISSION);

    if (!isAdmin) {
      router.push("/"); 
      return;
    }
  }, [session, status, router]);


  if (status === "loading") {
    return (
      <div className="bg-background-base text-foreground flex min-h-screen items-center justify-center text-xl">
        {t('loadingPage')}
      </div>
    );
  }

  if (!session || !session.user?.permissions?.includes(ADMIN_PERMISSION)) {
    return null;
  }

  if (shopsLoading || opinionsLoading) {
    return (
      <div className="bg-background-base text-foreground flex min-h-screen items-center justify-center text-xl">
        {t('loadingData')}
      </div>
    );
  }
  if (shopsError || opinionsError) {
    return (
      <div className="m-4 flex min-h-screen flex-col items-center justify-center rounded-lg bg-red-100 p-4 text-red-800 shadow-md dark:bg-red-900 dark:text-red-200">
        <Frown className="mb-4 h-16 w-16 text-red-600 dark:text-red-300" />
        <p className="mb-2 text-2xl font-bold">
          {t('errorTitle')}
        </p>
        {shopsError && (
          <p className="text-lg">{t('shopsError', { errorMessage: shopsError.message })}</p>
        )}
        {opinionsError && (
          <p className="text-lg">{t('opinionsError', { errorMessage: opinionsError.message })}</p>
        )}
        <p className="mt-4 text-sm">
          {t('checkConnection')}
        </p>
      </div>
    );
  }

  const reportedShops = shopsData?.storesReported || [];
  const reportedOpinions = opinionsData?.opinionsReported || [];

  return (
    <div className="bg-background-base text-foreground w-full pb-8">
      <div className="pt-8 pb-4 text-center">
        <h1 className="text-primary mb-4 text-5xl font-extrabold">
          {t('panelTitle')}
        </h1>
        <p className="text-foreground text-xl">
          {t.rich('welcomeMessage', {
            userName: session.user?.name ?? session.user?.email ?? "",
            strong: (chunks) => <span className="text-primary font-kalam font-semibold">{chunks}</span>
          })}
        </p>
      </div>

      <div className="mx-auto w-full max-w-6xl rounded-2xl p-8 shadow-xl">
        <div className="grid grid-cols-1 gap-10">
          <ReportedShopsList shops={reportedShops} refetchShops={refetchShops} />
          <ReportedOpinionsList opinions={reportedOpinions} refetchOpinions={refetchOpinions} />
        </div>
      </div>
    </div>
  );
}