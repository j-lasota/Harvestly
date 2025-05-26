import Auth0 from "next-auth/providers/auth0";
import NextAuth from "next-auth";

export const { handlers, auth, signIn, signOut } = NextAuth({
  providers: [
    Auth0({
      clientId: process.env.AUTH_AUTH0_ID!,
      clientSecret: process.env.AUTH_AUTH0_SECRET!,
      issuer: process.env.AUTH_AUTH0_ISSUER!,
    }),
  ],
  secret: process.env.AUTH_SECRET!,
  trustHost: true,
  callbacks: {
    authorized: async ({ auth }) => {
      // Logged in users are authenticated, otherwise redirect to login page
      return !!auth;
    },
    jwt: async ({ profile, token }) => {
      if (profile && profile.sub) {
        token.sub = profile.sub.split("|")[1] ?? undefined;
      }
      return token;
    },

    session: async ({ session, token }) => {
      if (session?.user && token.sub) {
        session.user.id = token.sub;
      }
      return session;
    },
  },
});
