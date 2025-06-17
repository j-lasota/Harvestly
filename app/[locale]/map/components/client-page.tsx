"use client";

import { useMemo, useState, useEffect } from "react";
import { useTranslations } from "next-intl";
import { LatLngTuple } from "leaflet";
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

const InteractiveMap = dynamic(() => import("./map"), {
  loading: () => <Loader />,
  ssr: false,
});

const initial = {
  zoom: 10,
  center: undefined,
};

export default function MapClientPage({ stores }: { stores: Store[] }) {
  const t = useTranslations("page.map");
  const [mapCenter, setMapCenter] = useState<LatLngTuple | undefined>(
    initial.center
  );
  const [mapZoom, setMapZoom] = useState<number>(initial.zoom);

  const [cityInput, setCityInput] = useState("");
  const [productInput, setProductInput] = useState("");
  const [selectedStore, setSelectedStore] = useState<Store | null>(null);
  const [filteredStores, setFilteredStores] = useState<Store[]>(stores ?? []);
  const [searchedLocation, setSearchedLocation] = useState<LatLngTuple | null>(
    null
  );

  const allProducts = useMemo(
    () => Array.from(new Set(stores.flatMap((s) => s.products ?? []))).sort(),
    [stores]
  );

  useEffect(() => {
    let filtered = stores;
    if (cityInput) {
      filtered = filtered.filter((store) =>
        store.city.toLowerCase().includes(cityInput.trim().toLowerCase())
      );
    }

    if (productInput) {
      filtered = filtered.filter((store) =>
        store.products?.some((p) =>
          p.toLowerCase().includes(productInput.trim().toLowerCase())
        )
      );
    }
    setFilteredStores(filtered);

    if (filtered.length > 0) {
      const avgLat =
        filtered.reduce((sum, s) => sum + s.latitude, 0) / filtered.length;
      const avgLng =
        filtered.reduce((sum, s) => sum + s.longitude, 0) / filtered.length;
      setMapCenter([avgLat, avgLng]);
      setMapZoom(initial.zoom);
    } else {
      setMapCenter(initial.center);
      setMapZoom(initial.zoom);
    }

    if (selectedStore && !filtered.some((s) => s.id === selectedStore.id)) {
      setSelectedStore(null);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [cityInput, productInput, stores]);

  useEffect(() => {
    if (productInput) {
      const matchingStore = filteredStores.find((store) =>
        store.products?.some((p) =>
          p.toLowerCase().includes(productInput.trim().toLowerCase())
        )
      );

      if (matchingStore) {
        setSelectedStore(matchingStore);
        setMapCenter([matchingStore.latitude, matchingStore.longitude]);
        setMapZoom(13);
      }
    }
  }, [productInput, filteredStores]);

  const handleShopChange = (selectedId: string) => {
    const store = stores.find((s) => s.id === selectedId) || null;

    if (store) {
      setSelectedStore(store);
      setMapCenter([store.latitude, store.longitude]);
      setMapZoom(13);
    }
  };

  const handleProductChange = (value: string) => {
    if (value === "__none") {
      setProductInput("");
    } else {
      setProductInput(value);
    }
  };

  const searchAddress = async (address: string) => {
    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(
          address
        )}`
      );
      const data = await response.json();

      if (data.length > 0) {
        const { lat, lon } = data[0];
        setSelectedStore(null);
        const coords: [number, number] = [parseFloat(lat), parseFloat(lon)];
        setSearchedLocation(coords);
        setMapCenter(coords);
        setMapZoom(15);
      } else {
        alert(t("addressNotFound"));
      }
    } catch (error) {
      console.error(t("searchError"), error);
      alert(t("searchError"));
    }
  };

  return (
    <main className="h-[calc(100vh-4rem-3px)] w-full md:h-[calc(100vh-5rem-3px)]">
      <div className="bg-background-elevated border-shadow ring-ring absolute top-0 left-0 z-10 flex flex-wrap gap-4 rounded-br-lg border-r-3 border-b-4 p-4 ring">
        {/* Wybór stoiska */}
        <label
          htmlFor="storeSelect"
          className="flex flex-col gap-1 text-sm font-medium"
        >
          {t("selectShop")}
          <Select
            value={selectedStore?.id || ""}
            onValueChange={handleShopChange}
          >
            <SelectTrigger className="w-full min-w-48" id="storeSelect">
              <SelectValue placeholder="..." />
            </SelectTrigger>
            <SelectContent>
              {filteredStores.map((store) => (
                <SelectItem key={store.id} value={store.id}>
                  {store.name} - {store.city}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </label>

        {/* Wyszukiwanie miasta */}
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
            onChange={(e) => setCityInput(e.target.value)}
            placeholder={t("cityPlaceholder")}
          />
        </label>

        {/* Wyszukiwanie produktu */}
        <label
          htmlFor="productSelect"
          className="flex flex-col gap-1 text-sm font-medium"
        >
          {t("searchProduct")}
          <Select value={productInput} onValueChange={handleProductChange}>
            <SelectTrigger className="w-full min-w-48" id="productSelect">
              <SelectValue placeholder="..." />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="__none">
                Wyczyść filtr dla produktów
              </SelectItem>
              {allProducts.map((product) => (
                <SelectItem key={product} value={product}>
                  {product}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </label>

        {/* Wyszukiwanie adresu */}
        <label
          htmlFor="addressInput"
          className="flex flex-col gap-1 text-sm font-medium"
        >
          {t("searchAddress")}
          <Input
            id="addressInput"
            type="text"
            className="w-full min-w-60"
            placeholder={t("addressPlaceholder")}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                const address = (e.target as HTMLInputElement).value.trim();
                if (address) searchAddress(address);
              }
            }}
            onChange={(e) => {
              if (e.target.value.trim() === "") setSearchedLocation(null);
            }}
          />
        </label>

        {/* Komunikat brak sklepów */}
        {cityInput && filteredStores.length === 0 && (
          <p className="mt-2 text-sm text-red-500">{t("noShopsInCity")}</p>
        )}
      </div>

      <InteractiveMap
        markers={filteredStores}
        center={mapCenter}
        zoom={mapZoom}
        selectedStore={selectedStore}
        searchedLocation={searchedLocation}
        selectedProduct={productInput}
      />
    </main>
  );
}
