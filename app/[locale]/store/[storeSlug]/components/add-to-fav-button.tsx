"use client";

import { Star, StarOff } from "lucide-react";
import { useTranslations } from "next-intl";
import { useState } from "react";

import { addFavoriteStore, removeFavoriteStore } from "../actions";
import { Button } from "@/components/ui/button";

const AddToFavButton = ({
  storeId,
  isFavorite: isFav,
}: {
  storeId: string;
  isFavorite: boolean;
}) => {
  const t = useTranslations("favoriteButton");
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
      size="sm"
      style={{ maxWidth: "max-content" }}
      className="gap-1.5"
    >
      {isFavorite ? (
        <>
          <StarOff size={16} strokeWidth={1.5} />
          {t("remove")}
        </>
      ) : (
        <>
          <Star size={16} strokeWidth={1.5} />
          {t("add")}
        </>
      )}
    </Button>
  );
};

export default AddToFavButton;
