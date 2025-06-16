import { allShopsLocationsQuery, allProductsQueryMap } from "@/graphql/query";
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
  const { data: storesData } = await getClient().query<{ stores: Store[] }>({
    query: allShopsLocationsQuery,
  });

  const { data: productsData } = await getClient().query<{
    ownProducts: {
      id: string;
      product: { name: string };
      store: { id: string; slug?: string };
    }[];
  }>({
    query: allProductsQueryMap,
  });

  const storesWithProducts = storesData.stores.map((store) => {
    const storeProducts = productsData.ownProducts
      .filter((p) => p.store.id === store.id || p.store.slug === store.name)
      .map((p) => p.product.name);

    const uniqueProductNames = Array.from(new Set(storeProducts)).sort();

    return {
      ...store,
      products: uniqueProductNames,
    };
  });

  return <MapClientPage stores={storesWithProducts} />;
}
