import { useTranslations } from "next-intl";
import React from "react";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

export const AvatarMenu = ({ Logout }: { Logout: React.ReactNode }) => {
  const t = useTranslations("AvatarMenu");
  return (
    <DropdownMenu>
      <DropdownMenuTrigger className="cursor-pointer outline-none">
        <Avatar>
          <AvatarImage src="/placeholder.jpeg" alt="Profile picture" />
          <AvatarFallback></AvatarFallback>
        </Avatar>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="min-w-48 p-2">
        <DropdownMenuLabel>{t("myaccount")}</DropdownMenuLabel>
        <DropdownMenuSeparator />
        <DropdownMenuItem>{t("profile")}</DropdownMenuItem>
        <DropdownMenuItem>{t("mystores")}</DropdownMenuItem>
        <div className="mt-2">{Logout}</div>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
