"use client";

import { useActionState } from "react";

import { SubmitButton } from "@/components/submit-button";
import { addOpinionAction } from "../actions";

// TODO: Add stars rating system improve UX
const AddOpinion = ({ slug }: { slug: string }) => {
  const [stateEmail, actionEmail] = useActionState(addOpinionAction, undefined);

  return (
    <form
      action={actionEmail}
      className="rounded-xl bg-white px-4 py-3 shadow-md"
    >
      <input type="hidden" name="storeId" value={slug} />

      <label className="flex flex-col gap-2" htmlFor="message">
        <span className="text-sm">Wiadomość</span>

        <textarea
          placeholder="Treść wiadomości..."
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
          <SubmitButton label="Wyślij" pendingLabel="Wysyłanie..." size="lg" />
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
