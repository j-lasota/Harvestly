"use client";

import { useState } from "react";
import Link from "next/link";

export default function SignUpForm() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await fetch("/api/auth/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ 
          firstName, 
          lastName, 
          email, 
          phone, 
          password 
        }),
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
        <h2 className="text-3xl font-bold mb-1">Sign In</h2>
        <p className="text-sm text-[#6b6b6b]">Welcome back!</p>
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
          {error}
        </div>
      )}

      <div className="grid grid-cols-2 gap-4">
        <div className="flex flex-col">
          <label htmlFor="firstName" className="mb-1 font-medium text-sm">
            First Name
          </label>
          <input
            id="firstName"
            type="text"
            className="border border-[#b3ab9a] px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8a8374]"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
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
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
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
          value={email}
          onChange={(e) => setEmail(e.target.value)}
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
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
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
            Processing...
          </span>
        ) : (
          "Sign Up"
        )}
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