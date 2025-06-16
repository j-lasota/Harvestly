import Auth0 from "next-auth/providers/auth0";
import NextAuth from "next-auth";

export const { handlers, auth, signIn, signOut } = NextAuth({
  providers: [
    Auth0({
      clientId: process.env.AUTH_AUTH0_ID!,
      clientSecret: process.env.AUTH_AUTH0_SECRET!,
      issuer: process.env.AUTH_AUTH0_ISSUER!,
      authorization: {
        params: {
          audience: process.env.AUTH_AUTH0_AUDIENCE!,
        },
      },
    }),
  ],
  secret: process.env.AUTH_SECRET!,
  trustHost: true,
  callbacks: {
    authorized: async ({ auth }) => {
      return !!auth;
    },
    jwt: async ({ token, account, profile }) => {
      if (account) {
        token.accessToken = account.access_token;
        if (account.access_token) {
          try {
            const decodedAccessToken = JSON.parse(
              Buffer.from(
                account.access_token.split(".")[1],
                "base64"
              ).toString()
            );
            if (decodedAccessToken.permissions) {
              token.permissions = decodedAccessToken.permissions;
            }
          } catch (error) {
            console.error(
              "Błąd dekodowania accessToken lub brak permissions w tokenie:",
              error
            );
          }
        }
      }

      if (profile && profile.sub) {
        token.sub = profile.sub ?? undefined;
      }

      return token;
    },
    session: async ({ session, token }) => {
      if (token.accessToken) {
        session.accessToken = token.accessToken;
      }

      if (session?.user && token.sub) {
        session.user.id = token.sub;
      }

      if (token.permissions) {
        session.user.permissions = token.permissions;
      }

      return session;
    },
  },
});
