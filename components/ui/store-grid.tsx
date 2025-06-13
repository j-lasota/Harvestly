import { getTranslations } from "next-intl/server";

import { DirectionAwareHover } from "@/components/ui/store-card";
import placeholder from "@/public/store_placeholder.jpg";
import { Link } from "@/i18n/navigation";

interface StoreGridProps {
  title: string;
  stores: Store[] | null;
}

interface Store {
  id: string;
  name: string;
  imageUrl: string | null;
  slug: string | null;
}

const StoreGrid = async ({ title, stores }: StoreGridProps) => {
  const t = await getTranslations("storeGrid");

  return (
    <section>
      <h1 className="mb-4 text-2xl font-medium sm:text-3xl lg:text-4xl">
        {title}
      </h1>
      <div className="grid grid-cols-2 gap-6 lg:grid-cols-3 lg:gap-10">
        {stores && stores.length > 0 ? (
          stores.map((store) => (
            <Link href={`/store/${store.slug}`} key={store.id}>
              <DirectionAwareHover
                imageUrl={
                  store.imageUrl && store.imageUrl !== ""
                    ? store.imageUrl
                    : placeholder
                }
              >
                <p className="text-xl font-bold">{store.name}</p>
              </DirectionAwareHover>
            </Link>
          ))
        ) : (
          <div className="col-span-3 py-10 text-center text-lg">
            {t("empty")}
          </div>
        )}
      </div>
    </section>
  );
};

export default StoreGrid;
