"use client";

import { Store } from 'lucide-react';
import { useRouter } from "next/navigation";

interface ReportedShop {
  id: string;
  name: string;
  description: string | null;
  latitude: number;
  longitude: number;
  city: string;
  address: string;
  imageUrl: string | null;
  verified: boolean;
  slug: string | null;
  reported: boolean | null;
}

interface ReportedShopsListProps {
  shops: (ReportedShop | null)[];
}

export default function ReportedShopsList({ shops }: ReportedShopsListProps) {
  const router = useRouter();

  const handleShopClick = (slug: string) => {
    if (slug) {
      router.push(`/store/${slug}`);
    }
  };

  const nonNullShops = shops.filter(Boolean) as ReportedShop[];

  return (
    <section className="bg-background-elevated p-6 rounded-xl shadow-lg border border-shadow">
      <h2 className="text-2xl font-bold mb-6 text-primary flex items-center justify-center gap-3">
        <Store className="h-8 w-8" /> Zgłoszone Sklepy ({nonNullShops.length})
      </h2>
      {nonNullShops.length === 0 ? (
        <p className="text-foreground text-center py-4">
          Brak zgłoszonych sklepów.
        </p>
      ) : (
        // Zmienione klasy: 'grid grid-cols-1 gap-6' na 'flex flex-wrap gap-6'
        // Możesz też użyć siatki z automatycznymi kolumnami: 'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6'
        <div className="flex flex-wrap gap-6 justify-center"> {/* Dodano justify-center dla lepszego wyśrodkowania */}
          {nonNullShops.map((shop: ReportedShop) => (
            <div
              key={shop.id}
              // Dodano w-full na mniejszych ekranach, md:w-[calc(50%-12px)] dla dwóch kolumn na średnich
              // 'w-full sm:w-[calc(50%-12px)] lg:w-[calc(33.333%-16px)]'
              className="border-shadow bg-background-elevated ring-ring flex flex-col gap-4 rounded-2xl border-r-3 border-b-4 px-4 py-3 shadow-md ring cursor-pointer hover:shadow-lg transition-all duration-300 transform hover:scale-[1.02]
                         w-full sm:w-[calc(50%-0.75rem)] xl:w-[calc(33.333%-1rem)]" /* calc(50% - gap/2) */
              onClick={() => handleShopClick(shop.slug!)}
            >
              <div className="flex w-full flex-col justify-between gap-2 sm:mt-4">
                <div>
                  <h3 className="text-2xl font-semibold text-primary">
                    {shop.name}
                  </h3>
                  <p className="text-foreground text-sm">
                    <span className="font-medium">Miasto:</span> {shop.city}
                  </p>
                  <p className="text-foreground text-sm">
                    <span className="font-medium">Adres:</span> {shop.address}
                  </p>
                </div>
                <div className="mt-4 text-sm text-blue-500 dark:text-blue-300 font-semibold flex items-center gap-1 self-end">
                  Przejdź do sklepu
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}