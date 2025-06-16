"use client";

import { useTranslations } from "next-intl";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import { addVerification } from "../actions";

const AddVerificationButton = ({
  storeId,
  isVerifiedByUser,
}: {
  storeId: string;
  isVerifiedByUser: boolean;
}) => {
  const t = useTranslations("verificationButton");
  const [isVerified, setIsVerified] = useState(isVerifiedByUser);

  const handleFavorite = () => {
    if (isVerified) return;
    addVerification(storeId);
    setIsVerified(true);
  };

  return (
    <Button
      onClick={handleFavorite}
      variant="outline"
      size="sm"
      style={{ maxWidth: "max-content" }}
      disabled={isVerified}
    >
      {isVerified ? t("verified") : t("add")}
    </Button>
  );
};

export default AddVerificationButton;
