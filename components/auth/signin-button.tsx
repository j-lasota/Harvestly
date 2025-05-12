import { useTranslations } from "next-intl";
import Image from "next/image";

import { Button } from "@/components/ui/button";
import auth0 from "@/public/auth0.svg";
import { signIn } from "@/auth";

export function SignIn() {
  const t = useTranslations("nav");

  return (
    <form
      action={async () => {
        "use server";
        await signIn("keycloak");
      }}
    >
      <Button type="submit">
        <Image src={auth0} alt="Keycloak logo" className="w-6" />
        {t("signIn")}
      </Button>
    </form>
  );
}
