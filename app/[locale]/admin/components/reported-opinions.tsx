"use client";

import { MessageSquareText, Trash2, CheckCircle2 } from 'lucide-react';
import { useRouter } from "next/navigation";
import { deleteOpinionAdminAction, unreportOpinionAdminAction } from "../actions";
import { useTranslations } from 'next-intl';

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
  reported: boolean | null;
}

interface ReportedOpinionsListProps {
  opinions: (ReportedOpinion | null)[];
  refetchOpinions: () => void;
}

export default function ReportedOpinionsList({ opinions, refetchOpinions }: ReportedOpinionsListProps) {
  const router = useRouter();
  const t = useTranslations('page.reportedOpinions'); // Inicjalizacja hooka useTranslations

  const handleOpinionClick = (storeSlug: string | null) => {
    if (storeSlug) {
      router.push(`/store/${storeSlug}`);
    }
  };

  const handleDeleteOpinion = async (opinionId: string) => {
    if (!window.confirm(t('confirmDeleteOpinion'))) { // Użyj tłumaczenia
      return;
    }
    try {
      const result = await deleteOpinionAdminAction(opinionId);
      if (result.success) {
        console.log(t('opinionDeletedSuccess', { opinionId })); // Użyj tłumaczenia
        refetchOpinions();
      } else {
        alert(t('deleteOpinionError', { errorMessage: result.error || t('unknownError') })); // Użyj tłumaczenia
      }
    } catch (error) {
      console.error(t('deleteOpinionUnexpectedErrorConsole'), error); // Użyj tłumaczenia w konsoli
      alert(t('deleteOpinionUnexpectedErrorAlert')); // Użyj tłumaczenia w alercie
    }
  };

  const handleUnreportOpinion = async (opinionId: string) => {
    if (!window.confirm(t('confirmUnreportOpinion'))) { // Użyj tłumaczenia
      return;
    }
    try {
      const result = await unreportOpinionAdminAction(opinionId);
      if (result.success) {
        console.log(t('opinionUnreportedSuccess', { opinionId })); // Użyj tłumaczenia
        refetchOpinions();
      } else {
        alert(t('unreportOpinionError', { errorMessage: result.error || t('unknownError') })); // Użyj tłumaczenia
      }
    } catch (error) {
      console.error(t('unreportOpinionUnexpectedErrorConsole'), error); // Użyj tłumaczenia w konsoli
      alert(t('unreportOpinionUnexpectedErrorAlert')); // Użyj tłumaczenia w alercie
    }
  };

  const nonNullOpinions = opinions.filter(Boolean) as ReportedOpinion[];

  return (
    <section className="bg-background-elevated p-6 rounded-xl shadow-lg border border-shadow">
      <h2 className="text-2xl font-bold mb-6 text-primary flex items-center justify-center gap-3">
        <MessageSquareText className="h-8 w-8" /> {t('sectionTitle', { count: nonNullOpinions.length })}
      </h2>
      {nonNullOpinions.length === 0 ? (
        <p className="text-foreground text-center py-4">
          {t('noReportedOpinions')}
        </p>
      ) : (
        <div className="flex flex-wrap gap-6 justify-center">
          {nonNullOpinions.map((opinion: ReportedOpinion) => (
            <div
              key={opinion.id}
              className="border-shadow bg-background-elevated ring-ring flex flex-col gap-4 rounded-2xl border-r-3 border-b-4 px-4 py-3 shadow-md ring"
            >
              <div className="flex w-full flex-col justify-between gap-2 sm:mt-4">
                <div>
                  <h3 className="text-2xl font-semibold text-primary cursor-pointer hover:underline" onClick={() => handleOpinionClick(opinion.store.slug)}>
                    {opinion.user?.firstName} {opinion.user?.lastName}
                  </h3>
                  <p className="text-foreground text-base italic mb-2">
                    "{opinion.description}"
                  </p>
                  <p className="text-foreground text-sm">
                    <span className="font-medium">{t('ratingLabel')}:</span> {opinion.stars} {t('starsLabel')}
                  </p>
                  <p className="text-foreground text-sm">
                    <span className="font-medium">{t('storeLabel')}:</span> {opinion.store.name || t('notAvailable')} (Slug: {opinion.store.slug || t('notAvailable')})
                  </p>
                </div>
                <div className="mt-4 flex justify-between items-center gap-2">
                  <button
                    onClick={() => handleOpinionClick(opinion.store.slug!)}
                    className="text-sm text-blue-500 dark:text-blue-300 font-semibold flex items-center gap-1 hover:underline transition-colors duration-200"
                  >
                    {t('goToOpinionStore')}
                  </button>
                  <div className="flex gap-2">
                    <button
                      onClick={(e) => { e.stopPropagation(); handleUnreportOpinion(opinion.id); }}
                      className="p-2 rounded-full bg-green-500 text-white hover:bg-green-600 transition-colors duration-200"
                      title={t('unreportOpinionTitle')} // Użyj tłumaczenia
                    >
                      <CheckCircle2 className="h-5 w-5" />
                    </button>
                    <button
                      onClick={(e) => { e.stopPropagation(); handleDeleteOpinion(opinion.id); }}
                      className="p-2 rounded-full bg-red-500 text-white hover:bg-red-600 transition-colors duration-200"
                      title={t('deleteOpinionTitle')} // Użyj tłumaczenia
                    >
                      <Trash2 className="h-5 w-5" />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}