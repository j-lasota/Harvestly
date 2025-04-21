import React from "react";
import Image from "next/image";
import Link from "next/link";

const Navbar = () => {
  return (
    <header>
      <nav>
        <ul className="flex justify-between items-center bg-[#f6e9d2] p-4 text-black border-b-5 border-[#b3ab9a]">
          <li>
            <Image
              src="/harvestly-logo.png"
              alt="Harvesty logo"
              width={70}
              height={70}
              priority
            />
          </li>
          <li className="flex space-x-4">
            <Link href="/" className="hover:text-gray-400">
              Home
            </Link>
            <Link href="/map" className="hover:text-gray-400">
              Map
            </Link>
            <Link href="/about" className="hover:text-gray-400">
              About
            </Link>
            <Link href="/contact" className="hover:text-gray-400">
              Contact
            </Link>
            <Link href="/signin" className="hover:text-gray-400">
              Login
            </Link>
          </li>
        </ul>
      </nav>
    </header>
  );
};

export default Navbar;
