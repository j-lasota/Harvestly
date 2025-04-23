import SignInForm from "../../components/forms/signin-form";
import Navbar from "../../components/navbar-ds";
import React from "react";

export default function SignIn() {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <div className="flex-1 flex items-center justify-center bg-[#f6e9d2]">
        <SignInForm />
      </div>
    </div>
  );
}
