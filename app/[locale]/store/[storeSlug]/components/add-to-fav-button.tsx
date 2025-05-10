"use client";

import { useState } from "react";

import { addFavoriteStore, removeFavoriteStore } from "../actions";
import { Button } from "@/components/ui/button";

const AddToFavButton = ({ storeId }: { storeId: string }) => {
  // TODO: Fetch favorite stores from the database
  const [isFavorite, setIsFavorite] = useState(false);

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
      {isFavorite ? "Usuń z ulubionych" : "Dodaj do ulubionych"}
    </Button>
  );
};

export default AddToFavButton;
