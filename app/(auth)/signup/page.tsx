import SignUpForm from "../../components/forms/signup-form";
import Navbar from "../../components/navbar-ds";
import React from "react";

export default function SignUp() {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <div className="flex-1 flex items-center justify-center bg-[#f6e9d2]">
        <SignUpForm />
      </div>
    </div>
  );
}
