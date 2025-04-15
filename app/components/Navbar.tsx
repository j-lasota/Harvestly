import React from "react";
import Image from "next/image";

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
            <a className="hover:text-gray-400">
              Home
            </a>
            <a className="hover:text-gray-400">
              Map
            </a>
            <a className="hover:text-gray-400">
              Contact
            </a>
          </li>
        </ul>
      </nav>
    </header>
  );
};

export default Navbar;
