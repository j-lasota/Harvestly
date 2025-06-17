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

import { addOwnProductAction, removeOwnProduct } from "../actions";

import placeholder from "@/public/food_placeholder.jpg";

type OwnProduct = {
  id: string;
  product: {
    name: string;
  };
  price: number;
  quantity: number;
  discount: number | null;
  imageUrl: string | null;
};

type ProductOption = {
  id: string;
  name: string;
  category?: string;
};

type AddProductListProps = {
  storeId: string;
  ownProducts: OwnProduct[];
  products: ProductOption[];
};

import Image from "next/image";
import { Button } from "@/components/ui/button";
import { useTranslations } from "next-intl";

export default function AddProductList({
  storeId,
  ownProducts,
  products,
}: AddProductListProps) {
  const [image, setImage] = useState<string>("");
  const [state, action] = useActionState(addOwnProductAction, undefined);
  const t = useTranslations("page.addProduct");
  return (
    <div className="flex flex-col gap-6">
      <h2 className="text-xl font-semibold">{t('products')}</h2>
      <ul className="space-y-2">
        {ownProducts.map((prod, idx) => (
          <li key={idx} className="flex items-center gap-4 border-b pb-2">
            <span>{prod.product.name}</span>
            <span>{t('price')}{prod.price}</span>
            <span>{t('discount')} {prod.discount}%</span>
            <span>{t('quantity')} {prod.quantity}</span>
            {prod.imageUrl && (
              <Image
                src={prod.imageUrl}
                alt="product"
                width={48}
                height={48}
                className="h-12 w-12 rounded object-cover"
              />
            )}
            <Button onClick={() => removeOwnProduct(prod.id, storeId)}>
              {t('delete')}
            </Button>
          </li>
        ))}
      </ul>
      <form
        action={action}
        className="flex flex-col gap-4 rounded-md border p-4"
      >
        <input type="hidden" name="storeId" value={storeId} readOnly />
        <input type="hidden" name="imageUrl" value={image ?? ""} readOnly />

        <label className="flex flex-col gap-1">
          Produkt:
          <Select name="productId" required>
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
          {t('basePrice')}:
          <Input name="price" type="number" min="0" required />
          {state?.errors?.price && (
            <span className="text-xs text-red-500">
              {state.errors.price.join(", ")}
            </span>
          )}
        </label>

        <label className="flex flex-col gap-1">
          {t('discount')} (%):
          <Input name="discount" type="number" min="0" max="100" />
        </label>

        <label className="flex flex-col gap-1">
          {t('quantity')}:
          <Input name="quantity" type="number" min="1" required />
          {state?.errors?.quantity && (
            <span className="text-xs text-red-500">
              {state.errors.quantity.join(", ")}
            </span>
          )}
        </label>
        <div>
          <span>{t('productPhoto')}:</span>
          <ImageUploader placeholder={placeholder} onUploaded={setImage} />
        </div>
        <div className="mt-2 w-full">
            {!state?.success && (
            <SubmitButton
              label={t('addProduct')}
              pendingLabel={t('adding')}
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
