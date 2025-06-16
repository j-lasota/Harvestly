package com.backend.IntegrationTests;

import com.backend.model.*;
import com.backend.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OwnProductControllerSecurityTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private StoreRepository storeRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OwnProductRepository ownProductRepository;

    private User storeOwner;
    private User anotherUser;
    private Store myStore;
    private Product globalProduct;
    private Product globalProduct1;
    private OwnProduct ownProductInMyStore;

    @BeforeEach
    void setUp() {
        ownProductRepository.deleteAll();
        storeRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        storeOwner = new User("2a6e8658-d6db-45d8-9131-e8f87b62ed75", "Owner", "User", "owner@test.com", null, 0, null);
        anotherUser = new User("another-user-id", "Another", "User", "another@test.com", null, 0, null);
        userRepository.saveAll(List.of(storeOwner, anotherUser));

        myStore = new Store(storeOwner, "My Store", "Desc", 10, 10, "City", "Addr", "img", "my-store");
        storeRepository.save(myStore);

        globalProduct = new Product("Test Product", ProductCategory.FRUIT, true);
        productRepository.save(globalProduct);
        globalProduct1 = new Product("Test Product", ProductCategory.FRUIT, true);
        productRepository.save(globalProduct1);
        ownProductInMyStore = new OwnProduct(myStore, globalProduct, new BigDecimal("9.99"), 100, "img");
        ownProductRepository.save(ownProductInMyStore);
    }

    @AfterEach
    void tearDown() {
        ownProductRepository.deleteAll();
        storeRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    // --- Metoda pomocnicza do tworzenia poprawnego kontekstu bezpieczeństwa ---
    public static RequestPostProcessor jwtWithAuthorities(String subject, List<String> permissions) {
        Collection<GrantedAuthority> authorities = permissions.stream()
                .map(p -> "SCOPE_" + p)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject(subject)
                .claim("permissions", permissions)
                .build();

        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);
        return authentication(token);
    }

    // ========================================================================
    // === Testy dla Mutacji `createOwnProduct`
    // ========================================================================

    @Test
    @DisplayName("Właściciel sklepu powinien móc dodać produkt do swojego sklepu")
    void createOwnProduct_asOwner_shouldSucceed() throws Exception {
        String payload = String.format("""
            {"query":"mutation { createOwnProduct(storeId: %d, productId: %d, price: 12.50, quantity: 50) { id store { id } } }"}
            """, myStore.getId(), globalProduct1.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(storeOwner.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createOwnProduct.id").exists());
    }

    @Test
    @DisplayName("Zwykły użytkownik NIE powinien móc dodać produktu do cudzego sklepu")
    void createOwnProduct_asAnotherUser_shouldBeForbidden() throws Exception {
        String payload = String.format("""
            {"query":"mutation { createOwnProduct(storeId: %d, productId: %d, price: 1.0, quantity: 1) { id } }"}
            """, myStore.getId(), globalProduct.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(anotherUser.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("FORBIDDEN")));
    }

    @Test
    @DisplayName("Admin powinien móc dodać produkt do dowolnego sklepu")
    void createOwnProduct_asAdmin_shouldSucceed() throws Exception {
        String payload = String.format("""
            {"query":"mutation { createOwnProduct(storeId: %d, productId: %d, price: 1.0, quantity: 1) { id } }"}
            """, myStore.getId(), globalProduct1.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities("admin-id", List.of("manage:all")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createOwnProduct.id").exists());
    }

    @Test
    void createOwnProduct_asAnonymous_shouldBeUnauthorized() throws Exception {
        String payload = String.format("""
            {"query":"mutation { createOwnProduct(storeId: %d, productId: %d, price: 1.0, quantity: 1) { id } }"}
            """, myStore.getId(), globalProduct.getId());

        mockMvc.perform(post("/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("FORBIDDEN")));
    }

    // ========================================================================
    // === Testy dla Mutacji `updateOwnProduct`
    // ========================================================================

    @Test
    @DisplayName("Właściciel sklepu powinien móc zaktualizować swój produkt")
    void updateOwnProduct_asOwner_shouldSucceed() throws Exception {
        String payload = String.format("""
            {"query":"mutation { updateOwnProduct(id: %d, price: 20.00) { id price } }"}
            """, ownProductInMyStore.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(storeOwner.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updateOwnProduct.price").value(20.00));
    }

    @Test
    @DisplayName("Zwykły użytkownik NIE powinien móc zaktualizować cudzego produktu")
    void updateOwnProduct_asAnotherUser_shouldBeForbidden() throws Exception {
        String payload = String.format("""
            {"query":"mutation { updateOwnProduct(id: %d, price: 0.01) { id } }"}
            """, ownProductInMyStore.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(anotherUser.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("FORBIDDEN")));
    }

    // ========================================================================
    // === Testy dla Mutacji `deleteOwnProduct`
    // ========================================================================

    @Test
    @DisplayName("Właściciel sklepu powinien móc usunąć swój produkt")
    void deleteOwnProduct_asOwner_shouldSucceed() throws Exception {
        String payload = String.format("""
            {"query":"mutation { deleteOwnProduct(id: %d) }"}
            """, ownProductInMyStore.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(storeOwner.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleteOwnProduct").value(true));

        assertFalse(ownProductRepository.findById(ownProductInMyStore.getId()).isPresent());
    }

    @Test
    @DisplayName("Zwykły użytkownik NIE powinien móc usunąć cudzego produktu")
    void deleteOwnProduct_asAnotherUser_shouldBeForbidden() throws Exception {
        String payload = String.format("""
            {"query":"mutation { deleteOwnProduct(id: %d) }"}
            """, ownProductInMyStore.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(anotherUser.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("FORBIDDEN")));
    }

    @Test
    @DisplayName("Admin powinien móc usunąć dowolny produkt")
    void deleteOwnProduct_asAdmin_shouldSucceed() throws Exception {
        String payload = String.format("""
            {"query":"mutation { deleteOwnProduct(id: %d) }"}
            """, ownProductInMyStore.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities("admin-id", List.of("manage:all")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleteOwnProduct").value(true));

        assertFalse(ownProductRepository.findById(ownProductInMyStore.getId()).isPresent());
    }

    // ========================================================================
    // === Testy dla Zapytań `ownProducts` (zakładamy, że są publiczne)
    // ========================================================================

    @Test
    @DisplayName("Każdy, nawet anonimowy użytkownik, powinien móc przeglądać produkty")
    void queryOwnProducts_asAnonymous_shouldSucceed() throws Exception {
        String payload = String.format("""
            {"query":"query { ownProductsByStore(storeId: %d) { id product { name } } }"}
            """, myStore.getId());

        mockMvc.perform(post("/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ownProductsByStore[0].product.name").value("Test Product"));
    }
}