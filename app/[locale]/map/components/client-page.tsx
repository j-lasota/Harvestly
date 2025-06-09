"use client";

import { useTranslations } from "next-intl";
import { useMemo, useState } from "react";
import { Loader } from "lucide-react";
import dynamic from "next/dynamic";

import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Store } from "../page";

export default function MapClientPage({ stores }: { stores: Store[] }) {
  const Map = useMemo(
    () =>
      dynamic(() => import("./map"), {
        loading: () => (
          <>
            <Loader />
          </>
        ),
        ssr: false,
      }),
    []
  );

  const t = useTranslations("page.map");
  const [mapCenter, setMapCenter] = useState<[number, number] | undefined>(
    undefined
  );
  const [mapZoom, setMapZoom] = useState(7);
  const [cityInput, setCityInput] = useState("");
  const [selectedStore, setSelectedStore] = useState<Store | null>(null);
  const [filteredStores, setFilteredStores] = useState<Store[]>(stores ?? []);

  const handleShopChange = (selectedId: string) => {
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
    <main className="h-[calc(100vh-4rem-3px)] w-full md:h-[calc(100vh-5rem-3px)]">
      <div className="bg-background-elevated border-shadow ring-ring absolute top-0 left-0 z-50 flex gap-4 rounded-br-lg border-r-3 border-b-4 p-4">
        <label
          htmlFor="storeSelect"
          className="flex flex-col gap-1 text-sm font-medium"
        >
          {t("selectShop")}
          <Select
            value={selectedStore?.id || ""}
            onValueChange={handleShopChange}
          >
            <SelectTrigger className="w-full min-w-48">
              <SelectValue
                placeholder={t("selectShopPlaceholder")}
                id="storeSelect"
              />
            </SelectTrigger>
            <SelectContent>
              {stores.map((store) => (
                <SelectItem key={store.id} value={store.id}>
                  {store.name} - {store.city}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </label>

        <label
          htmlFor="cityInput"
          className="flex flex-col gap-1 text-sm font-medium"
        >
          {t("searchCity")}
          <Input
            id="cityInput"
            type="text"
            className="w-full min-w-48"
            value={cityInput}
            onChange={(e) => handleCitySearch(e.target.value)}
            placeholder={t("cityPlaceholder")}
          />
        </label>

        {cityInput && filteredStores.length === 0 && (
          <p className="mt-2 text-sm text-red-500">{t("noShopsInCity")}</p>
        )}
      </div>

      <Map
        markers={filteredStores}
        center={mapCenter}
        zoom={mapZoom}
        selectedStore={selectedStore}
      />
    </main>
  );
}
