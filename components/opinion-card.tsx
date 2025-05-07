import React from "react";

import { FragmentOf, graphql, readFragment } from "@/graphql";
import { Star } from "lucide-react";

export const opinionCardFragment = graphql(`
  fragment OpinionCard on Opinion {
    description
    stars
    user {
      firstName
    }
  }
`);

interface OpinionCardProps {
  data: FragmentOf<typeof opinionCardFragment>;
}

export const OpinionCard = ({ data }: OpinionCardProps) => {
  const o = readFragment(opinionCardFragment, data);

  return (
    <article className="flex w-full max-w-3xl flex-col gap-2 rounded-xl bg-white px-4 py-3 shadow-md">
      <div className="flex items-center justify-between gap-6">
        <p className="w-full border-b border-black/15 text-lg font-medium">
          {o.user.firstName}
        </p>
        <div className="flex gap-1">
          {Array.from({ length: o.stars }, (_, i) => (
            <Star
              key={i}
              size={20}
              strokeWidth={1.5}
              style={{ fill: "var(--color-primary)" }}
              className="text-primary"
            />
          ))}
          {Array.from({ length: 5 - o.stars }, (_, i) => (
            <Star
              key={i}
              size={20}
              strokeWidth={1.5}
              className="text-primary"
            />
          ))}
        </div>
      </div>

      <p>{o.description}</p>
    </article>
  );
};
