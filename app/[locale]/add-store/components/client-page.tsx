"use client";

import { useActionState, useEffect, useMemo, useState } from "react";
import { useTranslations } from "next-intl";
import { Loader } from "lucide-react";
import dynamic from "next/dynamic";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import storePlaceholder from "@/public/store_placeholder.jpg";
import { SubmitButton } from "@/components/ui/submit-button";
import { addStoreAction, getAddress } from "../actions";
import ImageUploader from "@/components/image-uploader";
import { Input } from "@/components/ui/input";

const DAYS = [
  "MONDAY",
  "TUESDAY",
  "WEDNESDAY",
  "THURSDAY",
  "FRIDAY",
  "SATURDAY",
  "SUNDAY",
];

export default function AddStoreClientPage() {
  const t = useTranslations("");
  const [state, action] = useActionState(addStoreAction, undefined);
  const [image, setImage] = useState<string>("");
  const [address, setAddress] = useState<{
    city: string;
    details: string;
  } | null>(null);
  const [coords, setCoords] = useState<{ lat: number; lng: number } | null>(
    null
  );
  const [moreDetails, setMoreDetails] = useState(false);

  useEffect(() => {
    if (state?.success) setMoreDetails(true);
  }, [state]);

  const InteractiveMap = useMemo(
    () =>
      dynamic(() => import("./map-selector"), {
        loading: () => (
          <>
            <Loader />
          </>
        ),
        ssr: false,
      }),
    []
  );

  const handleCoordsChange = async (coords: { lat: number; lng: number }) => {
    const address = await getAddress(coords.lat, coords.lng);
    setAddress(address);
  };

  useEffect(() => {
    if (coords) handleCoordsChange(coords);
  }, [coords]);

  return (
    <ContainerWrapper comp="main" className="flex items-center justify-center">
      {!moreDetails ? (
        <form
          className="grid w-full max-w-6xl items-center justify-center gap-4 lg:grid-cols-3"
          action={action}
        >
          <div className="flex flex-col gap-4">
            <h1 className="text-4xl font-semibold">
              {t("page.addStore.title")}
            </h1>
            <ImageUploader
              placeholder={storePlaceholder}
              onUploaded={setImage}
            />
          </div>

          <div className="scrollbar-hidden h-[calc(100vh-8rem)] overflow-y-scroll lg:col-span-2">
            <div className="mr-4 ml-auto flex max-w-lg flex-col gap-4">
              <input
                name="image_url"
                type="hidden"
                value={image}
                readOnly
                className="hidden"
              />

              <label
                htmlFor="name"
                className="flex flex-col gap-1 text-sm font-medium"
              >
                {t("page.addStore.input.name")}
                <Input
                  id="name"
                  name="name"
                  type="text"
                  className="w-full rounded border px-2 py-1 text-sm"
                  placeholder={t("page.addStore.placeholder.name")}
                />
              </label>

              <div>
                <p className="text-sm font-medium">
                  {t("page.addStore.input.location")}
                </p>
                <div className="flex h-80 w-full items-center justify-center">
                  <InteractiveMap onChange={setCoords} />
                </div>

                <input
                  name="latitude"
                  type="hidden"
                  defaultValue={coords?.lat.toString()}
                  readOnly={true}
                  className="hidden"
                />

                <input
                  name="longitude"
                  type="hidden"
                  defaultValue={coords?.lng.toString()}
                  readOnly={true}
                  className="hidden"
                />
              </div>

              <label
                htmlFor="address"
                className="flex flex-col gap-1 text-sm font-medium"
              >
                {t("page.addStore.input.address")}
                <Input
                  id="address"
                  name="address"
                  type="text"
                  className="w-full rounded border px-2 py-1 text-sm"
                  placeholder={t("page.addStore.placeholder.address")}
                  defaultValue={address?.details || ""}
                  readOnly={true}
                />
              </label>

              <label
                htmlFor="city"
                className="flex flex-col gap-1 text-sm font-medium"
              >
                {t("page.addStore.input.city")}
                <Input
                  id="city"
                  name="city"
                  type="text"
                  className="w-full rounded border px-2 py-1 text-sm"
                  placeholder={t("page.addStore.placeholder.city")}
                  defaultValue={address?.city || ""}
                  readOnly={true}
                />
              </label>

              <label
                htmlFor="description"
                className="flex flex-col gap-1 text-sm font-medium"
              >
                {t("page.addStore.input.description")}
                <textarea
                  id="description"
                  name="description"
                  rows={4}
                  className="border-shadow file:text-foreground placeholder:text-foreground/25 focus-visible:ring-shadow flex w-full resize-none rounded-md border bg-transparent px-3 py-1 text-base shadow-sm transition-colors outline-none file:border-0 file:bg-transparent file:text-sm file:font-medium focus-visible:ring-1 focus-visible:outline-none disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
                  placeholder={t("page.addStore.placeholder.description")}
                />
              </label>

              <div className="mt-2 w-full">
                {!state?.success && (
                  <SubmitButton
                    label={t("page.addStore.action.submit")}
                    pendingLabel={t("page.addStore.action.pending")}
                    className="w-full"
                  />
                )}
                {state?.message && (
                  <p role="alert" className="text-primary w-full text-center">
                    {state.message}
                  </p>
                )}
              </div>
            </div>
          </div>
        </form>
      ) : (
        <form
          className="grid w-full max-w-6xl items-center justify-center gap-4 lg:grid-cols-3"
          action={action}
        >
          <div>
            <p className="mb-1 text-sm font-medium">
              {t("page.addStore.input.businessHours")}
            </p>

            {DAYS.map((day) => (
              <div
                className="flex items-center justify-between gap-2"
                key={day}
              >
                <p className="flex items-center gap-2 font-normal">
                  {t(`days.${day}`)}:
                </p>
                <div className="flex items-center gap-2">
                  <Input
                    name={`open_${day}`}
                    type="time"
                    className="w-full rounded border px-2 py-1 text-sm"
                  />
                  <Input
                    name={`close_${day}`}
                    type="time"
                    className="w-full rounded border px-2 py-1 text-sm"
                  />
                </div>
              </div>
            ))}
          </div>
        </form>
      )}
    </ContainerWrapper>
  );
}
