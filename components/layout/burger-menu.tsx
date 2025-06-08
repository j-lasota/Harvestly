"use client";

import { useTranslations } from "next-intl";
import { Menu, X } from "lucide-react";
import { useState } from "react";
import Link from "next/link";

import { NAVLINKS } from "@/constants/global";
import { Button } from "../ui/button";

const BurgerMenu = () => {
  const t = useTranslations("nav");
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <>
      {/* Burger menu button */}
      <Button
        variant="ghostPrimary"
        size="icon"
        aria-label={menuOpen ? "Close menu" : "Open menu"}
        onClick={() => setMenuOpen((v) => !v)}
      >
        {menuOpen ? (
          <X size={24} strokeWidth={1.5} />
        ) : (
          <Menu size={24} strokeWidth={1.5} />
        )}
      </Button>

      {/* Mobile nav */}
      {menuOpen && (
        <nav className="bg-background-elevated animate-in fade-in slide-in-from-top-4 absolute top-14 left-0 w-full shadow-lg md:hidden">
          <ul className="flex flex-col gap-2 p-4 text-lg font-medium">
            {NAVLINKS.map(({ href, label }) => (
              <li key={href}>
                <Link
                  href={href}
                  className="hover:text-primary rounded-xl px-3 py-2 transition-colors duration-200"
                  onClick={() => setMenuOpen(false)}
                >
                  {t(label)}
                </Link>
              </li>
            ))}
          </ul>
        </nav>
      )}
    </>
  );
};

export default BurgerMenu;
