"use client";

import { MapPinned, Store } from "lucide-react";
import { useMemo, useState } from "react";
import Image from "next/image";
import dynamic from "next/dynamic";

import placeholder from "@/public/store_placeholder.jpg";

export const ImageMapPreview = ({
  src,
  name,
  market,
}: {
  src: string | null;
  name: string;
  market: { lat: number; lng: number };
}) => {
  const [showMap, setShowMap] = useState(false);
  const Icon = showMap ? Store : MapPinned;

  const Map = useMemo(
    () =>
      dynamic(() => import("./map"), {
        loading: () => <p>A map is loading</p>,
        ssr: false,
      }),
    []
  );

  return (
    <div className="relative aspect-video w-full overflow-hidden rounded-xl">
      {showMap ? (
        <Map posix={market} />
      ) : (
        <Image
          src={src ?? placeholder}
          alt={`Image of ${name}`}
          width={700}
          height={400}
          className="aspect-video w-full object-cover"
        />
      )}

      <div
        className="absolute right-0 bottom-0 z-10 cursor-pointer"
        onClick={() => setShowMap((prev) => !prev)}
      >
        <div className="border-b-background border-shadow size-0 rotate-0 border-b-64 border-l-64 border-solid shadow-[0_2px_5px_rgba(0,0,0,0.15),-2px_-2px_5px_rgba(0,0,0,0.1)] drop-shadow-2xl"></div>
        <div className="clip-triangle absolute right-0 bottom-0 z-10 grid size-full place-items-center rounded-br-xl pt-2 pl-2">
          <Icon
            size={36}
            strokeWidth={2}
            className="text-primary animate-lite-pulse"
          />
        </div>
      </div>
    </div>
  );
};
