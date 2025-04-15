"use client";

import { useState } from "react";
import Link from "next/link";

export default function SignUpForm() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (password !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch("/api/auth/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || "Something went wrong");
      }

      console.log("Signed up!");
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-[#f6e9d2] text-[#3a3a3a] shadow-lg rounded-xl p-8 w-full max-w-md space-y-5 border border-[#b3ab9a]"
    >
      <div className="text-center">
        <h2 className="text-3xl font-bold mb-1">Create Account</h2>
        <p className="text-sm text-[#6b6b6b]">Join our community today</p>
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
          {error}
        </div>
      )}

      <div className="flex flex-col space-y-4">
        <div className="flex flex-col">
          <label htmlFor="email" className="mb-1 font-medium text-sm">
            Email Address
          </label>
          <input
            id="email"
            type="email"
            className="border border-[#b3ab9a] px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8a8374]"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
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
            value={password}
            onChange={(e) => setPassword(e.target.value)}
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
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
        </div>
      </div>

      <button
        type="submit"
        className="w-full bg-[#3a3a3a] text-[#f6e9d2] py-3 rounded-md hover:bg-[#2d2d2d] transition duration-200 disabled:opacity-50 font-medium"
        disabled={loading}
      >
        {loading ? (
          <span className="flex items-center justify-center">
            <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Creating account...
          </span>
        ) : (
          "Sign Up"
        )}
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