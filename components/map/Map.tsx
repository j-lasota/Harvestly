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

const customIcon = new L.Icon({
  iconUrl: '/marker.png',
    iconSize: [32, 32],        
    iconAnchor: [16, 32],     
    popupAnchor: [0, -32]     
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
    window.addEventListener('resize', checkIfMobile);
    return () => window.removeEventListener('resize', checkIfMobile);
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
    <div className="w-full h-full relative">
      <MapContainer
        center={center}
        zoom={zoom}
        scrollWheelZoom={true}
        zoomControl={false}
        className="w-full h-full z-0 rounded-xl overflow-hidden shadow-lg"
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
          >
          </Marker>
        )}

        <div
          className={`
            absolute z-[1000] rounded-xl border-2 border-[#d4c9b1] bg-[#f9f5eb] text-[#333] shadow-xl
            overflow-hidden transition-all duration-300 ease-in-out backdrop-blur-sm
            ${
              isMobile 
                ? `bottom-4 left-4 right-4 h-1/2 max-h-96 ${
                    isPanelOpen
                      ? "opacity-100 translate-y-0"
                      : "opacity-0 translate-y-10 pointer-events-none"
                  }`
                : `top-7 right-4 w-96 h-[85%] max-w-[30vw] ${
                    isPanelOpen
                      ? "opacity-100 translate-x-0"
                      : "opacity-0 translate-x-10 pointer-events-none"
                  }`
            }
          `}
        >
          {isPanelOpen && (
            <div className="p-6 h-full flex flex-col">
              <div className="flex justify-between items-start mb-4">
                <h2 className="text-2xl font-bold text-[#5a4a3a] border-b-2 border-[#d4c9b1] pb-2">
                  Location Details
                </h2>
                <button
                  onClick={() => setIsPanelOpen(false)}
                  className="text-[#7a6b5a] hover:text-[#5a4a3a] transition-colors"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              
              <div className="flex-1 overflow-y-auto pr-2">
                <div className="mb-6">
                  <div className="flex items-center gap-3 mb-3">
                    <div className="bg-[#e8d9c5] p-2 rounded-full">
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-[#7a6b5a]" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clipRule="evenodd" />
                      </svg>
                    </div>
                    <h3 className="font-semibold text-lg text-[#5a4a3a]">Location</h3>
                  </div>
                  <p className="text-base pl-11">{markerText}</p>
                </div>

                <div className="mb-6">
                  <div className="flex items-center gap-3 mb-3">
                    <div className="bg-[#e8d9c5] p-2 rounded-full">
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-[#7a6b5a]" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z" />
                      </svg>
                    </div>
                    <h3 className="font-semibold text-lg text-[#5a4a3a]">Details</h3>
                  </div>
                  <div className="text-base pl-11 space-y-2">
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
                className="mt-4 w-full py-3 px-4 bg-[#b3ab9a] hover:bg-[#9f9682] text-white font-semibold rounded-lg transition-all flex items-center justify-center gap-2"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
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