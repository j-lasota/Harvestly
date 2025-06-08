import { useTranslations } from "next-intl";
import { useMap } from "react-leaflet";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import { getCoordsFromCity } from "../actions";
import { Input } from "@/components/ui/input";

const SearchBox = () => {
  const t = useTranslations("page.addStore");
  const [city, setCity] = useState("");
  const map = useMap();

  const handleSearch = async (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    const coords = await getCoordsFromCity(city);
    if (coords) map.flyTo([coords.lat, coords.lng], 14);
  };

  return (
    <div className="bg-background-base absolute top-0 right-0 left-0 z-[1000] flex items-center justify-between gap-2 py-1">
      <Input
        type="text"
        placeholder={t("placeholder.location")}
        value={city}
        onChange={(e) => setCity(e.target.value)}
        className="rounded border px-2 py-1"
      />

      <Button onClick={handleSearch} type="button">
        Szukaj
      </Button>
    </div>
  );
};

export default SearchBox;
