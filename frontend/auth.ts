import NextAuth from "next-auth";
import KeycloakProvider from "next-auth/providers/keycloak";

export const { auth, handlers, signIn, signOut } = NextAuth({
  providers: [
    KeycloakProvider({
      clientId: process.env.AUTH_KEYCLOAK_ID,
      clientSecret: process.env.AUTH_KEYCLOAK_SECRET,
      issuer: process.env.AUTH_KEYCLOAK_ISSUER,
    })
  ],
  secret: process.env.AUTH_SECRET
});