import Auth0 from "next-auth/providers/auth0";
import NextAuth from "next-auth";

import { getClient } from "@/graphql/apollo-client";
import { graphql } from "@/graphql";

const userDataQuery = graphql(`
  query UserData($email: String!) {
    userByEmail(email: $email) {
      id
    }
  }
`);

export const { handlers, auth, signIn, signOut } = NextAuth({
  providers: [
    Auth0({
      clientId: process.env.AUTH_AUTH0_ID!,
      clientSecret: process.env.AUTH_AUTH0_SECRET!,
      issuer: process.env.AUTH_AUTH0_ISSUER!,
      redirectProxyUrl: process.env.AUTH_REDIRECT_PROXY_URL!,
    }),
  ],
  secret: process.env.AUTH_SECRET!,
  trustHost: true,
  callbacks: {
    authorized: async ({ auth }) => {
      // Logged in users are authenticated, otherwise redirect to login page
      return !!auth;
    },
    session: async ({ session, token }) => {
      if (session?.user && token.sub) {
        const { data } = await getClient().query({
          query: userDataQuery,
          variables: { email: session.user.email },
        });

        if (data.userByEmail) session.user.id = data.userByEmail.id;
      }
      return session;
    },
  },
});
