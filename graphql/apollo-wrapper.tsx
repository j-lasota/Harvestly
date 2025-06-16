"use client"; 
import { setContext } from "@apollo/client/link/context";
import { HttpLink, from } from "@apollo/client";
import { getSession } from "next-auth/react"; 
import {
  ApolloNextAppProvider,
  ApolloClient,
  InMemoryCache,
} from "@apollo/client-integration-nextjs";


function makeClient() {
  const httpLink = new HttpLink({
    uri: `${process.env.NEXT_PUBLIC_GRAPHQL}`,
  });
  
  const authLink = setContext(async (_, { headers }) => {
    const session = await getSession(); 
    const token = session?.accessToken; 

    return {
      headers: {
        ...headers,
        authorization: token ? `Bearer ${token}` : "",
      },
    };
  });

  return new ApolloClient({
    cache: new InMemoryCache(),
    link: from([authLink, httpLink]),
  });
}

export function ApolloWrapper({ children }: React.PropsWithChildren) {
  return (
    <ApolloNextAppProvider makeClient={makeClient}>
      {children}
    </ApolloNextAppProvider>
  );
}
