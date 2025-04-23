import MapClientWrapper from "../components/map/MapClientWrapper";
import Navbar from "../components/navbar-ds";

export default function MapView() {
  return (
    <main className="w-full h-screen overflow-hidden flex flex-col">
      <Navbar />
      <div className="flex-1">
        <MapClientWrapper />
      </div>
    </main>
  );
}
