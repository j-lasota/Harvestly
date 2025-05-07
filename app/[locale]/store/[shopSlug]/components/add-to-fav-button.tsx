"use client";

import { useState } from "react";

import { addFavoriteShop, removeFavoriteShop } from "../actions";
import { Button } from "@/components/ui/button";

const AddToFavButton = ({ shopId }: { shopId: string }) => {
  // TODO: Fetch favorite shops from the database
  const [isFavorite, setIsFavorite] = useState(false);

  const handleFavorite = () => {
    if (isFavorite) {
      removeFavoriteShop(shopId);
    } else {
      addFavoriteShop(shopId);
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
      {isFavorite ? "Usuń z ulubionych" : "Dodaj do ulubionych"}
    </Button>
  );
};

export default AddToFavButton;
