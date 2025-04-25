"use client";

import Link from "next/link";
import React from "react";
import { login } from "@/lib/actions/auth";

export default function SignInForm() {
  return (
    <form className="bg-[#f6e9d2] text-[#3a3a3a] shadow-lg rounded-xl p-8 w-full max-w-md space-y-5 border border-[#b3ab9a]">
      <div className="text-center">
        <h2 className="text-3xl font-bold mb-1">Sign In</h2>
        <p className="text-sm text-[#6b6b6b]">Welcome back!</p>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="flex flex-col">
          <label htmlFor="firstName" className="mb-1 font-medium text-sm">
            First Name
          </label>
          <input
            id="firstName"
            type="text"
            className="border border-[#b3ab9a] px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8a8374]"
            required
          />
        </div>

        <div className="flex flex-col">
          <label htmlFor="lastName" className="mb-1 font-medium text-sm">
            Last Name
          </label>
          <input
            id="lastName"
            type="text"
            className="border border-[#b3ab9a] px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8a8374]"
            required
          />
        </div>
      </div>

      <div className="flex flex-col">
        <label htmlFor="email" className="mb-1 font-medium text-sm">
          Email
        </label>
        <input
          id="email"
          type="email"
          className="border border-[#b3ab9a] px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8a8374]"
          required
        />
      </div>

      <div className="flex flex-col">
        <label htmlFor="phone" className="mb-1 font-medium text-sm">
          Phone Number
        </label>
        <input
          id="phone"
          type="tel"
          className="border border-[#b3ab9a] px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8a8374]"
          required
        />
      </div>

      <div className="flex flex-col">
        <label htmlFor="password" className="mb-1 font-medium text-sm">
          Password
        </label>
        <input
          id="password"
          type="password"
          className="border border-[#b3ab9a] px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8a8374]"
          required
        />
      </div>

      <button
        type="button"
        onClick={() => login()}
        className="w-full bg-[#3a3a3a] text-[#f6e9d2] py-3 rounded-md hover:bg-[#2d2d2d] transition duration-200 font-medium"
      >
        Sign In With Keycloack
      </button>

      <p className="text-sm text-center text-[#6b6b6b]">
        Doesn't have an account?{" "}
        <Link href="/signup" className="text-[#3a3a3a] hover:underline font-medium">
          Sign Up
        </Link>
      </p>
    </form>
  );
}