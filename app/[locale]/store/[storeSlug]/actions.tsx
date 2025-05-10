"use server";

import { revalidatePath } from "next/cache";
import { z } from "zod";

import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";
import { auth } from "@/auth";

// TODO: Przetestować - bez autha nie jestem w stanie

// ========== GraphQL mutations queries ==========
const addFavoriteStoreMutation = graphql(`
  mutation addFavoriteStore($userId: ID!, $storeId: ID!) {
    addFavoriteStore(userId: $userId, storeId: $storeId) {
      id
    }
  }
`);

const removeFavoriteStoreMutation = graphql(`
  mutation removeFavoriteStore($userId: ID!, $storeId: ID!) {
    removeFavoriteStore(userId: $userId, storeId: $storeId) {
      id
    }
  }
`);

const AddOpinionMutation = graphql(`
  mutation addOpinion(
    $userId: ID!
    $storeId: ID!
    $description: String!
    $stars: Int!
  ) {
    createOpinion(
      userId: $userId
      storeId: $storeId
      description: $description
      stars: $stars
    ) {
      id
    }
  }
`);

// ========== Add favorite store action ==========
export const addFavoriteStore = async (storeId: string) => {
  const session = await auth();
  if (!session?.user || !session.user.id) return;

  const { data } = await getClient().mutate({
    mutation: addFavoriteStoreMutation,
    variables: { userId: session.user.id, storeId },
  });

  return data;
};

export const removeFavoriteStore = async (storeId: string) => {
  const session = await auth();
  if (!session?.user || !session.user.id) return;

  const { data } = await getClient().mutate({
    mutation: removeFavoriteStoreMutation,
    variables: { userId: session.user.id, storeId },
  });

  return data;
};

// ========== Add opinion action ==========
const FormSchema = z.object({
  description: z
    .string({ message: "Opis jest wymagany." })
    .max(250, { message: "Opis jest zbyt długi, maksymalnie 350 znaków." })
    .trim(),
  storeId: z.string({ message: "ID sklepu jest wymagany." }),
  stars: z.number({ message: "Liczba gwiazdek jest wymagana." }).min(1).max(5),
});

type FormState =
  | {
      errors?: {
        description?: string[];
        storeId?: string[];
        stars?: string[];
      };
      success?: boolean;
      message?: string;
    }
  | undefined;

export async function addOpinionAction(state: FormState, formData: FormData) {
  const session = await auth();
  if (!session?.user || !session.user.id) return;

  const validatedFields = FormSchema.safeParse({
    description: formData.get("description"),
    storeId: formData.get("storeId"),
    stars: formData.get("stars"),
  });

  if (!validatedFields.success) {
    return {
      errors: validatedFields.error.flatten().fieldErrors,
    };
  }

  const { description, storeId, stars } = validatedFields.data;

  const { data } = await getClient().mutate({
    mutation: AddOpinionMutation,
    variables: { userId: session.user.id, storeId, description, stars },
  });

  if (data) {
    revalidatePath("/store/");

    return {
      success: true,
      message: "Opinia została dodana.",
    };
  } else {
    return {
      message: "Wystąpił błąd podczas dodawania opinii.",
    };
  }
}
