"use client";

import { gql, useQuery } from "@apollo/client";
import { useTranslations } from "next-intl";
import dynamic from "next/dynamic";
import { useState } from "react";

import { Input } from "@/components/ui/input";

const Map = dynamic(() => import("@/components/map/Map"), { ssr: false });

const SHOPS_LOCATIONS_QUERY = gql`
  query {
    stores {
      id
      latitude
      longitude
      name
      city
      description
      address
      imageUrl
      businessHours {
        dayOfWeek
        openingTime
        closingTime
      }
    }
  }
`;

type StoreLocation = {
  id: string;
  latitude: number;
  longitude: number;
  name: string;
  city: string;
  description?: string;
  address: string;
  imageUrl?: string;
};

const Page = () => {
  const t = useTranslations();
  const { data, loading, error } = useQuery<{ stores: StoreLocation[] }>(
    SHOPS_LOCATIONS_QUERY
  );
  const stores = data?.stores ?? [];

  const [mapCenter, setMapCenter] = useState<[number, number] | undefined>(
    undefined
  );
  const [mapZoom, setMapZoom] = useState(7);
  const [cityInput, setCityInput] = useState("");
  const [selectedStore, setSelectedStore] = useState<StoreLocation | null>(
    null
  );
  const [filteredStores, setFilteredStores] = useState<StoreLocation[]>(stores);

  const handleShopChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedId = e.target.value;
    const store = stores.find((s) => s.id === selectedId);
    if (store) {
      setSelectedStore(store);
      setMapCenter([store.latitude, store.longitude]);
      setMapZoom(13);
    }
  };

  const handleCitySearch = (input: string) => {
    setCityInput(input);
    const cityShops = stores.filter((store) =>
      store.city.toLowerCase().includes(input.trim().toLowerCase())
    );
    setFilteredStores(cityShops);

    if (cityShops.length > 0) {
      const avgLat =
        cityShops.reduce((sum, s) => sum + s.latitude, 0) / cityShops.length;
      const avgLng =
        cityShops.reduce((sum, s) => sum + s.longitude, 0) / cityShops.length;
      setMapCenter([avgLat, avgLng]);
      setMapZoom(13);
    } else {
      setMapCenter(undefined);
      setMapZoom(7);
    }
  };

  return (
    <div className="relative h-[calc(100vh-4rem)] w-full md:h-[calc(100vh-5rem)]">
      {loading && (
        <div className="absolute top-4 left-4 z-50 rounded bg-yellow-100 p-2 text-yellow-800 shadow">
          {t("map.loading")}
        </div>
      )}
      {error && (
        <div className="absolute top-4 left-4 z-50 rounded bg-red-100 p-2 text-red-800 shadow">
          {t("map.loadingError")}: {error.message}
        </div>
      )}
      <div className="bg-background-elevated absolute top-0 left-0 z-50 h-auto w-72 p-3 shadow-md">
        <label htmlFor="storeSelect" className="mb-1 block text-sm font-medium">
          {t("map.selectShop")}
        </label>
        <select
          id="storeSelect"
          className="w-full rounded border px-2 py-1 text-sm"
          value={selectedStore?.id || ""}
          onChange={handleShopChange}
        >
          <option value="">-- {t("map.selectShopPlaceholder")} --</option>
          {stores.map((store) => (
            <option key={store.id} value={store.id}>
              {store.name} – {store.city}
            </option>
          ))}
        </select>

        <div className="mt-4">
          <label htmlFor="cityInput" className="mb-1 block text-sm font-medium">
            {t("map.searchCity")}
          </label>
          <div className="flex">
            <Input
              id="cityInput"
              type="text"
              className="w-full rounded border px-2 py-1 text-sm"
              value={cityInput}
              onChange={(e) => handleCitySearch(e.target.value)}
              placeholder={t("map.cityPlaceholder")}
            />
          </div>
        </div>

        {cityInput && filteredStores.length === 0 && (
          <p className="mt-2 text-sm text-red-500">{t("map.noShopsInCity")}</p>
        )}
      </div>

      <Map
        markers={filteredStores}
        center={mapCenter}
        zoom={mapZoom}
        selectedStore={selectedStore}
      />
    </div>
  );
};

export default Page;
