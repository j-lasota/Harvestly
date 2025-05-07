"use server";

import { revalidatePath } from "next/cache";
import { z } from "zod";

import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";
import { auth } from "@/auth";

// TODO: Przetestować - bez autha nie jestem w stanie

// ========== GraphQL mutations queries ==========
const addFavoriteShopMutation = graphql(`
  mutation addFavoriteShop($userId: ID!, $shopId: ID!) {
    addFavoriteShop(userId: $userId, shopId: $shopId) {
      id
    }
  }
`);

const removeFavoriteShopMutation = graphql(`
  mutation removeFavoriteShop($userId: ID!, $shopId: ID!) {
    removeFavoriteShop(userId: $userId, shopId: $shopId) {
      id
    }
  }
`);

const AddOpinionMutation = graphql(`
  mutation addOpinion(
    $userId: ID!
    $shopId: ID!
    $description: String!
    $stars: Int!
  ) {
    createOpinion(
      userId: $userId
      shopId: $shopId
      description: $description
      stars: $stars
    ) {
      id
    }
  }
`);

// ========== Add favorite shop action ==========
export const addFavoriteShop = async (shopId: string) => {
  const session = await auth();
  if (!session?.user || !session.user.id) return;

  const { data } = await getClient().mutate({
    mutation: addFavoriteShopMutation,
    variables: { userId: session.user.id, shopId },
  });

  return data;
};

export const removeFavoriteShop = async (shopId: string) => {
  const session = await auth();
  if (!session?.user || !session.user.id) return;

  const { data } = await getClient().mutate({
    mutation: removeFavoriteShopMutation,
    variables: { userId: session.user.id, shopId },
  });

  return data;
};

// ========== Add opinion action ==========
const FormSchema = z.object({
  description: z
    .string({ message: "Opis jest wymagany." })
    .max(250, { message: "Opis jest zbyt długi, maksymalnie 350 znaków." })
    .trim(),
  shopId: z.string({ message: "ID sklepu jest wymagany." }),
  stars: z.number({ message: "Liczba gwiazdek jest wymagana." }).min(1).max(5),
});

type FormState =
  | {
      errors?: {
        description?: string[];
        shopId?: string[];
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
    shopId: formData.get("shopId"),
    stars: formData.get("stars"),
  });

  if (!validatedFields.success) {
    return {
      errors: validatedFields.error.flatten().fieldErrors,
    };
  }

  const { description, shopId, stars } = validatedFields.data;

  const { data } = await getClient().mutate({
    mutation: AddOpinionMutation,
    variables: { userId: session.user.id, shopId, description, stars },
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
