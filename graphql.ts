import { initGraphQLTada } from "gql.tada";

import { introspection } from "@/types/graphql-env";

export const graphql = initGraphQLTada<{
  introspection: introspection;
  scalars: {
    BigDecimal: number;
    Boolean: boolean;
    Float: number;
    ID: string;
    Int: number;
    LocalTime: string;
    String: string;
  };
}>();

export type { FragmentOf, ResultOf, VariablesOf } from "gql.tada";
export { readFragment } from "gql.tada";
