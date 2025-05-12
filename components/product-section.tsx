"use client";

import { useEffect, useState } from "react";

import { ProductCard, ProductCardProps } from "@/components/product-card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { X } from "lucide-react";

interface CategoryProps {
  name: string;
  category: string;
}

// ! Redundance of code ProductsSection
// TODO: Combine these two components

export function ProductSection({ products }: { products: ProductCardProps[] }) {
  const [productsData, setProductsData] = useState(products);
  const [input, setInput] = useState("");

  useEffect(() => {
    const filtered = products.filter(
      (p) =>
        p.product.name.toLowerCase().includes(input.toLowerCase()) ||
        p.store.city.toLowerCase().includes(input.toLowerCase()) ||
        p.store.name.toLowerCase().includes(input.toLowerCase())
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

// ================= ProductSectionWithCategoryFilter =================
export function ProductSectionWithCategoryFilter({
  products,
  categories,
}: {
  products: ProductCardProps[];
  categories: CategoryProps[];
}) {
  const [productsData, setProductsData] = useState(products);
  const [input, setInput] = useState("");

  const categoriesData = [...new Set(categories.map((item) => item.category))];
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);

  const [selectedSubCategories, setSelectedSubCategories] = useState<string[]>(
    []
  );

  const handleCategoryClick = (category: string) => {
    setSelectedCategory(category);
  };

  const handleSubCategoryClick = (subCategory: string) => {
    console.log(subCategory);
    if (selectedSubCategories.find((name) => name === subCategory)) {
      setSelectedSubCategories((prevState) =>
        prevState.filter((item) => item !== subCategory)
      );
    } else {
      setSelectedSubCategories((prevState) => [...prevState, subCategory]);
    }
  };

  const handleResetCategory = () => {
    setSelectedCategory(null);
    setSelectedSubCategories([]);
  };

  useEffect(() => {
    let filtered = products.filter(
      (p) =>
        p.product.name.toLowerCase().includes(input.toLowerCase()) ||
        p.store.city.toLowerCase().includes(input.toLowerCase()) ||
        p.store.name.toLowerCase().includes(input.toLowerCase())
    );

    if (selectedSubCategories.length > 0) {
      filtered = filtered.filter((p) =>
        selectedSubCategories.some((subCategory) =>
          p.product.name.toLowerCase().includes(subCategory.toLowerCase())
        )
      );
    }

    setProductsData(filtered);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [input, selectedSubCategories]);

  return (
    <section className="flex flex-col gap-2">
      <Input type="text" onChange={(e) => setInput(e.target.value)} />

      <div className="mb-4 overflow-hidden py-1">
        {selectedCategory ? (
          <div className="flex w-full flex-wrap gap-2">
            <Button
              size="icon"
              onClick={handleResetCategory}
              className="shrink-0"
            >
              <X />
            </Button>

            {categories
              .filter((item) => item.category === selectedCategory)
              .map((item) => (
                <Button
                  key={item.name}
                  onClick={() => handleSubCategoryClick(item.name)}
                  variant="accent"
                >
                  {item.name}
                </Button>
              ))}
          </div>
        ) : (
          categoriesData.map((category) => (
            <Button
              key={category}
              onClick={() => handleCategoryClick(category)}
            >
              {category}
            </Button>
          ))
        )}
      </div>

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
