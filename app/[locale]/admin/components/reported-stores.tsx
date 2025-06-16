"use client";

import { Store, Trash2, CheckCircle2 } from 'lucide-react';
import { useRouter } from "next/navigation";
import { deleteStoreAdminAction, unreportStoreAdminAction } from "../actions";
import { useTranslations } from 'next-intl'; 

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
  refetchShops: () => void;
}

export default function ReportedShopsList({ shops, refetchShops }: ReportedShopsListProps) {
  const router = useRouter();
  const t = useTranslations('page.reportedShops'); 

  const handleShopClick = (slug: string) => {
    if (slug) {
      router.push(`/store/${slug}`);
    }
  };

  const handleDeleteShop = async (shopId: string) => {
    if (!window.confirm(t('confirmDeleteShop'))) {
      return;
    }
    try {
      const result = await deleteStoreAdminAction(shopId);
      if (result.success) {
        console.log(t('shopDeletedSuccess', { shopId }));
        refetchShops();
      } else {
        alert(t('deleteShopError', { errorMessage: result.error || t('unknownError') }));
      }
    } catch (error) {
      console.error(t('deleteShopUnexpectedErrorConsole'), error);
      alert(t('deleteShopUnexpectedErrorAlert'));
    }
  };

  const handleUnreportShop = async (shopId: string) => {
    if (!window.confirm(t('confirmUnreportShop'))) {
      return;
    }
    try {
      const result = await unreportStoreAdminAction(shopId);
      if (result.success) {
        console.log(t('shopUnreportedSuccess', { shopId }));
        refetchShops();
      } else {
        alert(t('unreportShopError', { errorMessage: result.error || t('unknownError') }));
      }
    } catch (error) {
      console.error(t('unreportShopUnexpectedErrorConsole'), error);
      alert(t('unreportShopUnexpectedErrorAlert'));
    }
  };

  const nonNullShops = shops.filter(Boolean) as ReportedShop[];

  return (
    <section className="bg-background-elevated p-6 rounded-xl shadow-lg border border-shadow">
      <h2 className="text-2xl font-bold mb-6 text-primary flex items-center justify-center gap-3">
        <Store className="h-8 w-8" /> {t('sectionTitle', { count: nonNullShops.length })}
      </h2>
      {nonNullShops.length === 0 ? (
        <p className="text-foreground text-center py-4">
          {t('noReportedShops')}
        </p>
      ) : (
        <div className="flex flex-wrap gap-6 justify-center">
          {nonNullShops.map((shop: ReportedShop) => (
            <div
              key={shop.id}
              className="border-shadow bg-background-elevated ring-ring flex flex-col gap-4 rounded-2xl border-r-3 border-b-4 px-4 py-3 shadow-md ring"
            >
              <div className="flex w-full flex-col justify-between gap-2 sm:mt-4">
                <div>
                  <h3 className="text-2xl font-semibold text-primary cursor-pointer hover:underline" onClick={() => handleShopClick(shop.slug!)}>
                    {shop.name}
                  </h3>
                  <p className="text-foreground text-sm">
                    <span className="font-medium">{t('cityLabel')}:</span> {shop.city}
                  </p>
                  <p className="text-foreground text-sm">
                    <span className="font-medium">{t('addressLabel')}:</span> {shop.address}
                  </p>
                </div>
                <div className="mt-4 flex justify-between items-center gap-2">
                  <button
                    onClick={() => handleShopClick(shop.slug!)}
                    className="text-sm text-blue-500 dark:text-blue-300 font-semibold flex items-center gap-1 hover:underline transition-colors duration-200"
                  >
                    {t('goToShop')}
                  </button>
                  <div className="flex gap-2">
                    <button
                      onClick={(e) => { e.stopPropagation(); handleUnreportShop(shop.id); }}
                      className="p-2 rounded-full bg-green-500 text-white hover:bg-green-600 transition-colors duration-200"
                      title={t('unreportShopTitle')}
                    >
                      <CheckCircle2 className="h-5 w-5" />
                    </button>
                    <button
                      onClick={(e) => { e.stopPropagation(); handleDeleteShop(shop.id); }}
                      className="p-2 rounded-full bg-red-500 text-white hover:bg-red-600 transition-colors duration-200"
                      title={t('deleteShopTitle')}
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