"use server";

import { revalidatePath } from "next/cache";
import { z } from "zod";

import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";
import { auth } from "@/auth";

// ========== GraphQL mutations queries ==========
const editStoreMutation = graphql(`
  mutation editStore($id: ID!, $imageUrl: String, $description: String) {
    updateStoreByOwner(
      id: $id
      imageUrl: $imageUrl
      description: $description
    ) {
      id
    }
  }
`);

// ========== Edit Store action ==========
const StoreFormSchema = z.object({
  storeId: z.string(),
  imageUrl: z.string().nullable(),
  description: z
    .string({ message: "Opis jest wymagany." })
    .max(250, { message: "Opis jest zbyt długi, maksymalnie 350 znaków." })
    .trim()
    .nullable(),
});

type StoreFormState =
  | {
      errors?: {
        storeId?: string[];
        imageUrl?: string[];
        description?: string[];
      };
      success?: boolean;
      message?: string;
    }
  | undefined;

export async function editStoreAction(
  state: StoreFormState,
  formData: FormData
) {
  try {
    const session = await auth();
    if (!session?.user || !session.user.id) return;

    const validatedFields = StoreFormSchema.safeParse({
      storeId: formData.get("storeId"),
      imageUrl: formData.get("image_url") || null,
      description: formData.get("description") || null,
    });

    if (!validatedFields.success) {
      return {
        errors: validatedFields.error.flatten().fieldErrors,
      };
    }

    const { storeId, imageUrl, description } = validatedFields.data;

    const { data } = await getClient().mutate({
      mutation: editStoreMutation,
      variables: {
        id: storeId,
        imageUrl,
        description,
      },
    });

    if (data) {
      revalidatePath("/store/");

      return {
        success: true,
        message: "Sklep został zaaktualizowany.",
      };
    } else {
      return {
        message: "Wystąpił błąd podczas aktualizacji sklepu.",
      };
    }
  } catch (error) {
    console.error("Error in addStoreAction:", error);
    return {
      message: "Wystąpił błąd podczas aktualizacji sklepu.",
    };
  }
}
