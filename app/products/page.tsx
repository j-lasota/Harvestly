import { graphql } from "gql.tada";
import React from "react";

import { getClient } from "@/graphql/apollo-client";

// Test query
// const createQuery = graphql(`
//   mutation createProduct($name: String!, $category: ProductCategory!) {
//     createProduct(name: $name, category: $category) {
//       id
//       name
//     }
//   }
// `);

const getQuery = graphql(`
  query {
    products {
      id
      name
    }
  }
`);

export default async function ProductsPage() {
  // const { data: created } = await getClient().mutate({
  //   mutation: createQuery,
  //   variables: { name: "Pomidor", category: "FRUIT" },
  // });

  const { data } = await getClient().query({
    query: getQuery,
  });

  // console.log(created);
  console.log(data);

  return (
    <main>
      <p>ti ra ra ra ram pam pam pam</p>

      <div>
        {data.products &&
          data.products.map(
            (product) => product && <div key={product.id}>{product.name}</div>
          )}
      </div>
    </main>
  );
}
