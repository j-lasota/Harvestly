"use server";

import { revalidatePath } from "next/cache";
import { z } from "zod";

import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";
import { auth } from "@/auth";

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

const addVerificationMutation = graphql(`
  mutation addVerification($userId: ID!, $storeId: ID!) {
    createVerification(userId: $userId, storeId: $storeId) {
      id
    }
  }
`);

const reportStoreMutation = graphql(`
  mutation reportStore($userId: ID!, $storeId: ID!) {
    reportStore(userId: $userId, storeId: $storeId) {
      id
    }
  }
`);

const reportOpinionMutation = graphql(`
  mutation reportOpinion($userId: ID!, $opinionId: ID!) {
    reportOpinion(userId: $userId, opinionId: $opinionId) {
      id
    }
  }
`);

const addOpinionMutation = graphql(`
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

 const createMultipleBusinessHoursMutation = graphql(`
  mutation CreateMultipleBusinessHours(
    $storeId: ID!
    $businessHoursList: [BusinessHoursInput!]!
  ) {
    createMultipleBusinessHours(
      storeId: $storeId
      businessHoursList: $businessHoursList
    ) {
      id
      dayOfWeek
      openingTime
      closingTime
    }
  }
`);

// ========== Add favorite store action ==========
export const addFavoriteStore = async (storeId: string) => {
  try {
    const session = await auth();
    if (!session?.user || !session.user.id) return;

    const { data } = await getClient().mutate({
      mutation: addFavoriteStoreMutation,
      variables: { userId: session.user.id, storeId },
    });

    return data;
  } catch (error) {
    console.error("Error in addFavoriteStore:", error);
    return;
  }
};

export const removeFavoriteStore = async (storeId: string) => {
  try {
    const session = await auth();
    if (!session?.user || !session.user.id) return;

    const { data } = await getClient().mutate({
      mutation: removeFavoriteStoreMutation,
      variables: { userId: session.user.id, storeId },
    });

    return data;
  } catch (error) {
    console.error("Error in removeFavoriteStore:", error);
    return;
  }
};

// ========== Add verification action ==========
export const addVerification = async (storeId: string) => {
  try {
    const session = await auth();
    if (!session?.user || !session.user.id) return;

    const { data } = await getClient().mutate({
      mutation: addVerificationMutation,
      variables: { userId: session.user.id, storeId },
    });

    return data;
  } catch (error) {
    console.error("Error in addVerification:", error);
    return;
  }
};

// ========== Report store action ==========
export const reportStore = async (storeId: string) => {
  try {
    const session = await auth();
    if (!session?.user || !session.user.id) return;

    const { data } = await getClient().mutate({
      mutation: reportStoreMutation,
      variables: { userId: session.user.id, storeId },
    });

    return data;
  } catch (error) {
    console.error("Error in reportStore:", error);
    return;
  }
};

// ========== Report opinion action ==========
export const reportOpinion = async (opinionId: string) => {
  try {
    const session = await auth();
    if (!session?.user || !session.user.id) return;

    const { data } = await getClient().mutate({
      mutation: reportOpinionMutation,
      variables: { userId: session.user.id, opinionId },
    });

    return data;
  } catch (error) {
    console.error("Error in reportOpinion:", error);
    return;
  }
};

// ========== Add opinion action ==========
const FormSchema = z.object({
  description: z
    .string({ message: "Opis jest wymagany." })
    .max(250, { message: "Opis jest zbyt długi, maksymalnie 350 znaków." })
    .trim(),
  storeId: z.string({ message: "ID sklepu jest wymagany." }),
  stars: z.number().min(0).max(5),
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
  try {
    const session = await auth();
    if (!session?.user || !session.user.id) return;

    const validatedFields = FormSchema.safeParse({
      description: formData.get("description"),
      storeId: formData.get("storeId"),
      stars: Number(formData.get("stars")) || 0,
    });

    if (!validatedFields.success) {
      return {
        errors: validatedFields.error.flatten().fieldErrors,
      };
    }

    const { description, storeId, stars } = validatedFields.data;

    const { data } = await getClient().mutate({
      mutation: addOpinionMutation,
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
  } catch (error) {
    console.error("Error in addOpinionAction:", error);
    return {
      message: "Wystąpił błąd podczas dodawania opinii.",
    };
  }
}

interface BusinessHoursInput {
  dayOfWeek: "MONDAY" | "TUESDAY" | "WEDNESDAY" | "THURSDAY" | "FRIDAY" | "SATURDAY" | "SUNDAY";
  openingTime: string;
  closingTime: string;
}

export const createBusinessHoursForStore = async (
  storeId: string,
  businessHoursList: BusinessHoursInput[]
) => {
  try {
    const { data } = await getClient().mutate({
      mutation: createMultipleBusinessHoursMutation,
      variables: { storeId, businessHoursList },
    });

    return data;
  } catch (error) {
    console.error("Error in createBusinessHoursForStore:", error);
    return;
  }
};
