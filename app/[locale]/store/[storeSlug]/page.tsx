import { BadgeCheck, Send, Pencil } from "lucide-react";
import { getTranslations } from "next-intl/server";
import { notFound } from "next/navigation";
import Image from "next/image";
import Link from "next/link";

import { storeBySlugQuery, userFavoriteStoresQuery } from "@/graphql/query";
import { OpinionCard, OpinionCardProps } from "./components/opinion-card";
import { ContainerWrapper } from "@/components/layout/container-wrapper";
import AddVerificationButton from "./components/add-verification-button";
import { ProductsSection } from "@/components/ui/products-section";
import { ImageMapPreview } from "./components/image-map-preview";
import { ReportStoreButton } from "./components/report-buttons";
import AddToFavButton from "./components/add-to-fav-button";
import { getClient } from "@/graphql/apollo-client";
import AddOpinion from "./components/add-opinion";
import { Button } from "@/components/ui/button";
import { getShortTime } from "@/lib/utils";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { auth } from "@/auth";

import messenger from "@/public/messenger.png";

import {
  getStoreStatistics,
  getStoreStatisticsForPeriod,
  getStoreAverageRating,
} from "@/utils/api";

interface BusinessHoursProps {
  dayOfWeek: string;
  openingTime: string;
  closingTime: string;
}

export default async function StorePage({
  params,
}: Readonly<{
  params: Promise<{
    storeSlug: string;
  }>;
}>) {
  const t = await getTranslations("page.store");
  const session = await auth();
  const userId = session?.user?.id;

  const { storeSlug } = await params;
  const { data } = await getClient().query({
    query: storeBySlugQuery,
    variables: { slug: storeSlug },
  });

  let UserData = null;
  if (session?.user) {
    const { data: userDataResponse } = await getClient().query({
      query: userFavoriteStoresQuery,
      variables: { id: userId! },
    });
    UserData = userDataResponse;
  }

  if (!data || !data.storeBySlug) return notFound();

  const isOwner = userId === data.storeBySlug.user.id;

  let overallRatio = null;
  let last7DaysRatio = null;
  let averageRating = null;

  if (isOwner) {
    overallRatio = await getStoreStatistics(storeSlug);
    last7DaysRatio = await getStoreStatisticsForPeriod(storeSlug, 7);
    averageRating = await getStoreAverageRating(storeSlug);
  }

  return (
    <ContainerWrapper
      comp="main"
      className="mt-10 mb-16 flex min-h-screen flex-col gap-8 md:mt-10"
    >
      <div className="grid gap-8 md:grid-cols-2">
        <div className="flex flex-col gap-1">
          <ImageMapPreview
            src={data.storeBySlug.imageUrl}
            name={data.storeBySlug.name}
            market={{
              lat: data.storeBySlug.latitude,
              lng: data.storeBySlug.longitude,
            }}
          />

          <Dialog>
            <DialogTrigger asChild>
              <Button
                variant="ghostPrimary"
                className="gap-1.5 self-end font-normal"
              >
                {t("contact.action")}
                <Send size={16} strokeWidth={1.75} />
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]">
              <DialogHeader>
                <DialogTitle>{t("contact.hero")}</DialogTitle>
                <div className="mt-4 flex flex-col gap-2">
                  {data.storeBySlug.user.email && (
                    <div className="flex justify-between">
                      <p className="font-medium">{t("contact.email")}:</p>
                      <a href={`mailto:${data.storeBySlug.user.email}`}>
                        {data.storeBySlug.user.email}
                      </a>
                    </div>
                  )}

                  {data.storeBySlug.user.phoneNumber && (
                    <div className="flex justify-between">
                      <p className="font-medium">{t("contact.phone")}:</p>
                      <a href={`tel:${data.storeBySlug.user.phoneNumber}`}>
                        {data.storeBySlug.user.phoneNumber}
                      </a>
                    </div>
                  )}

                  {data.storeBySlug.user.facebookNickname && (
                    <Button
                      className="mt-4 bg-white font-normal text-black hover:bg-gray-50"
                      size="lg"
                    >
                      <Image src={messenger} width="24" alt="Messenger" />
                      <a
                        href={`m.me/${data.storeBySlug.user.facebookNickname}`}
                      >
                        {t("contact.messengerAction")}
                      </a>
                    </Button>
                  )}
                </div>
              </DialogHeader>
            </DialogContent>
          </Dialog>
        </div>

        <div className="flex flex-col gap-2">
          <div className="flex items-center gap-2">
            <h1 className="flex items-center gap-2 text-2xl font-medium sm:text-3xl lg:text-4xl">
              {data.storeBySlug.name}
              {data.storeBySlug.verified && (
                <BadgeCheck
                  size={32}
                  strokeWidth={2}
                  className="text-primary"
                />
              )}
            </h1>
            {isOwner && (
              <Link href={`/store/${storeSlug}/slug`} className="ml-2">
                <Button
                  variant="ghost"
                  size="icon"
                  className="text-gray-600 hover:text-gray-900"
                >
                  <Pencil size={20} />
                </Button>
              </Link>
            )}
          </div>

          {session?.user && data.storeBySlug && (
            <div className="flex gap-2">
              <AddToFavButton
                storeId={data.storeBySlug.id}
                isFavorite={(UserData?.userById?.favoriteStores || []).some(
                  (store) => store.id && store.id === data.storeBySlug!.id
                )}
              />
              <AddVerificationButton
                storeId={data.storeBySlug.id}
                isVerifiedByUser={(data.storeBySlug?.verifications || []).some(
                  (verification) => verification.user.id === session.user?.id
                )}
              />
              <ReportStoreButton
                storeId={data.storeBySlug.id}
                isReportedByUser={(data.storeBySlug?.storeReports || []).some(
                  (storeReport) => storeReport.user.id === session.user?.id
                )}
              />
            </div>
          )}

          <p className="font-kalam mb-4 text-lg">
            {data.storeBySlug.description}
          </p>

          <div className="inline-flex gap-1">
            <h2 className="font-semibold">{t("location")}:</h2>
            {data.storeBySlug.address}, {data.storeBySlug.city}
          </div>

          {data.storeBySlug.businessHours &&
            data.storeBySlug.businessHours.length > 0 && (
              <div className="flex w-full max-w-max flex-col gap-1">
                <h2 className="font-semibold">{t("businessHours.title")}:</h2>
                {data.storeBySlug.businessHours.map(
                  (d: BusinessHoursProps) =>
                    d && (
                      <div
                        key={d.dayOfWeek}
                        className="flex justify-between gap-4"
                      >
                        <p className="font-medium">
                          {t(`businessHours.days.${d.dayOfWeek}`)}
                        </p>
                        <p className="text-foreground/80">
                          {getShortTime(d.openingTime)} -{" "}
                          {getShortTime(d.closingTime)}
                        </p>
                      </div>
                    )
                )}
              </div>
            )}

          {isOwner && (
            <div className="mt-6">
              <h2 className="mb-3 text-2xl font-semibold">
                {t("stats.title")}
              </h2>
              {overallRatio !== null ? (
                <p>
                  <strong>{t("stats.overallRatio")}:</strong>{" "}
                  {overallRatio.toFixed(2)}
                </p>
              ) : (
                <p>{t("stats.noOverallStats")}</p>
              )}
              {last7DaysRatio !== null ? (
                <p>
                  <strong>{t("stats.last7DaysRatio")}:</strong>{" "}
                  {last7DaysRatio.toFixed(2)}
                </p>
              ) : (
                <p>{t("stats.no7DayStats")}</p>
              )}
              {averageRating !== null ? (
                <p>
                  <strong>{t("stats.averageRating")}:</strong>{" "}
                  {averageRating.toFixed(2)}
                </p>
              ) : (
                <p>{t("stats.noAverageRating")}</p>
              )}
            </div>
          )}
        </div>
      </div>

      {data.storeBySlug.ownProducts && (
        <ProductsSection products={data.storeBySlug.ownProducts} />
      )}

      <section className="flex max-w-3xl flex-col gap-4">
        <h3 className="mt-4 text-2xl font-semibold">{t("reviews.title")}</h3>
        {session?.user && <AddOpinion storeId={data.storeBySlug.id} />}
        {data.storeBySlug.opinions && data.storeBySlug.opinions.length > 0 ? (
          data.storeBySlug.opinions.map((opinion: OpinionCardProps) => (
            <OpinionCard key={opinion.id} {...opinion} />
          ))
        ) : (
          <p>{t("reviews.noReviews")}</p>
        )}
      </section>
    </ContainerWrapper>
  );
}
