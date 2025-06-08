"use server";

import { revalidatePath } from "next/cache";
import { z } from "zod";

import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";
import { auth } from "@/auth";

export async function getCoordsFromCity(
  city: string
): Promise<{ lat: number; lng: number } | null> {
  try {
    const res = await fetch(
      `https://nominatim.openstreetmap.org/search?format=json&q=${city}&limit=1`
    );
    const data = await res.json();
    if (data.length === 0) return null;

    console.log(data);

    return {
      lat: parseFloat(data[0].lat),
      lng: parseFloat(data[0].lon),
    };
  } catch (e) {
    console.error("City lookup failed", e);
    return null;
  }
}

export async function getAddress(
  lat: number,
  lng: number
): Promise<{
  city: string;
  details: string;
} | null> {
  try {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=20&addressdetails=1`
    );

    if (!response.ok) {
      throw new Error("Geocoding failed");
    }

    const data = await response.json();
    const city =
      data.address.city ||
      data.address.town ||
      data.address.village ||
      data.address.municipality ||
      data.address.county;

    const road = data.address.road || data.address.street;
    const number = data.address.house_number;
    const details = `${road}${number ? ` ${number}` : ""}`;

    return { city, details };
  } catch (error) {
    console.error("Error fetching city:", error);
    return null;
  }
}

const addStoreMutation = graphql(`
  mutation addStore(
    $userId: ID!
    $imageUrl: String!
    $name: String!
    $address: String!
    $city: String!
    $latitude: Float!
    $longitude: Float!
    $description: String!
  ) {
    createStore(
      userId: $userId
      imageUrl: $imageUrl
      name: $name
      address: $address
      city: $city
      latitude: $latitude
      longitude: $longitude
      description: $description
    ) {
      id
    }
  }
`);

// ========== Add Store action ==========
const FormSchema = z.object({
  imageUrl: z.string(),
  name: z.string().min(2).max(100).trim(),
  address: z.string().min(2).max(50).trim(),
  city: z.string().min(2).max(50).trim(),
  description: z
    .string({ message: "Opis jest wymagany." })
    .max(250, { message: "Opis jest zbyt długi, maksymalnie 350 znaków." })
    .trim(),
  latitude: z.number().min(-90).max(90),
  longitude: z.number().min(-180).max(180),
});

type FormState =
  | {
      errors?: {
        imageUrl?: string[];
        name?: string[];
        address?: string[];
        city?: string[];
        description?: string[];
        latitude?: string[];
        longitude?: string[];
      };
      success?: boolean;
      message?: string;
    }
  | undefined;

// TODO: Fix this - error from backend - idk why / maybe not all fields which are required are passed
export async function addStoreAction(state: FormState, formData: FormData) {
  const session = await auth();
  if (!session?.user || !session.user.id) return;

  const validatedFields = FormSchema.safeParse({
    imageUrl: formData.get("image_url") || "",
    name: formData.get("name"),
    address: formData.get("address"),
    city: formData.get("city"),
    description: formData.get("description"),
    latitude: Number(formData.get("latitude")),
    longitude: Number(formData.get("longitude")),
  });

  if (!validatedFields.success) {
    return {
      errors: validatedFields.error.flatten().fieldErrors,
    };
  }

  const { imageUrl, name, address, city, description, latitude, longitude } =
    validatedFields.data;

  const { data } = await getClient().mutate({
    mutation: addStoreMutation,
    variables: {
      userId: session.user.id,
      imageUrl,
      name,
      address,
      city,
      latitude,
      longitude,
      description,
    },
  });

  if (data) {
    revalidatePath("/store/");

    return {
      success: true,
      message: "Sklep został dodany.",
    };
  } else {
    return {
      message: "Wystąpił błąd podczas dodawania sklepu.",
    };
  }
}
