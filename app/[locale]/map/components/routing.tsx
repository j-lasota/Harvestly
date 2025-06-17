"use client";

import "leaflet-routing-machine/dist/leaflet-routing-machine.css";
import { useEffect, useRef, useState } from "react";
import L, { LatLngTuple } from "leaflet";
import { useMap } from "react-leaflet";
import "leaflet-routing-machine";

interface RoutingProps {
  from: LatLngTuple;
  to: LatLngTuple;
}

const Routing = ({ from, to }: RoutingProps) => {
  const map = useMap();
  const routingControlRef = useRef<L.Routing.Control | null>(null);
  const [routing, setRouting] = useState<L.Routing.Control | null>(null);

  useEffect(() => {
    if (!map || !from || !to) return;
    const waypoints = [L.latLng(...from), L.latLng(...to)];

    routingControlRef.current = new L.Routing.Control({
      waypoints: waypoints,
      routeWhileDragging: false,
      addWaypoints: false,
      show: false,
      router: new L.Routing.OSRMv1({
        serviceUrl: "https://router.project-osrm.org/route/v1",
      }),
      plan: L.Routing.plan(waypoints, {
        draggableWaypoints: false,
        createMarker: () => false,
      }),
    });

    setRouting(routingControlRef.current);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [map]);

  useEffect(() => {
    if (routing) {
      const waypoints = [L.latLng(...from), L.latLng(...to)];

      routing.addTo(map);
      routing.setWaypoints(waypoints);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [routing, from, to]);

  return null;
};

export default Routing;
