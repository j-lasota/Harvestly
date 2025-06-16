import { HttpLink, from } from "@apollo/client";
import {
  registerApolloClient,
  ApolloClient,
  InMemoryCache,
} from "@apollo/client-integration-nextjs";
import { setContext } from "@apollo/client/link/context";
import { auth } from "@/auth";


const authLink = setContext(async (_, { headers }) => {
  const session = await auth();
  const token = session?.accessToken;

  return {
    headers: {
      ...headers,
      authorization: token ? `Bearer ${token}` : "",
    },
  };
});

const httpLink = new HttpLink({
  uri: `${process.env.NEXT_PUBLIC_GRAPHQL}`,
});

export const { getClient, query, PreloadQuery } = registerApolloClient(() => {
  return new ApolloClient({
    cache: new InMemoryCache(),
    link: from([authLink, httpLink]),
  });
});