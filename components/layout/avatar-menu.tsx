import { useTranslations } from "next-intl";
import Image from "next/image";
import React from "react";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import accountPlaceholder from "@/public/account_placeholder.jpg";
import Link from "next/link";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

export const AvatarMenu = ({
  Logout,
  image,
}: {
  Logout: React.ReactNode;
  image: string | null | undefined;
}) => {
  const t = useTranslations("avatarMenu");
  return (
    <DropdownMenu>
      <DropdownMenuTrigger className="cursor-pointer outline-none">
        <Avatar>
          {image && <AvatarImage src={image} alt="Profile picture" />}
          <AvatarFallback>
            <Image src={accountPlaceholder} alt="Account placeholder" />
          </AvatarFallback>
        </Avatar>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="min-w-48 p-2">
        <DropdownMenuLabel>{t("myAccount")}</DropdownMenuLabel>
        <DropdownMenuSeparator />
        <DropdownMenuItem asChild>
          <Link href="/profile">{t("profile")}</Link>
        </DropdownMenuItem>
        <DropdownMenuItem asChild>
          <Link href="/my-stores">{t("myStores")}</Link>
        </DropdownMenuItem>
        <DropdownMenuItem asChild>
          <Link href="/add-store">{t("addStore")}</Link>
        </DropdownMenuItem>
        <div className="mt-2">{Logout}</div>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
