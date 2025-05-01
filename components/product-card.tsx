import Image from "next/image";
import React from "react";

import { FragmentOf, graphql, readFragment } from "@/graphql";
import Link from "next/link";

export const productCardFragment = graphql(`
  fragment ProductCard on OwnProduct {
    product {
      name
    }
    price
    quantity

    shop {
      name
    }
  }
`);

// TODO: Add imageUrl and shop slug to fragment

interface ProductCardProps {
  data: FragmentOf<typeof productCardFragment>;
}

export const ProductCard = ({ data }: ProductCardProps) => {
  const p = readFragment(productCardFragment, data);

  return (
    <Link
      className="bg-background flex w-full gap-4 rounded-2xl p-4 shadow-md"
      href="#"
    >
      <Image
        src="/placeholder.jpeg"
        alt="Product"
        width={200}
        height={200}
        className="max-w-42 rounded-xl"
      />

      <div className="mt-4 flex w-full flex-col justify-between gap-2">
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
