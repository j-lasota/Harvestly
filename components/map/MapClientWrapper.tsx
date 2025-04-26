"use client";

import dynamic from "next/dynamic";

const Map = dynamic(() => import("./Map"), {
  ssr: false,
  loading: () => <div className="h-full w-full bg-gray-100" />,
});

const MapClientWrapper = () => {
  const markerInfo = "Warszawa";

  return (
    <div className="absolute top-0 right-0 bottom-0 left-0">
      <Map center={[52.2297, 21.0122]} zoom={13} markerText={markerInfo} />
    </div>
  );
};

export default MapClientWrapper;
