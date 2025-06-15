"use server";

import { revalidatePath } from "next/cache";
import { z } from "zod";

import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";
import { auth } from "@/auth";

const editUserMutation = graphql(`
  mutation editUser(
    $id: ID!
    $img: String!
    $firstName: String!
    $lastName: String!
    $facebookNickname: String!
    $phoneNumber: String!
  ) {
    updateUser(
      id: $id
      img: $img
      firstName: $firstName
      lastName: $lastName
      facebook_nickname: $facebookNickname
      phoneNumber: $phoneNumber
    ) {
      id
    }
  }
`);

// TODO: Improve schema
// ========== Edit User action ==========
const FormSchema = z.object({
  img: z.string(),
  firstName: z.string().min(2).max(100).trim(),
  lastName: z.string().min(2).max(100).trim(),
  facebookNickname: z
    .string()
    .min(2)
    .max(50)
    .trim()
    .optional()
    .or(z.literal("")),
  phoneNumber: z.string().min(9).max(10).trim().optional().or(z.literal("")),
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
  const session = await auth();
  if (!session?.user || !session.user.id) return;

  const validatedFields = FormSchema.safeParse({
    img: formData.get("img") || "",
    firstName: formData.get("firstName"),
    lastName: formData.get("lastName"),
    facebookNickname: formData.get("facebookNickname") || "",
    phoneNumber: formData.get("phoneNumber"),
  });

  if (!validatedFields.success) {
    return {
      errors: validatedFields.error.flatten().fieldErrors,
    };
  }

  const { img, firstName, lastName, facebookNickname, phoneNumber } =
    validatedFields.data;

  const { data } = await getClient().mutate({
    mutation: editUserMutation,
    variables: {
      id: session.user.id,
      img,
      firstName,
      lastName,
      facebookNickname: facebookNickname || "",
      phoneNumber: phoneNumber || "",
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
}
