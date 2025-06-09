import { useTranslations } from "next-intl";
import Image from "next/image";

import placeholder from "@/public/food_placeholder.jpg";
import { Link } from "@/i18n/navigation";

export interface ProductCardProps {
  id: string;
  product: {
    name: string;
  };
  price: number;
  quantity: number;
  imageUrl: string | null;
  store: {
    slug: string | null;
    name: string;
    city: string;
  };
}

export const ProductCard = ({
  product,
  price,
  quantity,
  imageUrl,
  store,
}: ProductCardProps) => {
  const t = useTranslations("productCard");

  return (
    <Link
      className="border-shadow bg-background-elevated ring-ring flex w-full flex-col gap-4 rounded-2xl border-r-3 border-b-4 px-4 py-3 shadow-md ring sm:flex-row"
      href={`/store/${store.slug}`}
    >
      <Image
        src={imageUrl || placeholder}
        alt={`Image of ${product.name}`}
        width={500}
        height={500}
        className="aspect-video w-full rounded-xl object-cover sm:aspect-square sm:max-w-42"
      />

      <div className="flex w-full flex-col justify-between gap-2 sm:mt-4">
        <div>
          <p className="text-2xl font-semibold">{product.name}</p>
          <p className="text-sm">
            {t("soldBy")}{" "}
            <span className="font-kalam text-primary">{store.name}</span>
          </p>
        </div>

        <div className="self-end">
          <p className="text-primary text-end">
            <span className="text-3xl font-semibold">{price.toFixed(2)}</span>{" "}
            PLN/pc
          </p>
          <p className="text-end text-sm">
            {t.rich("available", {
              b: (chunks) => <span className="font-medium">{chunks}</span>,
              quantity,
            })}
          </p>
        </div>
      </div>
    </Link>
  );
};
