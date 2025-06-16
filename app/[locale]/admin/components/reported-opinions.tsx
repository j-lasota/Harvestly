"use client";

import { MessageSquareText } from 'lucide-react';
import { useRouter } from "next/navigation";

interface ReportedOpinion {
  id: string;
  description: string | null;
  stars: number;
  user: {
    firstName: string;
    lastName: string;
  };
  store: {
    name?: string | null;
    slug: string | null;
  };
}

interface ReportedOpinionsListProps {
  opinions: (ReportedOpinion | null)[];
}

export default function ReportedOpinionsList({ opinions }: ReportedOpinionsListProps) {
  const router = useRouter();

  const handleOpinionClick = (storeSlug: string | null) => {
    if (storeSlug) {
      router.push(`/store/${storeSlug}`);
    }
  };

  const nonNullOpinions = opinions.filter(Boolean) as ReportedOpinion[];

  return (
    <section className="bg-background-elevated p-6 rounded-xl shadow-lg border border-shadow">
      <h2 className="text-2xl font-bold mb-6 text-primary flex items-center justify-center gap-3">
        <MessageSquareText className="h-8 w-8" /> Zgłoszone Opinie ({nonNullOpinions.length})
      </h2>
      {nonNullOpinions.length === 0 ? (
        <p className="text-foreground text-center py-4">
          Brak zgłoszonych opinii.
        </p>
      ) : (
        // Zmienione klasy: 'grid grid-cols-1 gap-6' na 'flex flex-wrap gap-6'
        // Możesz też użyć siatki z automatycznymi kolumnami: 'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6'
        <div className="flex flex-wrap gap-6 justify-center"> {/* Dodano justify-center dla lepszego wyśrodkowania */}
          {nonNullOpinions.map((opinion: ReportedOpinion) => (
            <div
              key={opinion.id}
              // Dodano w-full na mniejszych ekranach, md:w-[calc(50%-12px)] dla dwóch kolumn na średnich
              className="border-shadow bg-background-elevated ring-ring flex flex-col gap-4 rounded-2xl border-r-3 border-b-4 px-4 py-3 shadow-md ring cursor-pointer hover:shadow-lg transition-all duration-300 transform hover:scale-[1.02]
                         w-full sm:w-[calc(50%-0.75rem)] xl:w-[calc(33.333%-1rem)]" /* calc(50% - gap/2) */
              onClick={() => handleOpinionClick(opinion.store.slug)}
            >
              <div className="flex w-full flex-col justify-between gap-2 sm:mt-4">
                <div>
                  <h3 className="text-2xl font-semibold text-primary">
                    {opinion.user?.firstName} {opinion.user?.lastName}
                  </h3>
                  <p className="text-foreground text-base italic mb-2">
                    "{opinion.description}"
                  </p>
                  <p className="text-foreground text-sm">
                    <span className="font-medium">Ocena:</span> {opinion.stars} gwiazdek
                  </p>
                  <p className="text-foreground text-sm">
                    <span className="font-medium">Sklep:</span> {opinion.store.name || 'N/A'} (Slug: {opinion.store.slug})
                  </p>
                </div>
                <div className="mt-4 text-sm text-blue-500 dark:text-blue-300 font-semibold flex items-center gap-1 self-end">
                  Przejdź do sklepu opinii
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}