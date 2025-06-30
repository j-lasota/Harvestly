# Harvestly - Online Marketplace Platform

**Harvestly** is a modern web platform connecting local farmers and vendors with customers through an interactive map. Discover fresh produce, rate your favorite stalls, and manage your online presence with our secure and scalable solution built on Spring Boot and Next.js.

---

## 🚀 Key Features

*   🗺️ **Interactive Map:** Users can browse stall locations in their area using an intuitive map interface.
*   🏪 **Stall Profiles:** Each vendor can manage their own profile, add business hours, and present their product offerings with images and prices.
*   ⭐ **Review and Rating System:** Customers can rate stalls and leave reviews, building a trustworthy community.
*   ❤️ **Favorite Stalls:** A feature to save favorite vendors for easy access in the future.
*   ✅ **Community-Based Verification:** A stall verification mechanism based on user confirmations to increase vendor credibility.
*   🛡️ **Admin Panel:** Dedicated tools for administrators to manage users, verify stalls, and moderate content.
*   🔐 **Secure Authentication:** Full support for registration and login, including social providers, integrated with Auth0.

---

## 🛠️ Tech Stack

### Backend

*   **Language:** Java 17+
*   **Framework:** Spring Boot 3
*   **API:** GraphQL (`spring-boot-starter-graphql`) & REST
*   **Database:** PostgreSQL
*   **Data Access:** Spring Data JPA / Hibernate
*   **Security:** Spring Security 6 with OAuth 2.0 / JWT
*   **Authentication:** Auth0
*   **File Uploads:** Cloudinary Service Integration

### Frontend

*   **Framework:** Next.js / React
*   **Language:** TypeScript
*   **State Management:** Apollo Client (for GraphQL)
*   **Styling:** Tailwind CSS
*   **Map:** Mapbox
*   **Testing:** Jest, React Testing Library, Locust (load testing)

---

## 🏛️ Architecture & Key Concepts

*   **Decoupled Architecture:** The application runs on a distributed architecture where the frontend (SPA) communicates with a stateless backend via a secured API.
*   **Defense in Depth Security:**
    *   **External Authentication (Auth0):** Delegating identity management to a specialized provider to minimize risk.
    *   **Role-Based Access Control (RBAC):** Precise, method-level control over API operations for different user types (standard user, administrator).
    *   **Multi-Level Validation:** Protecting against invalid data at both the API layer and the database layer.
*   **Data Synchronization:** A dedicated mechanism for synchronizing user data between the local database and Auth0, utilizing webhooks and scheduled tasks.
*   **Comprehensive Testing:** The project emphasizes code quality, confirmed by extensive unit, integration, and end-to-end test coverage.

---

_This project was developed as part of a university course at the Lodz University of Technology._
