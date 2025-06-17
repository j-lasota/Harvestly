"use client";

import { useActionState, useState } from "react";
import { useTranslations } from "next-intl";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { SubmitButton } from "@/components/ui/submit-button";
import { createBusinessHoursForStore } from "../../actions";
import { editStoreAction, removeStore } from "../actions";
import ImageUploader from "@/components/image-uploader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import AddProductList from "./add-product";

import storePlaceholder from "@/public/store_placeholder.jpg";

const DAYS = [
  "MONDAY",
  "TUESDAY",
  "WEDNESDAY",
  "THURSDAY",
  "FRIDAY",
  "SATURDAY",
  "SUNDAY",
];

interface BusinessHoursInput {
  dayOfWeek:
    | "MONDAY"
    | "TUESDAY"
    | "WEDNESDAY"
    | "THURSDAY"
    | "FRIDAY"
    | "SATURDAY"
    | "SUNDAY";
  openingTime: string;
  closingTime: string;
}

interface AvailableProduct {
  id: string;
  name: string;
  category: "FRUIT" | "VEGETABLE";
}

interface StoreOwnProduct {
  id: string;
  product: {
    id: string;
    name: string;
  };
  price: number;
  quantity: number;
  discount: number | null;
  imageUrl: string | null;
}

interface Store {
  id: string;
  name: string;
  city: string;
  address: string;
  latitude: number;
  longitude: number;
  imageUrl: string | null;
  description: string | null;
  businessHours:
    | {
        dayOfWeek:
          | "MONDAY"
          | "TUESDAY"
          | "WEDNESDAY"
          | "THURSDAY"
          | "FRIDAY"
          | "SATURDAY"
          | "SUNDAY";
        openingTime: string;
        closingTime: string;
      }[]
    | null;
  ownProducts: StoreOwnProduct[] | null;
}

export default function EditStoreClientPage({
  store,
  products,
}: {
  store: Store;
  products: AvailableProduct[];
}) {
  const t = useTranslations("");
  const [storeDetailsState, editStoreDetailsAction] = useActionState(
    editStoreAction,
    undefined
  );
  const [removeState, setRemoveState] = useState<{
    success?: boolean;
    message?: string;
  }>({});
  const [image, setImage] = useState<string>(store.imageUrl || "");

  const businessHoursActionWrapper = async (
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    previousState: any,
    formData: FormData
  ) => {
    const storeId = store.id;
    const businessHoursList: BusinessHoursInput[] = DAYS.map((day) => ({
      dayOfWeek: day as BusinessHoursInput["dayOfWeek"],
      openingTime: formData.get(`open_${day}`) as string,
      closingTime: formData.get(`close_${day}`) as string,
    })).filter((hours) => hours.openingTime && hours.closingTime);

    const result = await createBusinessHoursForStore(
      storeId,
      businessHoursList
    );

    if (result) {
      return {
        success: true,
        message: t("page.editStore.feedback.hoursSaved"),
      };
    } else {
      return {
        success: false,
        message: t("page.editStore.feedback.hoursSaveError"),
      };
    }
  };

  const [businessHoursState, businessHoursAction] = useActionState(
    businessHoursActionWrapper,
    undefined
  );

  const [currentBusinessHours, setCurrentBusinessHours] = useState<
    Record<string, { openingTime: string; closingTime: string }>
  >(() => {
    const initialHours: Record<
      string,
      { openingTime: string; closingTime: string }
    > = {};
    DAYS.forEach((day) => {
      initialHours[day] = { openingTime: "", closingTime: "" };
    });

    store.businessHours?.forEach((bh) => {
      initialHours[bh.dayOfWeek] = {
        openingTime: bh.openingTime.substring(0, 5),
        closingTime: bh.closingTime.substring(0, 5),
      };
    });
    return initialHours;
  });

  const handleHourChange = (
    day: string,
    type: "openingTime" | "closingTime",
    value: string
  ) => {
    setCurrentBusinessHours((prev) => ({
      ...prev,
      [day]: {
        ...prev[day],
        [type]: value,
      },
    }));
  };

  const handleRemoveStore = async () => {
    try {
      await removeStore(store.id);
      setRemoveState({
        success: true,
        message: t("page.editStore.feedback.storeRemoved"),
      });
      // Optionally redirect after successful removal
      // window.location.href = '/my-stores';
    } catch {
      setRemoveState({
        success: false,
        message: t("page.editStore.feedback.storeRemoveError"),
      });
    }
  };

  return (
    <ContainerWrapper
      comp="main"
      className="flex flex-col items-center justify-center py-10"
    >
      <h1 className="mb-10 text-center text-5xl font-extrabold text-gray-900">
        {t("page.editStore.title")}
      </h1>

      <section className="mb-8 w-full max-w-6xl rounded-lg bg-white p-8 shadow-lg">
        <h2 className="mb-6 text-3xl font-bold text-gray-800">
          {t("page.editStore.sections.storeDetails")}
        </h2>
        <form
          action={editStoreDetailsAction}
          className="grid grid-cols-1 gap-8 lg:grid-cols-2"
        >
          <div className="flex flex-col items-center justify-center rounded-md bg-gray-50 p-4">
            <ImageUploader
              currImage={image}
              placeholder={storePlaceholder}
              onUploaded={setImage}
            />
          </div>

          <div className="flex flex-col gap-4">
            <input name="storeId" type="hidden" value={store.id} readOnly />
            <input name="image_url" type="hidden" value={image} readOnly />

            <label
              htmlFor="address"
              className="flex flex-col gap-1 text-sm font-medium text-gray-700"
            >
              {t("page.addStore.input.address")}
              <Input
                id="address"
                type="text"
                className="w-full cursor-not-allowed rounded border bg-gray-100 px-3 py-2 text-base text-gray-800"
                defaultValue={store.address || ""}
                readOnly={true}
                disabled={true}
              />
            </label>

            <label
              htmlFor="city"
              className="flex flex-col gap-1 text-sm font-medium text-gray-700"
            >
              {t("page.addStore.input.city")}
              <Input
                id="city"
                type="text"
                className="w-full cursor-not-allowed rounded border bg-gray-100 px-3 py-2 text-base text-gray-800"
                defaultValue={store.city || ""}
                readOnly={true}
                disabled={true}
              />
            </label>

            <label
              htmlFor="description"
              className="flex flex-col gap-1 text-sm font-medium text-gray-700"
            >
              {t("page.addStore.input.description")}
              <textarea
                id="description"
                name="description"
                rows={4}
                className="flex w-full resize-none rounded-md border border-gray-300 bg-white px-3 py-2 text-base text-gray-800 shadow-sm transition-colors outline-none placeholder:text-gray-400 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
                placeholder={t("page.addStore.placeholder.description")}
                defaultValue={store.description || ""}
              />
            </label>

            <div className="mt-4 w-full">
              <SubmitButton
                label={t("page.editStore.action.updateStoreDetails")}
                pendingLabel={t("page.editStore.action.updatingStoreDetails")}
                className="w-full rounded-md bg-[var(--primary)] px-4 py-2 font-semibold text-white shadow transition-colors hover:bg-[color-mix(in_oklab,var(--primary),black_15%)]"
              />
              {storeDetailsState?.message && (
                <p
                  role="alert"
                  className={`mt-2 w-full text-center ${storeDetailsState.success ? "text-green-600" : "text-red-600"}`}
                >
                  {storeDetailsState.message}
                </p>
              )}
            </div>
          </div>
        </form>
      </section>

      <section className="mb-8 w-full max-w-6xl rounded-lg bg-white p-8 shadow-lg">
        <h2 className="mb-6 text-3xl font-bold text-gray-800">
          {t("page.editStore.sections.businessHours")}
        </h2>
        <form
          action={businessHoursAction}
          className="grid grid-cols-1 gap-6 md:grid-cols-1"
        >
          <input type="hidden" name="storeId" value={store.id} readOnly />
          <div className="grid grid-cols-1 gap-x-6 gap-y-4 sm:grid-cols-2 lg:grid-cols-3">
            {DAYS.map((day) => (
              <div
                className="flex flex-col items-start justify-between gap-2 rounded-md border border-gray-200 bg-gray-50 p-2 sm:flex-row sm:items-center"
                key={day}
              >
                <p className="min-w-[100px] font-medium text-gray-700">
                  {t(`days.${day}`)}:
                </p>
                <div className="flex w-full flex-grow items-center gap-2 sm:w-auto">
                  <Input
                    name={`open_${day}`}
                    type="time"
                    className="w-full rounded border border-gray-300 px-3 py-2 text-sm text-gray-800 focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
                    value={currentBusinessHours[day].openingTime}
                    onChange={(e) =>
                      handleHourChange(day, "openingTime", e.target.value)
                    }
                  />
                  <span className="text-gray-500">-</span>
                  <Input
                    name={`close_${day}`}
                    type="time"
                    className="w-full rounded border border-gray-300 px-3 py-2 text-sm text-gray-800 focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
                    value={currentBusinessHours[day].closingTime}
                    onChange={(e) =>
                      handleHourChange(day, "closingTime", e.target.value)
                    }
                  />
                </div>
              </div>
            ))}
          </div>
          <div className="mt-4 w-full">
            <SubmitButton
              label={t("page.editStore.action.saveHours")}
              pendingLabel={t("page.editStore.action.savingHours")}
              className="w-full rounded-md bg-[var(--primary)] px-4 py-2 font-semibold text-white shadow transition-colors hover:bg-[color-mix(in_oklab,var(--primary),black_15%)]"
            />
            {businessHoursState?.message && (
              <p
                role="alert"
                className={`mt-2 w-full text-center ${businessHoursState.success ? "text-green-600" : "text-red-600"}`}
              >
                {businessHoursState.message}
              </p>
            )}
          </div>
        </form>
      </section>

      <section className="mb-8 w-full max-w-6xl rounded-lg bg-white p-8 shadow-lg">
        <AddProductList
          storeId={store.id}
          ownProducts={store.ownProducts || []}
          products={products}
        />
      </section>

      <section className="w-full max-w-6xl rounded-lg bg-white p-8 shadow-lg">
        <div className="flex flex-col items-center gap-4">
          <h2 className="text-2xl font-bold text-gray-800">
            {t("page.editStore.sections.dangerZone")}
          </h2>
          <div className="w-full rounded-md border border-red-300 bg-red-50 p-4">
            <div className="flex flex-col items-center justify-between gap-4 md:flex-row">
              <div>
                <h3 className="font-semibold text-red-800">
                  {t("page.editStore.dangerZone.removeStoreTitle")}
                </h3>
                <p className="text-sm text-red-700">
                  {t("page.editStore.dangerZone.removeStoreWarning")}
                </p>
              </div>
              <Button
                variant="accent"
                onClick={handleRemoveStore}
                disabled={removeState?.success}
              >
                {removeState?.success
                  ? t("page.editStore.feedback.storeRemoved")
                  : t("page.editStore.action.removeStore")}
              </Button>
            </div>
            {removeState?.message && !removeState?.success && (
              <p className="mt-2 text-center text-red-600">
                {removeState.message}
              </p>
            )}
          </div>
        </div>
      </section>
    </ContainerWrapper>
  );
}
