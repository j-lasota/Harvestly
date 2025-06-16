// /types/next-auth.d.ts

import 'next-auth';
import 'next-auth/jwt';
import { DefaultSession } from 'next-auth'; // Importuj DefaultSession

/**
 * Rozszerzenie domyślnych typów dostarczanych przez next-auth.
 * Pozwala na dodanie niestandardowych właściwości do obiektu sesji i tokena JWT.
 */

declare module 'next-auth' {
  /**
   * Zwracany przez hooki `useSession`, `getSession` oraz w callbacku `session`.
   * Rozszerzamy go, aby zawierał nasz accessToken i permissions.
   */
  interface Session {
    accessToken?: string; // Możesz też użyć `string | undefined`
    user: {
      /** Domyślny typ użytkownika jest bardzo prosty. Rozszerzamy go o id i permissions. */
      id: string;
      permissions?: string[]; // ⬇️ DODANE: Uprawnienia użytkownika
    } & DefaultSession['user'];
  }
}

declare module 'next-auth/jwt' {
  /**
   * Zwracany przez callback `jwt` oraz używany wewnętrznie do przechowywania danych sesji.
   * Rozszerzamy go, aby pasował do danych, które dodajemy w callbacku `jwt`.
   */
  interface JWT {
    /** Token dostępowy z Auth0 */
    accessToken?: string; // Musi pasować do typu w Session
    /** ID użytkownika */
    sub?: string;
    permissions?: string[]; // ⬇️ DODANE: Uprawnienia użytkownika w tokenie JWT
  }
}