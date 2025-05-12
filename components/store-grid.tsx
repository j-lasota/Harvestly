import { DirectionAwareHover } from "@/components/store-card";

import placeholder from "@/public/placeholder.jpeg";
import { Link } from "@/i18n/navigation";

interface StoreGridProps {
  title: string;
  stores: Store[] | null;
}

interface Store {
  id: string;
  name: string;
  slug: string | null;
}

const StoreGrid = ({ title, stores }: StoreGridProps) => {
  return (
    <section>
      <h1 className="mb-4 text-2xl font-medium sm:text-3xl lg:text-4xl">
        {title}
      </h1>
      <div className="grid grid-cols-2 gap-6 lg:grid-cols-3 lg:gap-10">
        {/* // TODO: Uncomment when data is ready */}
        {/* {stores && stores.length > 0 ? (
          stores.map((store) => (
            <DirectionAwareHover imageUrl={placeholder} key={store.id}>
              <Link href={`/store/${store.slug}`}>
                <p className="text-xl font-bold">{store.name}</p>
              </Link>
            </DirectionAwareHover>
          ))
        ) : (
          <div className="col-span-3 py-10 text-center text-lg">
            Brak sklepów
          </div>
        )} */}

        <DirectionAwareHover imageUrl={placeholder}>
          <p className="text-xl font-bold">Sklep 1</p>
        </DirectionAwareHover>

        <DirectionAwareHover imageUrl={placeholder}>
          <p className="text-xl font-bold">Sklep 1</p>
        </DirectionAwareHover>

        <DirectionAwareHover imageUrl={placeholder}>
          <p className="text-xl font-bold">Sklep 1</p>
        </DirectionAwareHover>
      </div>
    </section>
  );
};

export default StoreGrid;
