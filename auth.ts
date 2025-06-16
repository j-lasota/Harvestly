// auth.ts
import Auth0 from "next-auth/providers/auth0";
import NextAuth from "next-auth";

// Nie importujemy już typów Session, JWT, DefaultSession z 'next-auth'
// ani nie używamy `declare module`, ponieważ są one w `next-auth.d.ts`

export const { handlers, auth, signIn, signOut } = NextAuth({
  providers: [
    Auth0({
      clientId: process.env.AUTH_AUTH0_ID!,
      clientSecret: process.env.AUTH_AUTH0_SECRET!,
      issuer: process.env.AUTH_AUTH0_ISSUER!,
      authorization: {
        params: {
          audience: process.env.AUTH_AUTH0_AUDIENCE,
          // Opcjonalnie: Jeśli Twoje API wymaga konkretnych scopes do wydania tokena z permissions,
          // dodaj je tutaj. Np. scope: 'openid profile email read:admin write:admin'.
          // Sprawdź dokumentację Auth0 i swoje API.
          // scope: 'openid profile email' // przykład, jeśli potrzebujesz
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

        // WAŻNE: Dekodowanie accessToken w celu wyciągnięcia permissions
        // Zakładamy, że permissions są w accessToken (Auth0 domyślnie je tam umieszcza, jeśli skonfigurowano)
        if (account.access_token) {
          try {
            const decodedAccessToken = JSON.parse(
              Buffer.from(account.access_token.split(".")[1], "base64").toString()
            );
            // WAŻNE: Sprawdź, jak pole z uprawnieniami jest nazwane w Twoim Auth0 tokenie.
            // Często to 'permissions', ale może być to niestandardowe roszczenie np. 'https://twoja.domena.com/permissions'
            if (decodedAccessToken.permissions) {
              token.permissions = decodedAccessToken.permissions;
            }
          } catch (error) {
            console.error("Błąd dekodowania accessToken lub brak permissions w tokenie:", error);
          }
        }
      }

      if (profile && profile.sub) {
        token.sub = profile.sub ?? undefined;
      }

      return token;
    },
    session: async ({ session, token }) => {
      // TypeScript będzie teraz wiedział, że `session.accessToken` i `session.user.id`
      // oraz `session.user.permissions` mogą istnieć dzięki `next-auth.d.ts`
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