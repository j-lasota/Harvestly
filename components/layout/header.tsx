import { getTranslations } from "next-intl/server";
import Image from "next/image";
import Link from "next/link";

import { ContainerWrapper } from "@/components/layout/container-wrapper";
import { LanguageSelector } from "@/components/layout/language-selector";
import { AvatarMenu } from "@/components/layout/avatar-menu";
import ThemeToggle from "@/components/layout/theme-toggle";
import { SignOut } from "@/components/auth/signout-button";
import { SignIn } from "@/components/auth/signin-button";
import BurgerMenu from "@/components/layout/burger-menu";
import { NAVLINKS } from "@/constants/global";
import logo from "@/public/logo.svg";
import { auth } from "@/auth";

export default async function Header() {
  const t = await getTranslations("nav");
  const session = await auth();

  return (
    <header className="bg-background-elevated border-shadow ring-ring sticky top-0 z-50 rounded-lg border-b-3 ring">
      <ContainerWrapper className="flex h-16 items-center justify-between md:h-20">
        <Link href="/" className="flex items-center gap-1.5 text-xl font-bold">
          <Image src={logo} alt="Harvesty logo" priority className="w-6" />
          Harvestly
        </Link>

        <div className="flex items-center gap-1">
          {/* Desktop nav */}
          <nav className="hidden md:block">
            <ul className="flex items-center gap-0.5 text-lg font-medium">
              {NAVLINKS.map(({ href, label }) => (
                <li key={href}>
                  <Link
                    href={href}
                    className="hover:text-primary rounded-xl px-3 py-2 transition-colors duration-200"
                  >
                    {t(label)}
                  </Link>
                </li>
              ))}
            </ul>
          </nav>

          {/* Mobile nav */}
          <BurgerMenu />

          <ThemeToggle />
          <LanguageSelector className="mr-2" />

          {session ? (
            <AvatarMenu Logout={<SignOut />} image={session?.user?.image} />
          ) : (
            <SignIn />
          )}
        </div>
      </ContainerWrapper>
    </header>
  );
}
