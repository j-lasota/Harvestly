"use client";

import { useActionState, useState } from "react";
import { useTranslations } from "next-intl";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import accountPlaceholder from "@/public/account_placeholder.jpg";
import { SubmitButton } from "@/components/ui/submit-button";
import ImageUploader from "@/components/image-uploader";
import { Input } from "@/components/ui/input";
import { editUserAction } from "../actions";

interface User {
  id: string;
  email: string | null;
  firstName: string;
  lastName: string;
  phoneNumber: string | null;
  facebookNickname: string | null;
  img: string | null;
  nip: string | null;
  publicTradePermitNumber: string | null;
}

export default function ProfileClientPage({ data }: { data: User }) {
  const t = useTranslations("page");
  const [state, action] = useActionState(editUserAction, undefined);
  const [image, setImage] = useState<string>(data.img || "");

  return (
    <ContainerWrapper
      comp="main"
      className="flex h-full items-center justify-center"
    >
      <form
        className="my-10 grid w-full max-w-6xl items-center justify-center gap-4 lg:my-0 lg:grid-cols-3"
        action={action}
      >
        <div className="lg:col-span-3">
          <h1 className="text-4xl font-semibold">{t("profile.title")}</h1>
        </div>

        <div className="flex flex-col gap-4">
          <ImageUploader
            currImage={image}
            placeholder={accountPlaceholder}
            onUploaded={setImage}
          />
        </div>

        <div className="scrollbar-hidden lg:col-span-2">
          <div className="mr-4 ml-auto flex max-w-lg flex-col gap-4">
            <input
              name="img"
              type="hidden"
              value={image || data.img || ""}
              readOnly
              className="hidden"
            />

            <label
              htmlFor="firstName"
              className="flex flex-col gap-1 text-sm font-medium"
            >
              {t("profile.input.firstName")}
              <Input
                id="firstName"
                name="firstName"
                type="text"
                className="w-full rounded border px-2 py-1 text-sm"
                defaultValue={data.firstName || ""}
              />
            </label>

            <label
              htmlFor="lastName"
              className="flex flex-col gap-1 text-sm font-medium"
            >
              {t("profile.input.lastName")}
              <Input
                id="lastName"
                name="lastName"
                type="text"
                className="w-full rounded border px-2 py-1 text-sm"
                defaultValue={data.lastName || ""}
              />
            </label>

            <label
              htmlFor="email"
              className="flex flex-col gap-1 text-sm font-medium"
            >
              {t("profile.input.email")}
              <Input
                id="email"
                name="email"
                type="text"
                className="w-full rounded border px-2 py-1 text-sm opacity-50"
                defaultValue={data.email || ""}
                readOnly
              />
            </label>

            <label
              htmlFor="facebookNickname"
              className="flex flex-col gap-1 text-sm font-medium"
            >
              {t("profile.input.facebookNickname")}
              <Input
                id="facebookNickname"
                name="facebookNickname"
                type="text"
                className="w-full rounded border px-2 py-1 text-sm"
                defaultValue={data.facebookNickname || ""}
              />
            </label>

            <label
              htmlFor="phoneNumber"
              className="flex flex-col gap-1 text-sm font-medium"
            >
              {t("profile.input.phoneNumber")}
              <Input
                id="phoneNumber"
                name="phoneNumber"
                type="phone"
                className="w-full rounded border px-2 py-1 text-sm"
                placeholder="000000000"
                defaultValue={data.phoneNumber || ""}
              />
            </label>

            <label
              htmlFor="nip"
              className="flex flex-col gap-1 text-sm font-medium"
            >
              {t("profile.input.nip")}
              <Input
                id="nip"
                name="nip"
                type="text"
                className="w-full rounded border px-2 py-1 text-sm"
                placeholder="000000000"
                defaultValue={data.nip || ""}
              />
            </label>

            <label
              htmlFor="publicTradePermitNumber"
              className="flex flex-col gap-1 text-sm font-medium"
            >
              {t("profile.input.publicTradePermitNumber")}
              <Input
                id="publicTradePermitNumber"
                name="publicTradePermitNumber"
                type="text"
                className="w-full rounded border px-2 py-1 text-sm"
                defaultValue={data.publicTradePermitNumber || ""}
              />
            </label>

            <div className="mt-2 w-full">
              {!state?.success && (
                <SubmitButton
                  label={t("profile.action.submit")}
                  pendingLabel={t("profile.action.pending")}
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
    </ContainerWrapper>
  );
}
