"use client";

import { MapContainer, TileLayer, Marker, useMap } from "react-leaflet";
import "leaflet-routing-machine/dist/leaflet-routing-machine.css";
import { useEffect, useRef, useState } from "react";
import { Input } from "@/components/ui/input";
import { useTranslations } from "next-intl";
import { Popup } from "react-leaflet";
import dynamic from "next/dynamic";
import "leaflet/dist/leaflet.css";
import { Icon } from "leaflet";

import { Store } from "../page";
import Image from "next/image";

function calculateDistanceKm(
  [lat1, lon1]: [number, number],
  [lat2, lon2]: [number, number]
): number {
  const R = 6371;
  const dLat = (lat2 - lat1) * (Math.PI / 180);
  const dLon = (lon2 - lon1) * (Math.PI / 180);
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(lat1 * (Math.PI / 180)) *
      Math.cos(lat2 * (Math.PI / 180)) *
      Math.sin(dLon / 2) ** 2;

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return +(R * c).toFixed(2);
}

interface MapProps {
  center?: [number, number];
  zoom?: number;
  markers?: Store[];
  selectedStore?: Store | null;
}

const Routing = dynamic(() => import("./routing"), { ssr: false });

const storeIcon = new Icon({
  iconUrl: "/marker.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

const selectedStoreIcon = new Icon({
  iconUrl: "/marker_red.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

const Map = ({
  center = [52.2297, 21.0122],
  zoom = 7,
  markers = [],
  selectedStore,
}: MapProps) => {
  const t = useTranslations("page.map");
  const [currentShop, setCurrentShop] = useState<Store | null>(
    selectedStore ?? null
  );
  const [searchedLocation, setSearchedLocation] = useState<
    [number, number] | null
  >(null);
  const mapRef = useRef<L.Map | null>(null);
  const [isMobile, setIsMobile] = useState(false);

  const distanceKm =
    searchedLocation && currentShop
      ? calculateDistanceKm(searchedLocation, [
          currentShop.latitude,
          currentShop.longitude,
        ])
      : null;

  const searchAddress = async (address: string) => {
    if (!mapRef.current) return;

    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}`
      );
      const data = await response.json();

      if (data.length > 0) {
        const { lat, lon } = data[0];
        setCurrentShop(null);
        setSearchedLocation([parseFloat(lat), parseFloat(lon)]); // ✅ Ustawienie szukanego adresu
        mapRef.current.setView([parseFloat(lat), parseFloat(lon)], 15, {
          animate: true,
        });
      } else {
        alert(t("addressNotFound"));
      }
    } catch (error) {
      console.error(t("searchError"), error);
    }
  };

  useEffect(() => {
    const checkIfMobile = () => setIsMobile(window.innerWidth < 768);
    checkIfMobile();
    window.addEventListener("resize", checkIfMobile);
    return () => window.removeEventListener("resize", checkIfMobile);
  }, []);

  const handleMarkerClick = (store: Store) => {
    setCurrentShop(store);
  };

  const SetMapRef = () => {
    const map = useMap();
    useEffect(() => {
      mapRef.current = map;
    }, [map]);
    return null;
  };

  useEffect(() => {
    if (!mapRef.current || !center) return;
    mapRef.current.setView(center, zoom);
  }, [center, zoom]);

  useEffect(() => {
    if (!mapRef.current) return;
    const map = mapRef.current;
    if (currentShop) {
      const mapSize = map.getSize();
      const offsetX = isMobile ? 0 : mapSize.x / 5;
      const offsetY = isMobile ? mapSize.y / 4 : 0;

      const point = map.project(
        [currentShop.latitude, currentShop.longitude],
        map.getZoom()
      );
      point.x += offsetX;
      point.y += offsetY;
      const newCenter = map.unproject(point, map.getZoom());
      map.panTo(newCenter, { animate: true });
    }
  }, [currentShop, isMobile]);

  return (
    <>
      {/* <div className="bg-background rounded-bottom absolute top-33 left-0 z-[1000] w-72 p-2 shadow-md">
        <Input
          type="text"
          placeholder="Wpisz adres..."
          className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none"
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              const address = (e.target as HTMLInputElement).value;
              if (address) searchAddress(address);
            }
          }}
          onChange={(e) => {
            const value = e.target.value.trim();
            if (value === "") {
              setSearchedLocation(null);
            }
          }}
        />
      </div> */}

      <MapContainer
        center={center}
        zoom={zoom}
        scrollWheelZoom={true}
        zoomControl={false}
        className="relative z-0 size-full rounded-lg"
      >
        <SetMapRef />
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
        />

        {markers?.map((store) => (
          <Marker
            key={store.id}
            position={[store.latitude, store.longitude]}
            icon={currentShop?.id === store.id ? selectedStoreIcon : storeIcon}
            eventHandlers={{
              click: () => handleMarkerClick(store),
            }}
          >
            <Popup>
              {t("popup.storeLocation", {
                storeName: store.name,
                city: store.city,
              })}
            </Popup>
          </Marker>
        ))}

        {searchedLocation && (
          <Marker position={searchedLocation} icon={selectedStoreIcon}>
            <Popup>{t("popup.searchedAddress")}</Popup>
          </Marker>
        )}

        {searchedLocation && currentShop && mapRef.current && (
          <Routing
            from={searchedLocation}
            to={[currentShop.latitude, currentShop.longitude]}
          />
        )}

        {/* Panel z informacjami o sklepie */}
        <div
          className={`absolute z-[1000] overflow-hidden rounded-xl border-2 border-[#d4c9b1] bg-[#f9f5eb] text-[#333] shadow-xl backdrop-blur-sm transition-all duration-300 ease-in-out ${
            isMobile
              ? `right-4 bottom-4 left-4 h-1/2 max-h-96 ${currentShop ? "translate-y-0 opacity-100" : "pointer-events-none translate-y-10 opacity-0"}`
              : `top-7 right-4 h-[85%] w-96 max-w-[30vw] ${currentShop ? "translate-x-0 opacity-100" : "pointer-events-none translate-x-10 opacity-0"}`
          }`}
        >
          {currentShop && (
            <div className="flex h-full flex-col p-6">
              <div className="mb-4 flex items-start justify-between">
                <h2 className="border-b-2 border-[#d4c9b1] pb-2 text-2xl font-bold text-[#5a4a3a]">
                  {currentShop.name}
                </h2>
                <button
                  onClick={() => {
                    setCurrentShop(null);
                    if (mapRef.current && center) {
                      mapRef.current.setView(center, zoom, { animate: true });
                    }
                  }}
                  className="text-[#7a6b5a] transition-colors hover:text-[#5a4a3a]"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="h-6 w-6"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M6 18L18 6M6 6l12 12"
                    />
                  </svg>
                </button>
              </div>
              <div className="flex-1 space-y-4 overflow-y-auto pr-2">
                <div>
                  <h3 className="mb-1 text-lg font-semibold text-[#5a4a3a]">
                    {t("storeInfo.description")}
                  </h3>
                  <p className="text-sm">
                    {currentShop.description || t("storeInfo.noDescription")}
                  </p>
                </div>
                <div>
                  <h3 className="mb-1 text-lg font-semibold text-[#5a4a3a]">
                    {t("storeInfo.address")}
                  </h3>
                  <p className="text-sm">{currentShop.address}</p>
                </div>
                {distanceKm && (
                  <div>
                    <h3 className="mb-1 text-lg font-semibold text-[#5a4a3a]">
                      Odległość
                    </h3>
                    <p className="text-sm">{distanceKm} km</p>
                  </div>
                )}

                <div>
                  <h3 className="mb-1 text-lg font-semibold text-[#5a4a3a]">
                    Godziny otwarcia
                  </h3>
                  {currentShop.businessHours &&
                  currentShop.businessHours.length > 0 ? (
                    <ul className="text-sm">
                      {currentShop.businessHours.map((d) => (
                        <li key={d.dayOfWeek}>
                          {d.dayOfWeek}: {d.openingTime} - {d.closingTime}
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-sm">
                      Brak informacji o godzinach otwarcia.
                    </p>
                  )}
                </div>

                <div>
                  <h3 className="mb-1 text-lg font-semibold text-[#5a4a3a]">
                    Zdjęcie
                  </h3>
                  <Image
                    src={
                      currentShop.imageUrl
                        ? currentShop.imageUrl
                        : "/store_placeholder.jpg"
                    }
                    alt={currentShop.name}
                    className="mt- max-h-60 w-full rounded-lg object-cover"
                    width={700}
                    height={400}
                  />
                </div>
              </div>
            </div>
          )}
        </div>
      </MapContainer>
    </>
  );
};

export default Map;
