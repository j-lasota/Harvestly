"use server";

import { revalidatePath } from "next/cache";
import { getClient } from "@/graphql/apollo-client";
import { gql } from "@apollo/client";
import { auth } from "@/auth";


const deleteStoreAdminMutation = gql`
  mutation DeleteStore($id: ID!) {
    deleteStore(id: $id)
  }
`;

const unreportStoreAdminMutation = gql`
  mutation UpdateStore($id: ID!) {
    updateStore(id: $id, reported: false) {
      id
      name
      reported
    }
  }
`;

const deleteOpinionAdminMutation = gql`
    mutation DeleteOpinion($id: ID!) {
        deleteOpinion(id: $id)
    }
`;

const unreportOpinionAdminMutation = gql`
    mutation UpdateOpinion($id: ID!) {
        updateOpinion(id: $id, reported: false) {
            id
            description
            stars
            reported
    }
}
`;





export const deleteStoreAdminAction = async (storeId: string) => {
    const session = await auth();


    try {
        const { data } = await getClient().mutate({
            mutation: deleteStoreAdminMutation,
            variables: { id: storeId },
        });
        revalidatePath("/admin");
        return { success: true, data };
    } catch (error) {
        console.error("Błąd podczas usuwania sklepu (Server Action):", error);
        return { success: false, error: "Nie udało się usunąć sklepu." };
    }
};

export const unreportStoreAdminAction = async (storeId: string) => {
    const session = await auth();

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

export const deleteOpinionAdminAction = async (opinionId: string) => {
    const session = await auth();

    if (!session?.user?.permissions?.includes("manage:all")) {
        console.warn("Brak uprawnień: Użytkownik próbował usunąć opinię bez odpowiednich uprawnień.");
        return { success: false, error: "Brak uprawnień do wykonania tej operacji." };
    }

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

export const unreportOpinionAdminAction = async (opinionId: string) => {
    const session = await auth();

    if (!session?.user?.permissions?.includes("manage:all")) {
        console.warn("Brak uprawnień: Użytkownik próbował odrzucić zgłoszenie opinii bez odpowiednich uprawnień.");
        return { success: false, error: "Brak uprawnień do wykonania tej operacji." };
    }

    try {
        const { data } = await getClient().mutate({
            mutation: unreportOpinionAdminMutation,
            variables: { id: opinionId, input: { reported: false } }, // Przekazanie obiektu 'input'
        });
        revalidatePath("/admin");
        return { success: true, data };
    } catch (error) {
        console.error("Błąd podczas oznaczania opinii jako niezgłoszonej (Server Action):", error);
        return { success: false, error: "Nie udało się zaktualizować statusu opinii." };
    }
};