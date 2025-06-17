"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { z } from "zod";

import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";
import { auth } from "@/auth";

// ========== Add OwnProduct mutation action ==========
const addOwnProductMutation = graphql(`
  mutation addOwnProduct(
    $storeId: ID!
    $productId: ID!
    $price: BigDecimal!
    $quantity: Int!
    $imageUrl: String
  ) {
    createOwnProduct(
      storeId: $storeId
      productId: $productId
      price: $price
      quantity: $quantity
      imageUrl: $imageUrl
    ) {
      id
    }
  }
`);

const editOwnProductMutation = graphql(`
  mutation editOwnProduct($id: ID!, $discount: Int!) {
    updateOwnProduct(id: $id, discount: $discount) {
      id
    }
  }
`);

const removeOwnProductMutation = graphql(`
  mutation removeOwnProduct($id: ID!) {
    deleteOwnProduct(id: $id)
  }
`);

const removeStoreMutation = graphql(`
  mutation removeStore($id: ID!) {
    deleteStore(id: $id)
  }
`);

export const removeOwnProduct = async (productId: string, storeId: string) => {
  try {
    const session = await auth();
    if (!session?.user || !session.user.id) return;

    const { data } = await getClient().mutate({
      mutation: removeOwnProductMutation,
      variables: { id: productId },
    });

    revalidatePath(`/store/${storeId}`);
    return data;
  } catch (error) {
    console.error("Error in removeOwnProduct:", error);
    return;
  }
};

export const removeStore = async (storeId: string) => {
  try {
    const session = await auth();
    if (!session?.user || !session.user.id) return;

    await getClient().mutate({
      mutation: removeStoreMutation,
      variables: { id: storeId },
    });

    redirect(`/my-stores`);
  } catch (error) {
    console.error("Error in removeStore:", error);
    return;
  }
};

const AddProductFormSchema = z.object({
  storeId: z.string(),
  productId: z.string(),
  price: z.preprocess((v) => Number(v), z.number().min(0)),
  discount: z.preprocess((v) => Number(v), z.number().min(0).max(100)),
  quantity: z.preprocess((v) => Number(v), z.number().min(1)),
  imageUrl: z.string().nullable(),
});

type AddProductFormState =
  | {
      errors?: {
        storeId?: string[];
        productId?: string[];
        price?: string[];
        discount?: string[];
        quantity?: string[];
        imageUrl?: string[];
      };
      success?: boolean;
      message?: string;
    }
  | undefined;

export async function addOwnProductAction(
  state: AddProductFormState,
  formData: FormData
) {
  try {
    const session = await auth();
    if (!session?.user || !session.user.id) return;

    const validatedFields = AddProductFormSchema.safeParse({
      storeId: formData.get("storeId"),
      productId: formData.get("productId"),
      price: formData.get("price") || 0,
      discount: formData.get("discount") || 0,
      quantity: formData.get("quantity") || 1,
      imageUrl: formData.get("imageUrl") || null,
    });

    if (!validatedFields.success) {
      return {
        errors: validatedFields.error.flatten().fieldErrors,
      };
    }

    const { storeId, productId, price, quantity, imageUrl, discount } =
      validatedFields.data;

    const { data } = await getClient().mutate({
      mutation: addOwnProductMutation,
      variables: {
        storeId,
        productId,
        price,
        quantity,
        imageUrl,
      },
    });

    if (data) {
      if (!data.createOwnProduct) return;

      const { data: update } = await getClient().mutate({
        mutation: editOwnProductMutation,
        variables: {
          id: data.createOwnProduct.id,
          discount,
        },
      });

      if (update) {
        revalidatePath(`/store/${storeId}`);
        return {
          success: true,
          message: "Produkt został dodany.",
        };
      }
    } else {
      return {
        message: "Wystąpił błąd podczas dodawania produktu.",
      };
    }
  } catch (error) {
    console.error("Error in addOwnProductAction:", error);
    return {
      message: "Wystąpił błąd podczas dodawania produktu.",
    };
  }
}

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
