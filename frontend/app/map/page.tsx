import MapClientWrapper from '../components/map/MapClientWrapper';
import Navbar from '../components/Navbar';

export default function MapView() {
  return (
    <main className="w-full h-screen">
        <Navbar />
      <MapClientWrapper />
    </main>
  );
}
