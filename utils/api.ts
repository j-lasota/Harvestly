// utils/api.ts

// Używamy zmiennej środowiskowej, która będzie dostępna na frontendzie
const BACKEND_BASE_URL = process.env.NEXT_PUBLIC_API;

if (!BACKEND_BASE_URL) {
  console.error("Environment variable NEXT_PUBLIC_API is not set. Please ensure it's defined in .env.local or your deployment configuration.");
}

export const recordEvent = async (slug: string, eventType: "MAP_PIN" | "STORE_PAGE") => {
  if (!BACKEND_BASE_URL) {
    console.error("Cannot record event: BACKEND_BASE_URL is not defined. Aborting request.");
    return;
  }

  try {
    // Zmieniamy wysyłanie danych z ciała żądania JSON na parametry zapytania URL
    // Zgodnie z @RequestParam String slug i @RequestParam EventType type w Twoim backendzie
    const response = await fetch(`${BACKEND_BASE_URL}/stats/event?slug=${slug}&type=${eventType}`, {
      method: 'POST',
      // Nagłówki Content-Type nie są wymagane, ponieważ dane są w URL, a nie w ciele żądania
      // Jeśli wysyłasz body, Content-Type jest kluczowy. Tutaj nie wysyłamy body.
      // body: JSON.stringify({ slug, type: eventType }), // TO ZOSTALO USUNIETE
    });

    if (!response.ok) {
      console.error(`Failed to record event: ${response.status} - ${response.statusText}`);
      const errorBody = await response.text();
      console.error('Error response body:', errorBody);
    } else {
      console.log(`Event ${eventType} recorded successfully for slug: ${slug}`);
    }
  } catch (error) {
    console.error('Error recording event:', error);
  }
};

export const getStoreStatistics = async (slug: string) => {
  if (!BACKEND_BASE_URL) {
    console.error("Cannot fetch store statistics: BACKEND_BASE_URL is not defined. Aborting request.");
    return null;
  }
  try {
    // Endpoint: /api/stats/ratio?slug=store-xyz (z Twojego kodu backendu)
    const response = await fetch(`${BACKEND_BASE_URL}/stats/ratio?slug=${slug}`);
    if (!response.ok) {
      console.error(`Failed to fetch store statistics: ${response.status} - ${response.statusText}`);
      return null;
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching store statistics:', error);
    return null;
  }
};

export const getStoreStatisticsForPeriod = async (slug: string, days: number) => {
  if (!BACKEND_BASE_URL) {
    console.error("Cannot fetch store statistics for period: BACKEND_BASE_URL is not defined. Aborting request.");
    return null;
  }
  try {
    // Endpoint: /api/stats/ratio/period?slug=store-xyz&days=7 (z Twojego kodu backendu)
    const response = await fetch(`${BACKEND_BASE_URL}/stats/ratio/period?slug=${slug}&days=${days}`);
    if (!response.ok) {
      console.error(`Failed to fetch store statistics for period: ${response.status} - ${response.statusText}`);
      return null;
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching store statistics for period:', error);
    return null;
  }
};

export const getStoreAverageRating = async (slug: string) => {
  if (!BACKEND_BASE_URL) {
    console.error("Cannot fetch store average rating: BACKEND_BASE_URL is not defined. Aborting request.");
    return null;
  }
  try {
    // Endpoint: /api/stats/average-rating?slug=store-xyz (z Twojego kodu backendu)
    const response = await fetch(`${BACKEND_BASE_URL}/stats/average-rating?slug=${slug}`);
    if (!response.ok) {
      console.error(`Failed to fetch store average rating: ${response.status} - ${response.statusText}`);
      return null;
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching store average rating:', error);
    return null;
  }
};