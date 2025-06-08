"use client";

import { useTranslations } from "next-intl";
import { useEffect, useState } from "react";
import { X } from "lucide-react";

import { ProductCard, ProductCardProps } from "@/components/product-card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

interface CategoryProps {
  name: string;
  category: string;
}

export function ProductSection({
  products,
  categories,
}: {
  products: ProductCardProps[];
  categories?: CategoryProps[];
}) {
  const t = useTranslations("productSection");
  const [productsData, setProductsData] = useState(products);
  const [input, setInput] = useState("");
  const [minPrice, setMinPrice] = useState<string>("");
  const [maxPrice, setMaxPrice] = useState<string>("");

  const categoriesData = categories
    ? [...new Set(categories.map((item) => item.category))]
    : [];
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  const [selectedSubCategories, setSelectedSubCategories] = useState<string[]>(
    []
  );

  const handleCategoryClick = (category: string) => {
    setSelectedCategory(category);
  };

  const handleSubCategoryClick = (subCategory: string) => {
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

    if (minPrice) {
      const min = parseFloat(minPrice);
      filtered = filtered.filter((p) => p.price >= min);
    }

    if (maxPrice) {
      const max = parseFloat(maxPrice);
      filtered = filtered.filter((p) => p.price <= max);
    }

    setProductsData(filtered);
  }, [input, selectedSubCategories, minPrice, maxPrice, products]);

  return (
    <>
      <section className="border-shadow bg-background-elevated ring-ring mb-4 flex w-full flex-col gap-2 rounded-2xl border-r-3 border-b-4 px-4 py-3 shadow-md ring">
        {/* Search Input */}
        <div>
          <label className="mb-1 text-sm">{t("searchLabel")}</label>
          <Input
            type="text"
            id="search"
            placeholder={t("searchPlaceholder")}
            onChange={(e) => setInput(e.target.value)}
            value={input}
          />
        </div>

        <div className="flex items-end gap-4">
          <div>
            <p className="mb-1 text-sm">{t("priceRangeLabel")}</p>
            <div className="flex items-center gap-2">
              <Input
                type="number"
                placeholder="min"
                value={minPrice}
                onChange={(e) => setMinPrice(e.target.value)}
                min="0"
                className="max-w-20"
              />
              {t("separatePriceRange")}
              <Input
                type="number"
                placeholder="max"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
                min="0"
                className="max-w-20"
              />
            </div>
          </div>

          {categories && categories.length > 0 && (
            <div>
              <p className="mb-1 text-sm">{t("categoryLabel")}</p>
              <div className="flex w-full flex-wrap gap-2 overflow-hidden">
                {selectedCategory ? (
                  <>
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
                          variant={
                            selectedSubCategories.includes(item.name)
                              ? "default"
                              : "outline"
                          }
                        >
                          {item.name}
                        </Button>
                      ))}
                  </>
                ) : (
                  categoriesData.map((category) => (
                    <Button
                      key={category}
                      onClick={() => handleCategoryClick(category)}
                      variant="outline"
                    >
                      {t(`categories.${category}`)}
                    </Button>
                  ))
                )}
              </div>
            </div>
          )}
        </div>
      </section>

      <section className="grid grid-cols-1 gap-x-8 gap-y-6 md:grid-cols-2">
        {productsData.map(
          (product) =>
            productsData && (
              <ProductCard key={product.id} {...product}></ProductCard>
            )
        )}
      </section>
    </>
  );
}
