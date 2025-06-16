"use client";

import "leaflet-routing-machine/dist/leaflet-routing-machine.css";
import { useEffect, useRef } from "react";
import { useMap } from "react-leaflet";
import "leaflet-routing-machine";
import L from "leaflet";

interface RoutingProps {
  from: [number, number];
  to: [number, number];
}

const Routing = ({ from, to }: RoutingProps) => {
  const map = useMap();
  // Ustawiamy typ na any, bo TS nie ma definicji L.Routing.Control
  const routingControlRef = useRef<any>(null);

  useEffect(() => {
    if (!map || typeof window === "undefined") return;

    if (!from || !to) return;

    if (routingControlRef.current) {
      map.removeControl(routingControlRef.current);
      routingControlRef.current = null;
    }

    const control = (L.Routing as any).control({
      waypoints: [L.latLng(...from), L.latLng(...to)],
      routeWhileDragging: false,
      addWaypoints: false,
      draggableWaypoints: false,
      createMarker: () => null,
      show: false,
      router: new (L.Routing as any).OSRMv1({
        serviceUrl: "https://router.project-osrm.org/route/v1",
      }),
    });

    control.addTo(map);
    routingControlRef.current = control;

    // Usuwanie po odmontowaniu
    return () => {
      if (routingControlRef.current) {
        map.removeControl(routingControlRef.current);
        routingControlRef.current = null;
      }
    };
  }, [map, from, to]);

  return null;
};

export default Routing;
