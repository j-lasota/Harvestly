import { allShopsLocationsQuery,storeBySlugQuery, allProductsQuery, allProductsQueryMap } from "@/graphql/query";
import MapClientPage from "./components/client-page";
import { getClient } from "@/graphql/apollo-client";

interface BusinessHoursProps {
  dayOfWeek: string;
  openingTime: string;
  closingTime: string;
}

export interface Store {
  id: string;
  name: string;
  description?: string;
  latitude: number;
  longitude: number;
  city: string;
  address: string;
  imageUrl?: string;
  businessHours?: BusinessHoursProps[];
  products?: string[];
  slug?: string; 
}

export default async function MapPage() {
  const client = getClient();

  // Pobieramy sklepy
  const { data: storesData } = await client.query<{ stores: Store[] }>({
    query: allShopsLocationsQuery,
  });

  // Pobieramy produkty wraz ze sklepami
  const { data: productsData } = await client.query<{
    ownProducts: {
      id: string;
      product: { name: string };
      store: { id: string, slug?:string };
    }[];
  }>({
    query: allProductsQueryMap,
  });

  // Mapujemy sklepy do obiektów z dodatkowymi produktami
  const storesWithProducts = storesData.stores.map((store) => {
    const storeProducts = productsData.ownProducts
      .filter((p) => p.store.id === store.id || p.store.slug === store.name) // dopasuj wg slug albo id
      .map((p) => p.product.name);

    // Unikalne nazwy produktów i posortowane
    const uniqueProductNames = Array.from(new Set(storeProducts)).sort();

    return {
      ...store,
      products: uniqueProductNames,
    };
  });

  return <MapClientPage stores={storesWithProducts} />;
}
