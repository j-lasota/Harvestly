'use client';

import dynamic from 'next/dynamic';

const Map = dynamic(() => import('./Map'), {
  ssr: false,
});

const MapClientWrapper = () => {
  return <Map markerText="MAM TAK SAMO JAK TY. MIASTO MOJE A W NIM..." />;
};

export default MapClientWrapper;