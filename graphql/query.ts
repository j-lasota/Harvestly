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

const allProductsQueryMap = graphql(`
  query AllProducts {
    ownProducts {
      id
      product {
        name
      }
      store {
        id
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
      id
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
      slug
      city
      address
      latitude
      longitude
      imageUrl
      description
      verified
      user {
        id
        email
        phoneNumber
        facebookNickname
      }
      verifications {
        user {
          id
        }
      }
      storeReports {
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
        opinionReports {
          user {
            id
          }
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

const storeBySlugEditQuery = graphql(`
  query storeBySlugEdit($slug: String!) {
    storeBySlug(slug: $slug) {
      id
      name
      city
      address
      latitude
      longitude
      imageUrl
      description
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

const hasUserStoresQuery = graphql(`
  query hasUserStores($userId: ID!) {
    userById(id: $userId) {
      stores {
        id
      }
      tier
    }
  }
`);

const allShopsLocationsQuery = graphql(`
  query AllShopsLocations {
    stores {
      id
      latitude
      longitude
      name
      city
      description
      address
      imageUrl
      slug
      verified
      businessHours {
        dayOfWeek
        openingTime
        closingTime
      }
    }
  }
`);

const userByIdQuery = graphql(`
  query userById($id: ID!) {
    userById(id: $id) {
      id
      email
      firstName
      lastName
      phoneNumber
      facebookNickname
      nip
      publicTradePermitNumber
      img
    }
  }
`);

const allReportedShopsQuery = graphql(`
  query storesReported {
    storesReported {
      id
      name
      description
      latitude
      longitude
      city
      address
      imageUrl
      verified
      slug
      reported
    }
  }
`);

const allReportedOpinionsQuery = graphql(`
  query opinionsReported {
    opinionsReported {
      id
      description
      stars
      reported
      user {
        firstName
        lastName
      }
      store {
        id
        name
        slug
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
  hasUserStoresQuery,
  allShopsLocationsQuery,
  userByIdQuery,
  allProductsQueryMap,
  allReportedShopsQuery,
  storeBySlugEditQuery,
  allReportedOpinionsQuery,
};
