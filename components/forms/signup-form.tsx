"use client";

import Link from "next/link";

export default function SignUpForm() {
  return (
    <form className="bg-[#f6e9d2] text-[#3a3a3a] shadow-lg rounded-xl p-8 w-full max-w-md space-y-5 border border-[#b3ab9a]">
      <div className="text-center">
        <h2 className="text-3xl font-bold mb-1">Create Account</h2>
        <p className="text-sm text-[#6b6b6b]">Join our community today</p>
      </div>

      <div className="flex flex-col space-y-4">
        <div className="flex flex-col">
          <label htmlFor="email" className="mb-1 font-medium text-sm">
            Email Address
          </label>
          <input
            id="email"
            type="email"
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

        <div className="flex flex-col">
          <label htmlFor="confirmPassword" className="mb-1 font-medium text-sm">
            Confirm Password
          </label>
          <input
            id="confirmPassword"
            type="password"
            className="border border-[#b3ab9a] px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8a8374]"
            required
          />
        </div>
      </div>

      <button
        type="button"
        className="w-full bg-[#3a3a3a] text-[#f6e9d2] py-3 rounded-md hover:bg-[#2d2d2d] transition duration-200 font-medium"
      >
        Sign Up
      </button>

      <p className="text-sm text-center text-[#6b6b6b]">
        Already have an account?{" "}
        <Link href="/signin" className="text-[#3a3a3a] hover:underline font-medium">
          Sign In
        </Link>
      </p>
    </form>
  );
}