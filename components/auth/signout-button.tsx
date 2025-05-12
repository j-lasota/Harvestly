import { useTranslations } from "next-intl";

import { Button } from "@/components/ui/button";
import { signOut } from "@/auth";

export function SignOut() {
  const t = useTranslations("avatarMenu");
  return (
    <form
      action={async () => {
        "use server";
        await signOut();
      }}
    >
      <Button type="submit" className="w-full">
        {t("signOut")}
      </Button>
    </form>
  );
}
