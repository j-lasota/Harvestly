"use client";

import { MapContainer, TileLayer, Marker, useMap } from "react-leaflet";
import { useEffect, useRef, useState } from "react";
import "leaflet/dist/leaflet.css";
import L from "leaflet";

interface MapProps {
  center?: [number, number];
  zoom?: number;
  markerText?: string;
}

const customIcon = new L.Icon({
  iconUrl: "/marker.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

delete (L.Icon.Default.prototype as any)._getIconUrl;

const Map = ({
  center = [52.2297, 21.0122],
  zoom = 7,
  markerText,
}: MapProps) => {
  const [isPanelOpen, setIsPanelOpen] = useState(false);
  const mapRef = useRef<L.Map | null>(null);
  const [isMobile, setIsMobile] = useState(false);

  useEffect(() => {
    const checkIfMobile = () => setIsMobile(window.innerWidth < 768);
    checkIfMobile();
    window.addEventListener("resize", checkIfMobile);
    return () => window.removeEventListener("resize", checkIfMobile);
  }, []);

  const handleMarkerClick = () => {
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
    if (!mapRef.current) return;
    const map = mapRef.current;

    if (isPanelOpen) {
      const mapSize = map.getSize();
      const offsetX = isMobile ? 0 : mapSize.x / 5;
      const offsetY = isMobile ? mapSize.y / 4 : 0;

      const point = map.project(center, map.getZoom());
      point.x += offsetX;
      point.y += offsetY;
      const newCenter = map.unproject(point, map.getZoom());
      map.panTo(newCenter, { animate: true });
    } else {
      map.panTo(center, { animate: true });
    }
  }, [isPanelOpen, center, isMobile]);

  return (
    <div className="relative h-full w-full">
      <MapContainer
        center={center}
        zoom={zoom}
        scrollWheelZoom={true}
        zoomControl={false}
        className="z-0 h-full w-full overflow-hidden rounded-xl shadow-lg"
      >
        <SetMapRef />
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
        />

        {markerText && (
          <Marker
            position={center}
            icon={customIcon}
            eventHandlers={{
              click: handleMarkerClick,
            }}
          ></Marker>
        )}

        <div
          className={`absolute z-[1000] overflow-hidden rounded-xl border-2 border-[#d4c9b1] bg-[#f9f5eb] text-[#333] shadow-xl backdrop-blur-sm transition-all duration-300 ease-in-out ${
            isMobile
              ? `right-4 bottom-4 left-4 h-1/2 max-h-96 ${
                  isPanelOpen
                    ? "translate-y-0 opacity-100"
                    : "pointer-events-none translate-y-10 opacity-0"
                }`
              : `top-7 right-4 h-[85%] w-96 max-w-[30vw] ${
                  isPanelOpen
                    ? "translate-x-0 opacity-100"
                    : "pointer-events-none translate-x-10 opacity-0"
                }`
          } `}
        >
          {isPanelOpen && (
            <div className="flex h-full flex-col p-6">
              <div className="mb-4 flex items-start justify-between">
                <h2 className="border-b-2 border-[#d4c9b1] pb-2 text-2xl font-bold text-[#5a4a3a]">
                  Location Details
                </h2>
                <button
                  onClick={() => setIsPanelOpen(false)}
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

              <div className="flex-1 overflow-y-auto pr-2">
                <div className="mb-6">
                  <div className="mb-3 flex items-center gap-3">
                    <div className="rounded-full bg-[#e8d9c5] p-2">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-5 w-5 text-[#7a6b5a]"
                        viewBox="0 0 20 20"
                        fill="currentColor"
                      >
                        <path
                          fillRule="evenodd"
                          d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z"
                          clipRule="evenodd"
                        />
                      </svg>
                    </div>
                    <h3 className="text-lg font-semibold text-[#5a4a3a]">
                      Location
                    </h3>
                  </div>
                  <p className="pl-11 text-base">{markerText}</p>
                </div>

                <div className="mb-6">
                  <div className="mb-3 flex items-center gap-3">
                    <div className="rounded-full bg-[#e8d9c5] p-2">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-5 w-5 text-[#7a6b5a]"
                        viewBox="0 0 20 20"
                        fill="currentColor"
                      >
                        <path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z" />
                      </svg>
                    </div>
                    <h3 className="text-lg font-semibold text-[#5a4a3a]">
                      Details
                    </h3>
                  </div>
                  <div className="space-y-2 pl-11 text-base">
                    <ul className="list-disc">
                      <li>Local markets available</li>
                      <li>Organic produce</li>
                      <li>Open 7 days a week</li>
                    </ul>
                  </div>
                </div>
              </div>

              <button
                onClick={() => setIsPanelOpen(false)}
                className="mt-4 flex w-full items-center justify-center gap-2 rounded-lg bg-[#b3ab9a] px-4 py-3 font-semibold text-white transition-all hover:bg-[#9f9682]"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-5 w-5"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                >
                  <path
                    fillRule="evenodd"
                    d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                    clipRule="evenodd"
                  />
                </svg>
                Close Panel
              </button>
            </div>
          )}
        </div>
      </MapContainer>
    </div>
  );
};

export default Map;
