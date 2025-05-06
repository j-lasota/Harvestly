import { useTranslations } from "next-intl";
import { Button } from "@/components/ui/button";
import { signOut } from "@/auth";

export function SignOut() {
  const t = useTranslations("AvatarMenu");
  return (
    <form
      action={async () => {
        "use server";
        await signOut();
      }}
    >
      <Button type="submit" className="w-full">
        {t("signout")}
      </Button>
    </form>
  );
}
