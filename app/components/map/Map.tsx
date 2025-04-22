"use client";

import { useEffect, useRef, useState } from "react";
import { MapContainer, TileLayer, Marker, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

interface MapProps {
  center?: [number, number];
  zoom?: number;
  markerText?: string;
}

delete (L.Icon.Default.prototype as any)._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl: require("leaflet/dist/images/marker-icon-2x.png"),
  iconUrl: require("leaflet/dist/images/marker-icon.png"),
  shadowUrl: require("leaflet/dist/images/marker-shadow.png"),
});

const Map = ({
  center = [52.2297, 21.0122],
  zoom = 7,
  markerText,
}: MapProps) => {
  const [isPanelOpen, setIsPanelOpen] = useState(false);
  const mapRef = useRef<L.Map | null>(null);

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
      const offsetX = mapSize.x / 5;
      const point = map.project(center, map.getZoom());
      point.x += offsetX;
      const newCenter = map.unproject(point, map.getZoom());
      map.panTo(newCenter, { animate: true });
    } else {
      map.panTo(center, { animate: true });
    }
  }, [isPanelOpen, center]);

  return (
    <div className="w-full h-full relative">
      <MapContainer
        center={center}
        zoom={zoom}
        scrollWheelZoom={true}
        zoomControl={false}
        className="w-full h-full z-0"
      >
        <SetMapRef />
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {markerText && (
          <Marker
            position={center}
            eventHandlers={{
              click: handleMarkerClick,
            }}
          />
        )}

        <div
          className={`absolute top-7 right-4 z-[1000] w-1/3 h-[90%] rounded-xl border border-[#b3ab9a] bg-[#f6e9d2] text-black shadow-lg overflow-hidden transition-all duration-300 ease-in-out ${
            isPanelOpen
              ? "opacity-100 translate-x-0"
              : "opacity-0 translate-x-10 pointer-events-none"
          }`}
        >
          {isPanelOpen && (
            <div className="p-6 h-full flex flex-col justify-between">
              <div>
                <h2 className="text-xl font-bold mb-3 border-b-2 border-[#b3ab9a] pb-2">
                  Location Information
                </h2>
                <p className="mb-3 text-base">{markerText}</p>
              </div>
              <button
                onClick={() => setIsPanelOpen(false)}
                className="mt-auto w-full py-2 px-4 bg-[#b3ab9a] text-white font-semibold rounded-md hover:bg-[#9f9682] transition-all"
              >
                Close
              </button>
            </div>
          )}
        </div>
      </MapContainer>
    </div>
  );
};

export default Map;
