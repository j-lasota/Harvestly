"use client";

import { useState } from "react";
import { useActionState } from "react";
import ImageUploader from "@/components/image-uploader";
import { Input } from "@/components/ui/input";
import { SubmitButton } from "@/components/ui/submit-button";
import {
  Select,
  SelectTrigger,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select";

import { addOwnProductAction } from "@/app/[locale]/add-store/actions";

type Product = {
  productId: string;
  basePrice: string;
  discount: string;
  quantity: string;
  imageUrl: string;
};

type ProductOption = {
  id: string;
  name: string;
  category?: string;
};

type AddProductListProps = {
  storeId: string;
  ownProducts: Product[];
  products: ProductOption[];
};

import Image from "next/image";

export default function AddProductList({
  storeId,
  ownProducts,
  products,
}: AddProductListProps) {
  const [newProduct, setNewProduct] = useState<Product>({
    productId: "",
    basePrice: "",
    discount: "",
    quantity: "",
    imageUrl: "",
  });

  const [state, action] = useActionState(addOwnProductAction, undefined);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setNewProduct((prev) => ({ ...prev, [name]: value }));
  };

  const handleImageUpload = (url: string) => {
    setNewProduct((prev) => ({ ...prev, imageUrl: url }));
  };

  // Reset form on success
  if (
    state?.success &&
    (newProduct.productId || newProduct.basePrice || newProduct.quantity)
  ) {
    setNewProduct({
      productId: "",
      basePrice: "",
      discount: "",
      quantity: "",
      imageUrl: "",
    });
  }

  return (
    <div className="flex flex-col gap-6">
      <h2 className="text-xl font-semibold">Produkty</h2>
      <ul className="space-y-2">
        {ownProducts.map((prod, idx) => (
          <li key={idx} className="flex items-center gap-4 border-b pb-2">
            <span>
              {products.find((p) => p.id === prod.productId)?.name ||
                prod.productId}
            </span>
            <span>Cena: {prod.basePrice}</span>
            <span>Rabat: {prod.discount}</span>
            <span>Ilość: {prod.quantity}</span>
            {prod.imageUrl && (
              <Image
                src={prod.imageUrl}
                alt="product"
                width={48}
                height={48}
                className="h-12 w-12 rounded object-cover"
              />
            )}
          </li>
        ))}
      </ul>
      <form
        action={action}
        className="flex flex-col gap-4 rounded-md border p-4"
      >
        <input type="hidden" name="storeId" value={storeId} />
        <input type="hidden" name="imageUrl" value={newProduct.imageUrl} />
        <label className="flex flex-col gap-1">
          Produkt:
          <Select
            value={newProduct.productId}
            onValueChange={(value) =>
              setNewProduct((prev) => ({ ...prev, productId: value }))
            }
            name="productId"
            required
          >
            <SelectTrigger className="rounded border px-2 py-1">
              <SelectValue placeholder="Wybierz produkt" />
            </SelectTrigger>
            <SelectContent>
              {products.map((p) => (
                <SelectItem key={p.id} value={p.id}>
                  {p.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </label>
        <label className="flex flex-col gap-1">
          Cena bazowa:
          <Input
            name="basePrice"
            type="number"
            min="0"
            value={newProduct.basePrice}
            onChange={handleChange}
            required
          />
          {state?.errors?.price && (
            <span className="text-xs text-red-500">
              {state.errors.price.join(", ")}
            </span>
          )}
        </label>
        {/* Rabat nie jest obsługiwany przez backend, pole zostaje tylko lokalnie */}
        <label className="flex flex-col gap-1">
          Rabat (%):
          <Input
            name="discount"
            type="number"
            min="0"
            max="100"
            value={newProduct.discount}
            onChange={handleChange}
          />
        </label>
        <label className="flex flex-col gap-1">
          Ilość:
          <Input
            name="quantity"
            type="number"
            min="1"
            value={newProduct.quantity}
            onChange={handleChange}
            required
          />
          {state?.errors?.quantity && (
            <span className="text-xs text-red-500">
              {state.errors.quantity.join(", ")}
            </span>
          )}
        </label>
        <div>
          <span>Zdjęcie produktu:</span>
          <ImageUploader
            placeholder="/food_placeholder.jpg"
            onUploaded={handleImageUpload}
          />
        </div>
        <div className="mt-2 w-full">
          {!state?.success && (
            <SubmitButton
              label="Dodaj produkt"
              pendingLabel="Dodawanie..."
              className="w-full"
            />
          )}
          {state?.message && (
            <p role="alert" className="text-primary w-full text-center">
              {state.message}
            </p>
          )}
        </div>
      </form>
    </div>
  );
}
