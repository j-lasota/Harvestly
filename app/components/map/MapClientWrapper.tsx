'use client';

import dynamic from 'next/dynamic';

const Map = dynamic(() => import('./Map'), {
  ssr: false,
});

const MapClientWrapper = () => {
  const markerInfo = "MAM TAK SAMO JAK TY. MIASTO MOJE A W NIM...";
  
  return (
    <div className="w-full h-full">
      <Map 
        center={[52.2297, 21.0122]} 
        zoom={13} 
        markerText={markerInfo} 
      />
    </div>
  );
};

export default MapClientWrapper;