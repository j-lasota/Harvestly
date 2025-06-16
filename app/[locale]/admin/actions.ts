// 'use server' na początku pliku oznacza, że wszystkie eksportowane funkcje będą Server Actions
"use server";

import { revalidatePath } from "next/cache";
import { getClient } from "@/graphql/apollo-client";
import { gql } from "@apollo/client";
import { auth } from "@/auth";

// --- GraphQL Mutations for Admin Actions ---

// Mutacja do usuwania sklepu (zgodna z Twoim graphql-env: zwraca Boolean)
const deleteStoreAdminMutation = gql`
  mutation DeleteStoreAdmin($id: ID!) {
    deleteStore(id: $id)
  }
`;

// Mutacja do oznaczania sklepu jako niezgłoszonego (reported: false)
const unreportStoreAdminMutation = gql`
  mutation UnreportStoreAdmin($id: ID!) {
    updateStore(id: $id, input: { reported: false }) {
      id
      name
      reported
    }
  }
`;

// Mutacja do usuwania opinii (zakładam, że zwraca Boolean, tak jak deleteStore)
const deleteOpinionAdminMutation = gql`
  mutation DeleteOpinionAdmin($id: ID!) {
    deleteOpinion(id: $id)
  }
`;

// Mutacja do oznaczania opinii jako niezgłoszonej (jeśli opinia ma pole 'reported')
const unreportOpinionAdminMutation = gql`
  mutation UnreportOpinionAdmin($id: ID!) {
    updateOpinion(id: $id, input: { reported: false }) {
      id
      description
      reported # Upewnij się, że twoja opinia ma pole 'reported'
    }
  }
`;


// --- Server Actions ---

// Action do usuwania sklepu
export const deleteStoreAdminAction = async (storeId: string) => {
  const session = await auth();
  // TODO: Dodaj sprawdzenie uprawnień administratora tutaj, np.:
  // if (!session?.user?.permissions?.includes("manage:all")) {
  //   return { success: false, error: "Brak uprawnień do wykonania tej operacji." };
  // }

  try {
    const { data } = await getClient().mutate({
      mutation: deleteStoreAdminMutation,
      variables: { id: storeId },
    });
    // Revalidacja ścieżki admina, aby odświeżyć listę na stronie
    revalidatePath("/admin");
    return { success: true, data };
  } catch (error) {
    console.error("Błąd podczas usuwania sklepu (Server Action):", error);
    return { success: false, error: "Nie udało się usunąć sklepu." };
  }
};

// Action do oznaczania sklepu jako niezgłoszonego
export const unreportStoreAdminAction = async (storeId: string) => {
  const session = await auth();
  // TODO: Dodaj sprawdzenie uprawnień administratora

  try {
    const { data } = await getClient().mutate({
      mutation: unreportStoreAdminMutation,
      variables: { id: storeId },
    });
    revalidatePath("/admin");
    return { success: true, data };
  } catch (error) {
    console.error("Błąd podczas oznaczania sklepu jako niezgłoszonego (Server Action):", error);
    return { success: false, error: "Nie udało się zaktualizować statusu sklepu." };
  }
};

// Action do usuwania opinii
export const deleteOpinionAdminAction = async (opinionId: string) => {
  const session = await auth();
  // TODO: Dodaj sprawdzenie uprawnień administratora

  try {
    const { data } = await getClient().mutate({
      mutation: deleteOpinionAdminMutation,
      variables: { id: opinionId },
    });
    revalidatePath("/admin");
    return { success: true, data };
  } catch (error) {
    console.error("Błąd podczas usuwania opinii (Server Action):", error);
    return { success: false, error: "Nie udało się usunąć opinii." };
  }
};

// Action do oznaczania opinii jako niezgłoszonej
export const unreportOpinionAdminAction = async (opinionId: string) => {
  const session = await auth();
  // TODO: Dodaj sprawdzenie uprawnień administratora

  try {
    const { data } = await getClient().mutate({
      mutation: unreportOpinionAdminMutation,
      variables: { id: opinionId },
    });
    revalidatePath("/admin");
    return { success: true, data };
  } catch (error) {
    console.error("Błąd podczas oznaczania opinii jako niezgłoszonej (Server Action):", error);
    return { success: false, error: "Nie udało się zaktualizować statusu opinii." };
  }
};