"use client";

import { useEffect, useState } from "react";

import { ProductCard, ProductCardProps } from "@/components/product-card";
import { Input } from "@/components/ui/input";

export function ProductSection({ products }: { products: ProductCardProps[] }) {
  const [productsData, setProductsData] = useState(products);
  const [input, setInput] = useState("");

  useEffect(() => {
    const filtered = products.filter(({ product }) =>
      product.name.toLowerCase().includes(input.toLowerCase())
    );
    setProductsData(filtered);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [input]);

  return (
    <section className="flex flex-col gap-8">
      <Input type="text" onChange={(e) => setInput(e.target.value)} />

      <div className="grid grid-cols-1 gap-10 md:grid-cols-2">
        {productsData.map(
          (product) =>
            productsData && (
              <ProductCard key={product.id} {...product}></ProductCard>
            )
        )}
      </div>
    </section>
  );
}
