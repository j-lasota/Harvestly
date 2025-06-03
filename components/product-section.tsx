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
// TO DO: Change function name, now it filters by price and category

export function ProductSectionWithCategoryFilter({
  products,
  categories,
}: {
  products: ProductCardProps[];
  categories: CategoryProps[];
}) {
  const [productsData, setProductsData] = useState(products);
  const [input, setInput] = useState("");
  const [minPrice, setMinPrice] = useState<string>("");
  const [maxPrice, setMaxPrice] = useState<string>("");

  const categoriesData = [...new Set(categories.map((item) => item.category))];
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  const [selectedSubCategories, setSelectedSubCategories] = useState<string[]>([]);

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

  const handleResetPriceFilter = () => {
    setMinPrice("");
    setMaxPrice("");
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
    <section className="flex flex-col gap-4">
      <div className="flex flex-col gap-4">
        {/* Search Input */}
        <Input 
          type="text" 
          placeholder="Search products..." 
          onChange={(e) => setInput(e.target.value)} 
          value={input}
        />

        <div className="flex flex-col gap-2 rounded-lg border p-4">
          <h3 className="text-sm font-medium">Price Range</h3>
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2">
              <Input
                type="number"
                placeholder="Min"
                value={minPrice}
                onChange={(e) => setMinPrice(e.target.value)}
                min="0"
                className="w-24"
              />
            </div>
            <span>to</span>
            <div className="flex items-center gap-2">
              <Input
                type="number"
                placeholder="Max"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
                min="0"
                className="w-24"
              />
            </div>
            {(minPrice || maxPrice) && (
              <Button
                variant="ghost"
                size="sm"
                onClick={handleResetPriceFilter}
                className="text-muted-foreground"
              >
                Reset
              </Button>
            )}
          </div>
        </div>

        <div className="flex w-full flex-wrap gap-2 overflow-hidden py-1">
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
                {category}
              </Button>
            ))
          )}
        </div>
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