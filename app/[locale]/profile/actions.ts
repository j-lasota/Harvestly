"use server";

import { revalidatePath } from "next/cache";
import { z } from "zod";

import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";
import { auth } from "@/auth";

const editUserMutation = graphql(`
  mutation editUser(
    $id: ID!
    $img: String
    $firstName: String!
    $lastName: String!
    $facebookNickname: String
    $phoneNumber: String
    $nip: String
    $publicTradePermitNumber: String
  ) {
    updateUser(
      id: $id
      img: $img
      firstName: $firstName
      lastName: $lastName
      facebookNickname: $facebookNickname
      phoneNumber: $phoneNumber
      nip: $nip
      publicTradePermitNumber: $publicTradePermitNumber
    ) {
      id
    }
  }
`);

// TODO: Improve schema
// ========== Edit User action ==========
const FormSchema = z.object({
  img: z.string().nullable(),
  firstName: z.string().min(2).max(100).trim(),
  lastName: z.string().min(2).max(100).trim(),
  facebookNickname: z.string().min(2).max(50).trim().nullable(),
  phoneNumber: z.string().min(9).max(10).trim().nullable(),
  nip: z.string().min(9).max(10).trim().nullable(),
  publicTradePermitNumber: z.string().min(9).max(10).trim().nullable(),
});

type FormState =
  | {
      errors?: {
        img?: string[];
        firstName?: string[];
        lastName?: string[];
        facebookNickname?: string[];
        phoneNumber?: string[];
      };
      success?: boolean;
      message?: string;
    }
  | undefined;

export async function editUserAction(state: FormState, formData: FormData) {
  try {
    const session = await auth();
    if (!session?.user || !session.user.id) return;

    const validatedFields = FormSchema.safeParse({
      img: formData.get("img") || null,
      firstName: formData.get("firstName"),
      lastName: formData.get("lastName"),
      facebookNickname: formData.get("facebookNickname") || null,
      phoneNumber: formData.get("phoneNumber") || null,
      nip: formData.get("nip") || null,
      publicTradePermitNumber: formData.get("publicTradePermitNumber") || null,
    });

    if (!validatedFields.success) {
      return {
        errors: validatedFields.error.flatten().fieldErrors,
      };
    }

    const {
      img,
      firstName,
      lastName,
      facebookNickname,
      phoneNumber,
      nip,
      publicTradePermitNumber,
    } = validatedFields.data;

    const { data } = await getClient().mutate({
      mutation: editUserMutation,
      variables: {
        id: session.user.id,
        img,
        firstName,
        lastName,
        facebookNickname,
        phoneNumber,
        nip,
        publicTradePermitNumber,
      },
    });

    if (data) {
      revalidatePath("/profile");

      return {
        success: true,
        message: "Profil został zaktualizowany.",
      };
    } else {
      return {
        message: "Wystąpił błąd podczas aktualizacji profilu.",
      };
    }
  } catch (error) {
    console.error("Error in editUserAction:", error);
    return {
      message: "Wystąpił błąd podczas aktualizacji profilu.",
    };
  }
}
