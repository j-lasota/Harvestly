import { defineRouting } from "next-intl/routing";

export const routing = defineRouting({
  locales: ["en", "pl", "de", "fr", "es", "it", "uk"],
  defaultLocale: "en",
});
