import { Star } from "lucide-react";
import React from "react";

export interface OpinionCardProps {
  id: string;
  description: string | null;
  stars: number;
  user: {
    firstName: string;
  };
}

export const OpinionCard = ({ description, stars, user }: OpinionCardProps) => {
  return (
    <article className="flex w-full max-w-3xl flex-col gap-2 rounded-xl bg-white px-4 py-3 shadow-md">
      <div className="flex items-center justify-between gap-6">
        <p className="w-full border-b border-black/15 text-lg font-medium">
          {user.firstName}
        </p>
        <div className="flex gap-1">
          {Array.from({ length: stars }, (_, i) => (
            <Star
              key={i}
              size={20}
              strokeWidth={1.5}
              style={{ fill: "var(--color-primary)" }}
              className="text-primary"
            />
          ))}
          {Array.from({ length: 5 - stars }, (_, i) => (
            <Star
              key={i}
              size={20}
              strokeWidth={1.5}
              className="text-primary"
            />
          ))}
        </div>
      </div>

      {description && <p>{description}</p>}
    </article>
  );
};
