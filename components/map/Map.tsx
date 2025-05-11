'use client';

import { Popup } from 'react-leaflet';
import { useEffect, useRef, useState } from 'react';
import { MapContainer, TileLayer, Marker, useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

interface MapProps {
  center?: [number, number];
  zoom?: number;
  markers?: Store[];
  selectedShop?: Store | null;
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
  const [isPanelOpen, setIsPanelOpen] = useState(false);
  const [currentShop, setCurrentShop] = useState<Store | null>(selectedStore);
  const [searchedLocation, setSearchedLocation] = useState<[number, number] | null>(null);
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
        setSearchedLocation([parseFloat(lat), parseFloat(lon)]); // ✅ Ustawienie szukanego adresu
        mapRef.current.setView([parseFloat(lat), parseFloat(lon)], 15, { animate: true });
      } else {
        alert("Nie znaleziono adresu.");
      }
    } catch (error) {
      console.error("Błąd wyszukiwania adresu:", error);
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

      const point = map.project([currentShop.latitude, currentShop.longitude], map.getZoom());
      point.x += offsetX;
      point.y += offsetY;
      const newCenter = map.unproject(point, map.getZoom());
      map.panTo(newCenter, { animate: true });
    }
  }, [isPanelOpen, currentShop, isMobile]);

  return (
    <div className="relative h-full w-full">
      <div className="absolute top-45 left-4 z-[1000] bg-white p-2 rounded-lg shadow-md">
  <input
    type="text"
    placeholder="Wpisz adres..."
    className="px-3 py-2 rounded-lg border border-gray-300 focus:outline-none w-full"
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
        scrollWheelZoom={true}
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
    <Popup>{store.name} — {store.city}</Popup>
  </Marker>
))}
{searchedLocation && (
  <Marker 
    position={searchedLocation} 
    icon={redIcon}
  >
    <Popup>Szukany adres</Popup>
  </Marker>
)}



        {/* Panel z informacjami o sklepie */}
        <div
  className={`absolute z-[1000] rounded-xl border-2 border-[#d4c9b1] bg-[#f9f5eb] text-[#333] shadow-xl
  overflow-hidden transition-all duration-300 ease-in-out backdrop-blur-sm
  ${isMobile
    ? `bottom-4 left-4 right-4 h-1/2 max-h-96 ${isPanelOpen ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10 pointer-events-none'}`
    : `top-7 right-4 w-96 h-[85%] max-w-[30vw] ${isPanelOpen ? 'opacity-100 translate-x-0' : 'opacity-0 translate-x-10 pointer-events-none'}`
  }`}
>

          {isPanelOpen && currentShop && (
            <div className="p-6 h-full flex flex-col">
              <div className="flex justify-between items-start mb-4">
                <h2 className="text-2xl font-bold text-[#5a4a3a] border-b-2 border-[#d4c9b1] pb-2">
                  {currentShop.name}
                </h2>
                <button
                  onClick={() => {
                    setIsPanelOpen(false)
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
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              <div className="flex-1 overflow-y-auto pr-2 space-y-4">
                <div>
                  <h3 className="font-semibold text-lg text-[#5a4a3a] mb-1">Opis</h3>
                  <p className="text-sm">{currentShop.description ?? 'Brak opisu.'}</p>
                </div>
                <div>
                  <h3 className="font-semibold text-lg text-[#5a4a3a] mb-1">Adres</h3>
                  <p className="text-sm">{currentShop.address}</p>
                </div>
                {currentShop.imageUrl && (
                  <div>
                    <img
                      src={currentShop.imageUrl}
                      alt={currentShop.name}
                      className="rounded-lg max-h-40 object-cover w-full mt-2"
                    />
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </MapContainer>
    </div>
  );
};

export default Map;