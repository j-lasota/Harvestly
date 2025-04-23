import MapClientWrapper from "@/app/components/map/MapClientWrapper";

export default function MapView() {
  return (
    <main className="flex h-screen w-full flex-col overflow-hidden">
      <div className="flex-1">
        <MapClientWrapper />
      </div>
    </main>
  );
}
