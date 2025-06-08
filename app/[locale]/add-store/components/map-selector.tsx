"use client";

import { Icon, LatLngTuple } from "leaflet";
import React, { useState } from "react";
import "leaflet/dist/leaflet.css";
import {
  MapContainer,
  TileLayer,
  Marker,
  useMapEvents,
  ZoomControl,
} from "react-leaflet";

import SearchBox from "./searchbox";

const storeIcon = new Icon({
  iconUrl: "/marker.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

interface MapProps {
  initialPosition?: LatLngTuple;
  zoom?: number;
  onChange: (coords: { lat: number; lng: number }) => void;
}

const ClickHandler: React.FC<{
  onClick: (latlng: { lat: number; lng: number }) => void;
}> = ({ onClick }) => {
  useMapEvents({
    click(e) {
      onClick(e.latlng);
    },
  });
  return null;
};

const InteractiveMap: React.FC<MapProps> = ({
  initialPosition = [52.2297, 21.0122] as LatLngTuple, // Warszawa
  zoom = 16,
  onChange,
}) => {
  const [markerPosition, setMarkerPosition] = useState<LatLngTuple | null>(
    null
  );

  const handleMapClick = (latlng: { lat: number; lng: number }) => {
    const newPos: LatLngTuple = [latlng.lat, latlng.lng];
    setMarkerPosition(newPos);
    onChange(latlng);
  };

  return (
    <MapContainer
      center={initialPosition}
      zoom={zoom}
      scrollWheelZoom={true}
      attributionControl={false}
      zoomControl={false}
      className="relative z-1 size-full rounded-lg"
    >
      <SearchBox />
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
      />
      <ClickHandler onClick={handleMapClick} />
      {markerPosition && <Marker position={markerPosition} icon={storeIcon} />}
      <ZoomControl position="bottomright" />
    </MapContainer>
  );
};

export default InteractiveMap;
