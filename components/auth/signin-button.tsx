import { useTranslations } from "next-intl";
import Image from "next/image";

import keycloak from "@/public/keycloak.png";
import { signIn } from "@/auth";

export function SignIn() {
  const t = useTranslations("Auth");

  return (
    <form
      action={async () => {
        "use server";
        await signIn("keycloak");
      }}
    >
      <button
        type="submit"
        className="text-accent flex w-full cursor-pointer items-center gap-2 rounded-md bg-zinc-700 px-5 py-3 font-medium transition duration-200 hover:bg-zinc-800"
      >
        <Image src={keycloak} alt="Keycloak logo" className="w-6" />
        {t("signinKeycloak")}
      </button>
    </form>
  );
}
