package com.backend.SecurityIntegrationTests;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.ImageUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ImageUploadControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @MockBean
    private ImageUploadService imageUploadService;

    private User storeOwner;
    private Store myStore;

    @BeforeEach
    void setUp() throws IOException {
        storeRepository.deleteAll();
        userRepository.deleteAll();

        storeOwner = new User("owner-id", "Owner", "User", "owner@test.com", null, 0, null);
        userRepository.save(storeOwner);

        myStore = new Store(storeOwner, "My Store", "Desc", 10, 10, "City", "Addr", null, "my-store");
        storeRepository.save(myStore);

        when(imageUploadService.uploadImage(any())).thenReturn("http://mock.url/image.jpg");
    }
    public static RequestPostProcessor jwtWithAuthorities(String subject) {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject(subject)
                .build();

        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("DUMMY_AUTHORITY")));
        return authentication(token);
    }

    @Test
    void uploadStoreImage_asAuthenticatedUser_shouldSucceed() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

        mockMvc.perform(multipart("/api/upload/stores/{id}/image", myStore.getId())
                        .file(imageFile)
                        .with(jwtWithAuthorities(storeOwner.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("http://mock.url/image.jpg"));
    }

    @Test
    void uploadStoreImage_asAnonymous_shouldBeUnauthorized() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

        mockMvc.perform(multipart("/api/upload/stores/{id}/image", myStore.getId())
                        .file(imageFile))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadProductImage_asAnonymous_shouldBeUnauthorized() throws Exception {
         long ownProductId = 1L;
         MockMultipartFile imageFile = new MockMultipartFile("file", "prod.jpg", MediaType.IMAGE_JPEG_VALUE, "content".getBytes());
         mockMvc.perform(multipart("/api/upload/products/{id}/image", ownProductId)
                         .file(imageFile))
                 .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadImage_asAuthenticatedUser_shouldSucceed() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "public.jpg", MediaType.IMAGE_JPEG_VALUE, "public image".getBytes());

        mockMvc.perform(multipart("/api/upload/images")
                        .file(imageFile)
                        .with(jwtWithAuthorities(storeOwner.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("http://mock.url/image.jpg"));
    }
}