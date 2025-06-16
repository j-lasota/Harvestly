import { DefaultSession } from "next-auth";
import "next-auth/jwt";
import "next-auth";

declare module "next-auth" {
  interface Session {
    accessToken?: string;
    user: {
      id: string;
      permissions?: string[];
    } & DefaultSession["user"];
  }
}

declare module "next-auth/jwt" {
  interface JWT {
    accessToken?: string;
    sub?: string;
    permissions?: string[];
  }
}
