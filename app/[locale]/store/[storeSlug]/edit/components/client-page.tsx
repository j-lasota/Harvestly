"use client";

import { useActionState, useEffect, useState } from "react";
import { useTranslations } from "next-intl";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import storePlaceholder from "@/public/store_placeholder.jpg";
import { SubmitButton } from "@/components/ui/submit-button";
// import { addStoreAction, getAddress } from "../actions";
import ImageUploader from "@/components/image-uploader";
import AddProductList from "@/components/add-product";
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

export default function EditStoreClientPage({
  products,
}: {
  products: { id: string; name: string; category: "FRUIT" | "VEGETABLE" }[];
}) {
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

  useEffect(() => {
    if (coords) handleCoordsChange(coords);
  }, [coords]);

  return (
    <ContainerWrapper comp="main" className="flex items-center justify-center">
      <form
        className="grid w-full max-w-6xl items-center justify-center gap-4 lg:grid-cols-3"
        action={action}
      >
        <div className="flex flex-col gap-4">
          <h1 className="text-4xl font-semibold">
            {t("page.editStore.title")}
          </h1>
          <ImageUploader placeholder={storePlaceholder} onUploaded={setImage} />
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
              htmlFor="address"
              className="flex flex-col gap-1 text-sm font-medium"
            >
              {t("page.addStore.input.address")}
              <Input
                id="address"
                name="address"
                type="text"
                className="w-full rounded border px-2 py-1 text-sm"
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

      <form
        className="grid w-full max-w-6xl items-center justify-center gap-4 lg:grid-cols-3"
        action={action}
      >
        <input type="hidden" name="storeId" value={state?.storeId} />
        <div>
          <p className="mb-1 text-sm font-medium">
            {t("page.addStore.input.businessHours")}
          </p>

          {DAYS.map((day) => (
            <div className="flex items-center justify-between gap-2" key={day}>
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

      <AddProductList
        storeId={state?.storeId ?? ""}
        ownProducts={[]}
        products={products}
      />
    </ContainerWrapper>
  );
}
