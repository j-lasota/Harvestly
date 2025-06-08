"use client";

import { useActionState, useState } from "react";
import { useTranslations } from "next-intl";
import { Star } from "lucide-react";

import { SubmitButton } from "@/components/submit-button";
import { addOpinionAction } from "../actions";
import { cn } from "@/lib/utils";

const AddOpinion = ({ storeId }: { storeId: string }) => {
  const t = useTranslations("addOpinion");
  const [stateEmail, actionEmail] = useActionState(addOpinionAction, undefined);
  const [starsPreview, setStarsPreview] = useState(0);
  const [starsValue, setStarsValue] = useState(0);

  return (
    <form
      action={actionEmail}
      className="bg-background-elevated border-shadow ring-ring rounded-xl border-r-3 border-b-4 px-4 py-3 shadow-md ring"
    >
      <input type="hidden" name="storeId" value={storeId} />

      <label className="mb-3 flex flex-col gap-2" htmlFor="stars">
        <span className="text-sm">{t("ratingLabel")}</span>
        <div className="flex items-center gap-0.25">
          {[1, 2, 3, 4, 5].map((star) => (
            <button
              key={star}
              type="button"
              aria-label={t("starAriaLabel", { count: star })}
              tabIndex={0}
              className="border-none bg-transparent p-0"
              onClick={() => setStarsValue(star)}
              onMouseEnter={() => setStarsPreview(star)}
              onMouseLeave={() => setStarsPreview(0)}
            >
              <Star
                className={cn(
                  "text-primary",
                  star <= starsPreview ? "fill-primary opacity-75" : "",
                  star <= starsValue ? "fill-primary" : "",
                  "transition-all duration-300"
                )}
                size={24}
              />
            </button>
          ))}
        </div>
        <input
          type="hidden"
          name="stars"
          id="stars"
          value={starsValue}
          readOnly
        />
      </label>

      <label className="flex flex-col gap-2" htmlFor="message">
        <span className="text-sm">{t("messageLabel")}</span>

        <textarea
          placeholder={t("messagePlaceholder")}
          name="description"
          id="description"
          rows={5}
          aria-invalid={stateEmail?.errors?.description ? "true" : undefined}
          aria-describedby={
            stateEmail?.errors?.description ? "description-error" : undefined
          }
          className="w-full resize-none px-1 py-2 outline-none"
        />
      </label>
      {stateEmail?.errors?.description && (
        <div
          id="description-error"
          role="alert"
          className="text-sm text-red-600"
        >
          {stateEmail.errors.description[0]}
        </div>
      )}

      <div className="mt-2">
        {!stateEmail?.success && (
          <SubmitButton
            label={t("submit")}
            pendingLabel={t("pending")}
            size="lg"
          />
        )}
        {stateEmail?.message && (
          <p role="alert" className="text-primary w-full text-center">
            {stateEmail.message}
          </p>
        )}
      </div>
    </form>
  );
};

export default AddOpinion;
