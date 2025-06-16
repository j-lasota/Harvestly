"use client";

import { useTranslations } from "next-intl";
import { useState } from "react";

import { reportOpinion, reportStore } from "../actions";
import { Button } from "@/components/ui/button";

const ReportStoreButton = ({
  storeId,
  isReportedByUser,
}: {
  storeId: string;
  isReportedByUser: boolean;
}) => {
  const t = useTranslations("reportButton");
  const [isReported, setIsReported] = useState(isReportedByUser);

  const handleReport = () => {
    if (isReported) return;
    reportStore(storeId);
    setIsReported(true);
  };

  return (
    <Button
      onClick={handleReport}
      variant="outline"
      size="sm"
      style={{ maxWidth: "max-content" }}
      disabled={isReported}
    >
      {isReported ? t("reported") : t("report")}
    </Button>
  );
};

const ReportOpinionButton = ({
  opinionId,
  isReportedByUser,
}: {
  opinionId: string;
  isReportedByUser: boolean;
}) => {
  const t = useTranslations("reportButton");
  const [isReported, setIsReported] = useState(isReportedByUser);

  const handleReport = () => {
    if (isReported) return;
    reportOpinion(opinionId);
    setIsReported(true);
  };

  return (
    <Button
      onClick={handleReport}
      variant="outline"
      size="sm"
      style={{ maxWidth: "max-content" }}
      disabled={isReported}
    >
      {isReported ? t("reported") : t("report")}
    </Button>
  );
};

export { ReportStoreButton, ReportOpinionButton };
