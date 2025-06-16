import { useTranslations } from "next-intl";
import Image from "next/image";

import { Button } from "@/components/ui/button";
import auth0 from "@/public/auth0.svg";
import { signIn } from "@/auth";

export function SignIn() {
  const t = useTranslations("auth");

  return (
    <form
      action={async () => {
        "use server";
        await signIn("auth0");
      }}
    >
      <Button type="submit">
        <Image src={auth0} alt="Oauth logo" className="w-6" />
        {t("signIn")}
      </Button>
    </form>
  );
}
