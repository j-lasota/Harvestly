import { allShopsLocationsQuery } from "@/graphql/query";
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
}

export default async function MapPage() {
  const { data } = await getClient().query<{ stores: Store[] }>({
    query: allShopsLocationsQuery,
  });

  return <MapClientPage stores={data.stores} />;
}
