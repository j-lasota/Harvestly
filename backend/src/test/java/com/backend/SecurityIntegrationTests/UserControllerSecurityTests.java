package com.backend.SecurityIntegrationTests;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerSecurityTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private StoreRepository storeRepository;

    private User testUser;
    private User anotherUser;
    private Store testStore;

    @BeforeEach
    @Transactional
    void setUp() {

        storeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        userRepository.flush();

        testUser = new User("test-user-id", "Test", "User", "test@user.com", null, 0, null);
        anotherUser = new User("another-user-id", "Another", "User", "another@user.com", null, 0, null);
        userRepository.saveAll(List.of(testUser, anotherUser));

        testStore = new Store(anotherUser, "Test Store", "Desc", 0,0,"City","Addr","img","slug");
        storeRepository.save(testStore);
    }

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

    @Test
    void updateUser_asSelf_shouldSucceed() throws Exception {
        String payload = String.format("""
            {"query":"mutation { updateUser(id: \\"%s\\", firstName: \\"Updated\\") { id firstName } }"}
            """, testUser.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(testUser.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updateUser.firstName").value("Updated"));
    }

    @Test
    void updateUser_asAnotherUser_shouldBeForbidden() throws Exception {
        String payload = String.format("""
            {"query":"mutation { updateUser(id: \\"%s\\", firstName: \\"Hacked\\") { id } }"}
            """, testUser.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(anotherUser.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("FORBIDDEN")));
    }

    @Test
    void updateUser_asAdmin_shouldSucceed() throws Exception {
        String payload = String.format("""
            {"query":"mutation { updateUser(id: \\"%s\\", tier: 1) { id tier } }"}
            """, testUser.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities("admin-id", List.of("manage:all")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updateUser.tier").value(1));
    }

    @Test
    void deleteUser_asNormalUser_shouldBeForbidden() throws Exception {
        String payload = String.format("""
            {"query":"mutation { deleteUser(id: \\"%s\\") }"}
            """, anotherUser.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(testUser.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("FORBIDDEN")));
    }

    @Test
    void deleteUser_asAdmin_shouldSucceed() throws Exception {
        String payload = String.format("""
            {"query":"mutation { deleteUser(id: \\"%s\\") }"}
            """, anotherUser.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities("admin-id", List.of("manage:all")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleteUser").value(true));
    }

    @Test
    void addFavoriteStore_asSelf_shouldSucceed() throws Exception {
        String payload = String.format("""
            {"query":"mutation { addFavoriteStore(userId: \\"%s\\", storeId: %d) { id favoriteStores { id } } }"}
            """, testUser.getId(), testStore.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(testUser.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addFavoriteStore.favoriteStores[0].id").value(testStore.getId()));
    }

    @Test
    void addFavoriteStore_forAnotherUser_shouldBeForbidden() throws Exception {
        String payload = String.format("""
            {"query":"mutation { addFavoriteStore(userId: \\"%s\\", storeId: %d) { id } }"}
            """, anotherUser.getId(), testStore.getId()); // Próbujemy dodać do ulubionych innego użytkownika

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(testUser.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("FORBIDDEN")));
    }

    @Test
    void userById_asAuthenticated_shouldSucceed() throws Exception {
        String payload = String.format("""
            {"query":"query { userById(id: \\"%s\\") { id } }"}
            """, testUser.getId());

        mockMvc.perform(post("/graphql")
                        .with(jwtWithAuthorities(anotherUser.getId(), List.of()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userById.id").value(testUser.getId()));
    }
}