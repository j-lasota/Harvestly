'use client';

import 'leaflet-routing-machine/dist/leaflet-routing-machine.css';
import { useMap } from 'react-leaflet';
import { useEffect } from 'react';
import 'leaflet-routing-machine';
import L from 'leaflet';

interface RoutingProps {
  from: [number, number];
  to: [number, number];
  mode?: 'car' | 'foot';
}

const Routing = ({ from, to, mode = 'foot' }: RoutingProps) => {
  const map = useMap();

  useEffect(() => {
    if (!map || !map._loaded) return;

    const routingControl = L.Routing.control({
      waypoints: [L.latLng(...from), L.latLng(...to)],
      routeWhileDragging: false,
      addWaypoints: false,
      draggableWaypoints: false,
      createMarker: () => null,
      show: false,
      lineOptions: {
        styles: [{ color: mode === 'car' ? 'blue' : 'green', opacity: 0.7, weight: 5 }],
      },
      router: new L.Routing.OSRMv1({
        serviceUrl: 'https://router.project-osrm.org/route/v1',
        profile: mode === 'car' ? 'car' : 'foot',
      }),
    }).addTo(map);

    return () => {
      map.removeControl(routingControl);
    };
  }, [from, to, mode, map]);

  return null;
};

export default Routing;
