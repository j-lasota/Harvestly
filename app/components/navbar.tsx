"use client";

import { Menu, X } from "lucide-react";
import { useState } from "react";
import Image from "next/image";
import Link from "next/link";

import logo from "@/public/logo.svg";
import { cn } from "@/lib/utils";

const NAVLINKS = [
  { href: "/", label: "Home" },
  { href: "/map", label: "Map" },
  { href: "/about", label: "About" },
  { href: "/contact", label: "Contact" },
  { href: "/signin", label: "Login" },
];

const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <header className="bg-background sticky top-0 z-50 rounded-lg drop-shadow-md">
      <div className="container mx-auto flex h-16 items-center justify-between px-4 md:h-20">
        <Link href="/" className="flex items-center gap-1.5 text-xl font-bold">
          <Image src={logo} alt="Harvesty logo" priority className="w-6" />
          Harvestly
        </Link>

        {/* Desktop nav */}
        <nav className="hidden md:block">
          <ul className="flex gap-0.5 text-lg font-medium">
            {NAVLINKS.map(({ href, label }, idx) => (
              <li key={href}>
                <Link
                  href={href}
                  className={cn(
                    "rounded-xl px-4 py-2 transition-colors duration-200",
                    NAVLINKS.length - 1 === idx
                      ? "bg-primary text-accent hover:bg-primary/90"
                      : "hover:text-primary"
                  )}
                >
                  {label}
                </Link>
              </li>
            ))}
          </ul>
        </nav>

        {/* Burger menu button */}
        <button
          className="hover:bg-muted cursor-pointer rounded-lg p-2 transition md:hidden"
          aria-label={menuOpen ? "Close menu" : "Open menu"}
          onClick={() => setMenuOpen((v) => !v)}
        >
          {menuOpen ? <X size={28} /> : <Menu size={28} />}
        </button>
      </div>

      {/* Mobile nav */}
      {menuOpen && (
        <nav className="bg-background animate-in fade-in slide-in-from-top-4 absolute top-14 left-0 w-full shadow-lg md:hidden">
          <ul className="flex flex-col gap-1 p-4 text-lg font-medium">
            {NAVLINKS.map(({ href, label }, idx) => (
              <li key={href}>
                <Link
                  href={href}
                  className={cn(
                    "block w-full rounded-xl px-4 py-3 transition-colors duration-200",
                    NAVLINKS.length - 1 === idx
                      ? "bg-primary text-accent hover:bg-primary/90"
                      : "hover:text-primary"
                  )}
                  onClick={() => setMenuOpen(false)}
                >
                  {label}
                </Link>
              </li>
            ))}
          </ul>
        </nav>
      )}
    </header>
  );
};

export default Navbar;
