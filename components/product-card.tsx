import Image from "next/image";
import Link from "next/link";
import React from "react";

import { FragmentOf, graphql, readFragment } from "@/graphql";
import placeholder from "@/public/placeholder.jpeg";

export const productCardFragment = graphql(`
  fragment ProductCard on OwnProduct {
    product {
      name
    }
    price
    quantity
    imageUrl
    shop {
      name
    }
  }
`);

interface ProductCardProps {
  data: FragmentOf<typeof productCardFragment>;
}

export const ProductCard = ({ data }: ProductCardProps) => {
  const p = readFragment(productCardFragment, data);

  return (
    <Link
      className="bg-background flex w-full flex-col gap-4 rounded-2xl border-r-2 border-b-4 border-[#fcf2de] p-4 shadow-md sm:flex-row"
      href="#"
    >
      <Image
        src={p.imageUrl || placeholder}
        alt={`Image of ${p.product.name}`}
        className="aspect-video w-full rounded-xl object-cover sm:aspect-square sm:max-w-42"
      />

      <div className="flex w-full flex-col justify-between gap-2 sm:mt-4">
        <div>
          <p className="text-2xl font-semibold">{p.product.name}</p>
          <p className="text-sm">
            Sold by{" "}
            <span className="font-kalam text-primary">{p.shop.name}</span>
          </p>
        </div>

        <div className="self-end">
          <p className="text-primary text-end">
            <span className="text-3xl font-semibold">{p.price.toFixed(2)}</span>{" "}
            PLN/pc
          </p>
          <p className="text-end text-sm">Available: {p.quantity}pcs</p>
        </div>
      </div>
    </Link>
  );
};
