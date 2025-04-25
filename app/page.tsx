// import Image from "next/image";

import { query } from "@/graphql/apollo-client";
import { graphql } from "gql.tada";

const CountriesQuery = graphql(`
  query Countries {
    countries {
      name
      capital
      code
      continent {
        code
        name
      }
      currency
    }
  }
`);

export default async function Home() {
  const { data } = await query({ query: CountriesQuery });

  console.log(data);

  return (
    <div className="grid min-h-screen grid-rows-[20px_1fr_20px] items-center justify-items-center gap-16 p-8 pb-20 font-[family-name:var(--font-geist-sans)] sm:p-20"></div>
  );
}
