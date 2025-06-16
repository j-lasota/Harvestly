// app/admin/page.tsx
"use client"; // To jest Client Component

import { useSession, signIn } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

// WAŻNE: To uprawnienie MUSI dokładnie odpowiadać jednemu ze stringów
// w tablicy 'permissions' dla admina w tokenie Auth0.
const ADMIN_PERMISSION = "read:admin"; 

export default function AdminPage() {
  const { data: session, status } = useSession(); // status: "loading", "authenticated", "unauthenticated"
  const router = useRouter();

  useEffect(() => {
    // 1. Jeśli sesja się jeszcze ładuje, poczekaj.
    if (status === "loading") {
      return;
    }

    // 2. Jeśli użytkownik nie jest zalogowany (status "unauthenticated"), przekieruj do logowania.
    if (!session) {
      console.log("Użytkownik nie jest zalogowany. Przekierowanie do logowania...");
      // Możesz użyć signIn() które przekieruje na skonfigurowaną stronę logowania Auth0.
      // Alternatywnie: router.push('/api/auth/signin');
      signIn(); 
      return; // Zatrzymaj renderowanie i wykonanie dalszego kodu w useEffect
    }

    // 3. Użytkownik jest zalogowany (status "authenticated"). Sprawdź uprawnienia.
    // Pobierz uprawnienia użytkownika, domyślnie pusta tablica, jeśli brak pola permissions.
    const userPermissions = session.user?.permissions || [];
    // Sprawdź, czy tablica uprawnień zawiera wymagane uprawnienie admina.
    const isAdmin = userPermissions.includes(ADMIN_PERMISSION);

    // 4. Jeśli użytkownik nie ma uprawnień admina, przekieruj go.
    if (!isAdmin) {
      console.log(`Brak uprawnień admina (${ADMIN_PERMISSION}). Przekierowanie na stronę główną...`);
      router.push("/"); // Przekieruj na stronę główną lub inną stronę z informacją o braku dostępu
      // Możesz też tutaj wyświetlić komunikat:
      // return <div>Brak dostępu. Nie posiadasz wymaganych uprawnień administratora.</div>;
    }

    // Logowanie dla debugowania:
    console.log("Status sesji:", status);
    console.log("Dane sesji:", session);
    console.log("Uprawnienia użytkownika:", userPermissions);
    console.log("Czy admin?", isAdmin);

  }, [session, status, router]); // Zależności hooka useEffect

  // Renderowanie warunkowe w zależności od statusu ładowania i uprawnień
  if (status === "loading") {
    return <div>Ładowanie strony administratora...</div>;
  }

  // Jeśli sesja jest załadowana i użytkownik MA uprawnienia admina, wyświetl zawartość strony.
  // Ten warunek jest dodatkowym zabezpieczeniem i upewnieniem się, że renderujemy tylko dla adminów.
  if (session && session.user?.permissions?.includes(ADMIN_PERMISSION)) {
    return (
      <div>
        <h1>Witaj na stronie administratora!</h1>
        <p>Tutaj masz dostęp do wszystkich funkcji administracyjnych.</p>
        <p>Twoje uprawnienia: {session.user.permissions.join(', ')}</p>
        {/* Tutaj umieść komponenty i treści dostępne tylko dla admina */}
      </div>
    );
  }

  // W przypadku, gdy użytkownik nie jest adminem (zostanie przekierowany w useEffect)
  // lub sesja jest w innym stanie (np. błąd, ale nie loading), zwracamy null.
  // Ważne, aby nie renderować niczego, co mogłoby migotać przed przekierowaniem.
  return null;
}