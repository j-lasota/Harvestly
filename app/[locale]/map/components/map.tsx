"use client";

import "leaflet-routing-machine/dist/leaflet-routing-machine.css";
import { useEffect, useRef, useState } from "react";
import { Icon, LatLngTuple, Map } from "leaflet";
import { useTranslations } from "next-intl";
import { Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import { X } from "lucide-react";
import Image from "next/image";
import Link from "next/link";
import {
  MapContainer,
  TileLayer,
  Marker,
  useMap,
  ZoomControl,
} from "react-leaflet";

import { Button } from "@/components/ui/button";
import { getShortTime } from "@/lib/utils";
import { recordEvent } from "@/utils/api";
import { Store } from "../page";
import Routing from "./routing";

import placeholder from "@/public/store_placeholder.jpg";

function calculateDistanceKm(
  [lat1, lon1]: LatLngTuple,
  [lat2, lon2]: LatLngTuple
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
  center?: LatLngTuple;
  zoom?: number;
  markers?: Store[];
  selectedStore?: Store | null;
  searchedLocation?: LatLngTuple | null;
  selectedProduct: string;
}

const storeIcon = new Icon({
  iconUrl: "/marker.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

const verfStoreIcon = new Icon({
  iconUrl: "/marker-verf.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

const selectedStoreIcon = new Icon({
  iconUrl: "/marker-select.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

const InteractiveMap = ({
  center = [52.2297, 21.0122] as LatLngTuple, // Warszawa
  zoom = 6,
  markers = [],
  selectedStore,
  searchedLocation,
  selectedProduct,
}: MapProps) => {
  const t = useTranslations("");
  const [currentShop, setCurrentShop] = useState<Store | null>(
    selectedStore ?? null
  );
  const mapRef = useRef<Map | null>(null);
  const [isMobile, setIsMobile] = useState(false);

  const distanceKm =
    searchedLocation && currentShop
      ? calculateDistanceKm(searchedLocation, [
          currentShop.latitude,
          currentShop.longitude,
        ])
      : null;

  useEffect(() => {
    const checkIfMobile = () => setIsMobile(window.innerWidth < 768);
    checkIfMobile();

    window.addEventListener("resize", checkIfMobile);
    return () => window.removeEventListener("resize", checkIfMobile);
  }, []);

  const handleStoreSelect = (store: Store) => {
    setCurrentShop(store);

    if (store.slug) {
      recordEvent(store.slug, "MAP_PIN");
    }
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

  useEffect(() => {
    if (selectedStore) {
      setCurrentShop(selectedStore);
    }
  }, [selectedStore]);

  return (
    <>
      <MapContainer
        center={center}
        zoom={zoom}
        scrollWheelZoom={true}
        attributionControl={false}
        zoomControl={false}
        className="relative z-0 size-full rounded-lg"
      >
        <SetMapRef />

        <ZoomControl position="bottomright" />

        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
        />

        {markers
          ?.filter(
            (store) =>
              selectedProduct === "" ||
              store.products?.some((product) =>
                product.toLowerCase().includes(selectedProduct.toLowerCase())
              )
          )
          .map((store) => {
            const hasProduct = selectedProduct
              ? store.products?.some((p) =>
                  p.toLowerCase().includes(selectedProduct.toLowerCase())
                )
              : false;

            const icon =
              currentShop?.id === store.id
                ? selectedStoreIcon
                : hasProduct
                  ? selectedStoreIcon
                  : store.verified
                    ? verfStoreIcon
                    : storeIcon;

            return (
              <Marker
                key={store.id}
                position={[store.latitude, store.longitude]}
                icon={icon}
                eventHandlers={{
                  click: () => handleStoreSelect(store),
                }}
              >
                <Popup className="text-foreground">
                  {t("page.map.popup.storeLocation", {
                    storeName: store.name,
                    city: store.city,
                  })}
                </Popup>
              </Marker>
            );
          })}

        {searchedLocation && (
          <Marker position={searchedLocation} icon={selectedStoreIcon}>
            <Popup>{t("page.map.popup.searchedAddress")}</Popup>
          </Marker>
        )}

        {searchedLocation && currentShop && (
          <Routing
            from={searchedLocation}
            to={[currentShop.latitude, currentShop.longitude]}
          />
        )}
      </MapContainer>

      {/* Panel z informacjami o sklepie */}
      {currentShop && (
        <section
          className={`bg-background-elevated border-shadow text-foreground ring-ring absolute z-10 flex flex-col gap-4 overflow-hidden rounded-lg border-r-3 border-b-4 px-6 py-5 shadow-md ring backdrop-blur-sm transition-all duration-300 ease-in-out ${
            isMobile
              ? `right-4 bottom-4 left-4 h-1/2 max-h-96 ${
                  currentShop
                    ? "translate-y-0 opacity-100"
                    : "pointer-events-none translate-y-10 opacity-0"
                }`
              : `top-7 right-4 h-[85%] w-md max-w-[30vw] ${
                  currentShop
                    ? "translate-x-0 opacity-100"
                    : "pointer-events-none translate-x-10 opacity-0"
                }`
          }`}
        >
          <h2 className="text-foreground before:bg-ring relative w-fit pb-2 text-2xl font-bold before:absolute before:bottom-0 before:left-0 before:h-1 before:w-40 before:content-['']">
            {currentShop.name}
          </h2>

          <Button
            onClick={() => {
              setCurrentShop(null);
              if (mapRef.current && center) {
                mapRef.current.setView(center, zoom, { animate: true });
              }
            }}
            size="icon"
            variant="ghostPrimary"
            className="absolute top-4 right-4"
          >
            <X size={24} strokeWidth={1.75} />
          </Button>

          <div className="scrollbar-custom flex flex-1 flex-col gap-4 overflow-y-auto px-1 py-5">
            <Image
              src={currentShop.imageUrl ? currentShop.imageUrl : placeholder}
              alt={`Image of ${currentShop.name}`}
              width={700}
              height={400}
              className="max-h-60 w-full rounded-lg object-cover"
            />

            <Button
              asChild
              onClick={() => {
                recordEvent(currentShop.slug!, "STORE_PAGE");
              }}
            >
              <Link href={`/store/${currentShop.slug}`}>
                {t("page.map.storeInfo.action")}
              </Link>
            </Button>

            {currentShop.description && (
              <div>
                <h3 className="text-foreground text-lg font-semibold">
                  {t("page.map.storeInfo.description")}
                </h3>
                <p className="text-sm">
                  {currentShop.description ||
                    t("page.map.storeInfo.noDescription")}
                </p>
              </div>
            )}

            {currentShop.address && (
              <div>
                <h3 className="text-foreground text-lg font-semibold">
                  {t("page.map.storeInfo.address")}
                </h3>
                <p className="text-sm">{currentShop.address}</p>
              </div>
            )}

            {distanceKm && (
              <div>
                <h3 className="text-foreground text-lg font-semibold">
                  {t("page.map.storeInfo.distance")}
                </h3>
                <p className="text-sm">{distanceKm} km</p>
              </div>
            )}

            {currentShop.businessHours &&
              currentShop.businessHours.length > 0 && (
                <div>
                  <h3 className="text-foreground text-lg font-semibold">
                    {t("page.map.storeInfo.businessHours")}
                  </h3>
                  {currentShop.businessHours.length > 0 ? (
                    <ul className="text-sm">
                      {currentShop.businessHours.map((d) => (
                        <li
                          key={d.dayOfWeek}
                          className="flex justify-between gap-4"
                        >
                          <p className="font-medium">
                            {t(`days.${d.dayOfWeek}`)}
                          </p>
                          <p className="text-foreground/80">
                            {getShortTime(d.openingTime)} -{" "}
                            {getShortTime(d.closingTime)}
                          </p>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-sm">
                      Brak informacji o godzinach otwarcia.
                    </p>
                  )}
                </div>
              )}
          </div>
        </section>
      )}
    </>
  );
};

export default InteractiveMap;
