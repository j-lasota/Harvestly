"use client";

import { useState } from "react";

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
          Usuń z ulubionych
        </>
      ) : (
        <>
          <Star /> Dodaj do ulubionych
        </>
      )}
    </Button>
  );
};

export default AddToFavButton;
