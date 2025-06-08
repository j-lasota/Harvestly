import { graphql } from "@/graphql";

const allProductsQuery = graphql(`
  query AllProducts {
    ownProducts {
      id
      product {
        name
      }
      price
      quantity
      imageUrl
      store {
        slug
        name
        city
      }
    }
  }
`);

const allCategoriesQuery = graphql(`
  query Category {
    products {
      name
      category
    }
  }
`);

const allMyStoresQuery = graphql(`
  query AllMyStores($userId: ID!) {
    userById(id: $userId) {
      stores {
        id
        name
        imageUrl
        slug
      }
      favoriteStores {
        id
        name
        imageUrl
        slug
      }
    }
  }
`);

const storeBySlugQuery = graphql(`
  query storeBySlug($slug: String!) {
    storeBySlug(slug: $slug) {
      id
      name
      city
      address
      latitude
      longitude
      imageUrl
      description
      verified
      user {
        email
      }
      verifications {
        user {
          id
        }
      }
      opinions {
        id
        description
        stars
        user {
          firstName
        }
      }
      businessHours {
        dayOfWeek
        openingTime
        closingTime
      }
      ownProducts {
        id
        product {
          name
        }
        price
        quantity
        imageUrl
        store {
          slug
          name
          city
        }
      }
    }
  }
`);

const userFavoriteStoresQuery = graphql(`
  query userFavoriteStores($id: ID!) {
    userById(id: $id) {
      favoriteStores {
        id
      }
    }
  }
`);

export {
  allProductsQuery,
  allCategoriesQuery,
  allMyStoresQuery,
  storeBySlugQuery,
  userFavoriteStoresQuery,
};
