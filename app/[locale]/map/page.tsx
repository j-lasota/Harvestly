'use client';

import dynamic from 'next/dynamic';
import { gql, useQuery } from '@apollo/client';
import { useState } from 'react';

const Map = dynamic(() => import('@/components/map/Map'), { ssr: false });

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
  const { data, loading, error } = useQuery<{ stores: StoreLocation[] }>(SHOPS_LOCATIONS_QUERY);
const stores = data?.stores ?? [];

  const [mapCenter, setMapCenter] = useState<[number, number] | undefined>(undefined);
  const [mapZoom, setMapZoom] = useState(7);
  const [cityInput, setCityInput] = useState('');
  const [selectedStore, setSelectedStore] = useState<StoreLocation | null>(null);
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
    const cityShops = stores.filter(
      (store) => store.city.toLowerCase().includes(input.trim().toLowerCase())
    );
    setFilteredStores(cityShops);

    if (cityShops.length > 0) {
      const avgLat = cityShops.reduce((sum, s) => sum + s.latitude, 0) / cityShops.length;
      const avgLng = cityShops.reduce((sum, s) => sum + s.longitude, 0) / cityShops.length;
      setMapCenter([avgLat, avgLng]);
      setMapZoom(13);
    } else {
      setMapCenter(undefined);
      setMapZoom(7);
    }
  };

  return (
    <div className="w-full h-screen relative">
      {loading && (
        <div className="absolute top-4 left-4 bg-yellow-100 text-yellow-800 p-2 rounded z-50 shadow">
          Ładowanie danych sklepów...
        </div>
      )}
      {error && (
        <div className="absolute top-4 left-4 bg-red-100 text-red-800 p-2 rounded z-50 shadow">
          Nie udało się załadować danych sklepów: {error.message}
        </div>
      )}

      <div className="absolute top-4 left-4 z-50 bg-white shadow-md rounded p-3 w-72">
        <label htmlFor="storeSelect" className="block text-sm font-medium mb-1">
          Wybierz stanowisko:
        </label>
        <select
          id="storeSelect"
          className="border px-2 py-1 rounded w-full text-sm"
          value={selectedStore?.id || ''}
          onChange={handleShopChange}
        >
          <option value="">-- Wybierz sklep --</option>
          {stores.map((store) => (
            <option key={store.id} value={store.id}>
              {store.name} – {store.city}
            </option>
          ))}
        </select>

        <div className="mt-4">
          <label htmlFor="cityInput" className="block text-sm font-medium mb-1">
            Przenieś mapę do miasta:
          </label>
          <div className="flex">
            <input
              id="cityInput"
              type="text"
              className="border px-2 py-1 rounded w-full text-sm"
              value={cityInput}
              onChange={(e) => handleCitySearch(e.target.value)}
              placeholder="Wpisz nazwę miasta"
            />
          </div>
        </div>
        

        {cityInput && filteredStores.length === 0 && (
          <p className="mt-2 text-red-500 text-sm">Brak sklepów w tym mieście</p>
        )}
      </div>

      <Map markers={filteredStores} center={mapCenter} zoom={mapZoom} selectedStore={selectedStore} />
    </div>
  );
};

export default Page;
