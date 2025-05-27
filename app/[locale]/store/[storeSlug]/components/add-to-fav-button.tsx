"use client";

import { useState } from "react";
import { useTranslations } from "next-intl";

import { addFavoriteStore, removeFavoriteStore } from "../actions";
import { Button } from "@/components/ui/button";
import { Star, StarOff } from "lucide-react";

const AddToFavButton = ({
  storeId,
  isFavorite: isFav,
}: {
  storeId: string;
  isFavorite: boolean;
}) => {
  const t = useTranslations("favorites");
  const [isFavorite, setIsFavorite] = useState(isFav);

  const handleFavorite = () => {
    if (isFavorite) {
      removeFavoriteStore(storeId);
    } else {
      addFavoriteStore(storeId);
    }
    setIsFavorite(!isFavorite);
  };

  return (
    <Button
      onClick={handleFavorite}
      variant="outline"
      size="sm"
      style={{ maxWidth: "max-content" }}
    >
      {isFavorite ? (
        <>
          <StarOff />
          {t("remove")}
        </>
      ) : (
        <>
          <Star />
          {t("add")}
        </>
      )}
    </Button>
  );
};

export default AddToFavButton;
