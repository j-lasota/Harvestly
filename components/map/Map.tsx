"use client";

import { Popup } from "react-leaflet";
import { useEffect, useRef, useState } from "react";
import { MapContainer, TileLayer, Marker, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { Input } from "../ui/input";
import { useTranslations } from "next-intl";

interface MapProps {
  center?: [number, number];
  zoom?: number;
  markers?: Store[];
  selectedStore?: Store | null;
}

interface Store {
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

interface BusinessHoursProps {
  dayOfWeek: string;
  openingTime: string;
  closingTime: string;
}

const customIcon = new L.Icon({
  iconUrl: "/marker.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

const redIcon = new L.Icon({
  iconUrl: "/marker_red.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

delete (L.Icon.Default.prototype as any)._getIconUrl;

const Map = ({
  center = [52.2297, 21.0122],
  zoom = 7,
  markers = [],
  selectedStore,
}: MapProps) => {
  const t = useTranslations();
  const [isPanelOpen, setIsPanelOpen] = useState(false);
  const [currentShop, setCurrentShop] = useState<Store | null>(selectedStore);
  const [searchedLocation, setSearchedLocation] = useState<
    [number, number] | null
  >(null);
  const mapRef = useRef<L.Map | null>(null);
  const [isMobile, setIsMobile] = useState(false);

  const searchAddress = async (address: string) => {
    if (!mapRef.current) return;

    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}`
      );
      const data = await response.json();

      if (data.length > 0) {
        const { lat, lon } = data[0];
        setSearchedLocation([parseFloat(lat), parseFloat(lon)]);
        mapRef.current.setView([parseFloat(lat), parseFloat(lon)], 15, {
          animate: true,
        });
      } else {
        alert(t("map.addressNotFound"));
      }
    } catch (error) {
      console.error(t("map.searchError"), error);
    }
  };

  useEffect(() => {
    const checkIfMobile = () => setIsMobile(window.innerWidth < 768);
    checkIfMobile();
    window.addEventListener("resize", checkIfMobile);
    return () => window.removeEventListener("resize", checkIfMobile);
  }, []);

  useEffect(() => {
    if (selectedStore) {
      setCurrentShop(selectedStore);
      setIsPanelOpen(true);
    }
  }, [selectedStore]);

  const handleMarkerClick = (store: Store) => {
    setCurrentShop(store);
    setIsPanelOpen(true);
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
    if (currentShop && isPanelOpen) {
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
  }, [isPanelOpen, currentShop, isMobile]);

  return (
    <div className="relative h-full w-full">
      <div className="bg-background-elevated rounded-bottom absolute top-39 left-0 z-[1000] w-72 p-2 shadow-md">
        <Input
          type="text"
          placeholder={t("map.searchPlaceholder")}
          className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none"
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              const address = (e.target as HTMLInputElement).value;
              if (address) searchAddress(address);
            }
          }}
        />
      </div>

      <MapContainer
        center={center}
        zoom={zoom}
        scrollWheelZoom={false}
        zoomControl={false}
        className="z-0 h-full w-full overflow-hidden rounded-xl shadow-lg"
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
            icon={currentShop?.id === store.id ? redIcon : customIcon}
            eventHandlers={{
              click: () => handleMarkerClick(store),
            }}
          >
            <Popup>
              {t("map.popup.storeLocation", {
                storeName: store.name,
                city: store.city,
              })}
            </Popup>
          </Marker>
        ))}

        {searchedLocation && (
          <Marker position={searchedLocation} icon={redIcon}>
            <Popup>{t("map.popup.searchedAddress")}</Popup>
          </Marker>
        )}

        {/* Store information panel */}
        <div
          className={`absolute z-[1000] overflow-hidden rounded-xl border-2 border-[#d4c9b1] bg-[#f9f5eb] text-[#333] shadow-xl backdrop-blur-sm transition-all duration-300 ease-in-out ${
            isMobile
              ? `right-4 bottom-4 left-4 h-1/2 max-h-96 ${isPanelOpen ? "translate-y-0 opacity-100" : "pointer-events-none translate-y-10 opacity-0"}`
              : `top-7 right-4 h-[85%] w-96 max-w-[30vw] ${isPanelOpen ? "translate-x-0 opacity-100" : "pointer-events-none translate-x-10 opacity-0"}`
          }`}
        >
          {isPanelOpen && currentShop && (
            <div className="flex h-full flex-col p-6">
              <div className="mb-4 flex items-start justify-between">
                <h2 className="border-b-2 border-[#d4c9b1] pb-2 text-2xl font-bold text-[#5a4a3a]">
                  {currentShop.name}
                </h2>
                <button
                  onClick={() => {
                    setIsPanelOpen(false);
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
                    {t("map.storeInfo.description")}
                  </h3>
                  <p className="text-sm">
                    {currentShop.description ||
                      t("map.storeInfo.noDescription")}
                  </p>
                </div>
                <div>
                  <h3 className="mb-1 text-lg font-semibold text-[#5a4a3a]">
                    {t("map.storeInfo.address")}
                  </h3>
                  <p className="text-sm">{currentShop.address}</p>
                </div>
                <div>
                  <h3 className="mb-1 text-lg font-semibold text-[#5a4a3a]">
                    {t("store.businessHours")}
                  </h3>
                  {currentShop.businessHours &&
                  currentShop.businessHours.length > 0 ? (
                    <ul className="text-sm">
                      {currentShop.businessHours.map(
                        (d: BusinessHoursProps) => (
                          <li key={d.dayOfWeek}>
                            {t(`store.days.${d.dayOfWeek}`)}: {d.openingTime} -{" "}
                            {d.closingTime}
                          </li>
                        )
                      )}
                    </ul>
                  ) : (
                    <p className="text-sm">{t("map.storeInfo.noHours")}</p>
                  )}
                </div>
                <div>
                  <h3 className="mb-1 text-lg font-semibold text-[#5a4a3a]">
                    {t("map.storeInfo.photo")}
                  </h3>
                  <img
                    src={
                      currentShop.imageUrl
                        ? currentShop.imageUrl
                        : "/store_placeholder.jpg"
                    }
                    alt={currentShop.name}
                    className="mt- max-h-60 w-full rounded-lg object-cover"
                  />
                </div>
              </div>
            </div>
          )}
        </div>
      </MapContainer>
    </div>
  );
};

export default Map;
