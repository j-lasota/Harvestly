package com.backend.IntegrationTests;

import com.backend.model.Opinion;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.OpinionRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GraphQLSecurityTests {

    @Autowired MockMvc mockMvc;
    @Autowired StoreRepository storeRepo;
    @Autowired OpinionRepository opinionRepo;
    @Autowired UserRepository userRepository;

    private Store existingStore;
    private Opinion existingOpinion;

    @BeforeEach
    void setUp() {
        opinionRepo.deleteAll();
        storeRepo.deleteAll();
        userRepository.deleteAll();

        User owner = new User(
                "owner1",              // musi się zgadzać z tym, co wstawiasz jako userId
                "John",
                "Doe",
                null,                  // nickName
                "john.doe@example.com",
                "000",
                0,
                "img.jpg"
        );
        owner = userRepository.save(owner);

        existingStore = new Store();
        existingStore.setName("TestStore");
        existingStore.setDescription("Desc");
        existingStore.setCity("TestCity");
        existingStore.setAddress("TestAddress");
        existingStore.setLatitude(10.0);
        existingStore.setLongitude(20.0);
        existingStore.setImageUrl("http://img");
        existingStore.setUser(owner);
        existingStore = storeRepo.save(existingStore);

        User author = new User("author1","Author","A",null,
                "author1@example.com","987654321",0,"img");
        author = userRepository.save(author);

        existingOpinion = new Opinion();
        existingOpinion.setStore(existingStore);
        existingOpinion.setDescription("Good");
        existingOpinion.setStars(4);
        existingOpinion.setReported(false);
        existingOpinion.setUser(author);
        existingOpinion = opinionRepo.save(existingOpinion);
    }

    @Test
    void queryStores_publicAllowed() throws Exception {
        String payload = "{\"query\":\"query { stores { id name } }\"}";
        mockMvc.perform(post("/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stores[0].name").value("TestStore"));
    }

    @Test
    void createStore_withoutAuth_unauthorized() throws Exception {
        // mutation do próby stworzenia sklepu
        String payload = """
      {
        "query":"mutation { createStore(userId:\\\"someUser\\\",name:\\\"X\\\",description:\\\"D\\\",latitude:1.0,longitude:2.0,city:\\\"C\\\",address:\\\"A\\\",imageUrl:\\\"U\\\"){ id } }"
      }
      """;

        mockMvc.perform(post("/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("Unauthorized")));
    }

    @Test
    void createStore_withAuth_allowed() throws Exception {
        // 1) Utwórz nowego "czystego" właściciela, który jeszcze nie ma sklepu
        User freshOwner = new User(
                "freshOwner",
                "Foo", "Bar",
                null,
                "foo.bar@example.com",
                "001230",
                0,
                "img.jpg"
        );
        userRepository.save(freshOwner);

        // 2) Mutation prosi o tego właśnie świeżego właściciela
        String payload = "{\"query\":"
                + "\"mutation { createStore(userId:\\\"freshOwner\\\","
                + "name:\\\"NewStore\\\",description:\\\"D\\\","
                + "latitude:1.0,longitude:2.0,city:\\\"C\\\","
                + "address:\\\"A\\\",imageUrl:\\\"U\\\"){"
                + "id } }\"}";

        mockMvc.perform(post("/graphql")
                        // token subject musi odpowiadać userId
                        .with(jwt().jwt(t -> t.subject("freshOwner")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                // sprawdzamy tylko, że dostaliśmy newStore.id
                .andExpect(jsonPath("$.data.createStore.id").isNotEmpty());
    }

    @Test
    void updateStore_asOwner_allowed() throws Exception {
        String payload = String.format(
                "{\"query\":\"mutation { updateStore("
                        + "id:%d,name:\\\"UpdName\\\",description:\\\"UpdDesc\\\","
                        + "latitude:3.0,longitude:4.0,city:\\\"NewCity\\\","
                        + "address:\\\"NewAddr\\\",imageUrl:\\\"NewUrl\\\")"
                        + "{ id name city address imageUrl }}\"}",
                existingStore.getId()
        );
        mockMvc.perform(post("/graphql")
                        .with(jwt().jwt(t -> t.subject("owner1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updateStore.name").value("UpdName"))
                .andExpect(jsonPath("$.data.updateStore.city").value("NewCity"));
    }

    @Test
    void updateStore_asNonOwner_forbidden() throws Exception {
        String payload = String.format(
                "{\"query\":\"mutation { updateStore("
                        + "id:%d,name:\\\"X\\\",description:\\\"X\\\","
                        + "latitude:0.0,longitude:0.0,city:\\\"X\\\","
                        + "address:\\\"X\\\",imageUrl:\\\"X\\\")"
                        + "{ id }}\"}",
                existingStore.getId()
        );
        mockMvc.perform(post("/graphql")
                        .with(jwt().jwt(t -> t.subject("otherUser")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("Forbidden")));
    }

    @Test
    void deleteStore_asOwner_allowed() throws Exception {
        String payload = String.format(
                "{\"query\":\"mutation { deleteStore(id:%d) }\"}",
                existingStore.getId()
        );
        mockMvc.perform(post("/graphql")
                        .with(jwt().jwt(t -> t.subject("owner1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleteStore").value(true));
    }

    @Test
    void deleteStore_asNonOwner_forbidden() throws Exception {
        String payload = String.format(
                "{\"query\":\"mutation { deleteStore(id:%d) }\"}",
                existingStore.getId()
        );
        mockMvc.perform(post("/graphql")
                        .with(jwt().jwt(t -> t.subject("someoneElse")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("Forbidden")));
    }

    @Test
    void queryOpinions_publicAllowed() throws Exception {
        String payload = "{\"query\":\"query { opinions { id description stars reported } }\"}";
        mockMvc.perform(post("/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.opinions[0].description").value("Good"));
    }

    @Test
    void createOpinion_withoutAuth_unauthorized() throws Exception {
        // mutation do próby stworzenia opinii
        String payload = String.format("""
      {
        "query":"mutation { createOpinion(userId:\\\"authorX\\\",storeId:%d,description:\\\"X\\\",stars:5){ id } }"
      }
      """, existingStore.getId());

        mockMvc.perform(post("/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("Unauthorized")));
    }

    @Test
    void createOpinion_withAuth_allowed() throws Exception {
        // --- Użyj świeżego autora, nie "author1" ---
        User author2 = new User(
                "author2", "Second", "User",
                /* nick */ null,
                /* email */ "author2@example.com",
                /* phone */ "555-222",
                0, "img2.jpg"
        );
        userRepository.save(author2);

        Long sid = existingStore.getId();
        String payload = String.format(
                "{\"query\":\"mutation { createOpinion("
                        + "userId:\\\"author2\\\","
                        + "storeId:%d,"
                        + "description:\\\"X\\\","
                        + "stars:5"
                        + "){ id user { id } }}\"}",
                sid
        );

        mockMvc.perform(post("/graphql")
                        .with(jwt().jwt(t -> t.subject("author2")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createOpinion.user.id").value("author2"));
    }

    @Test
    void updateOpinion_asAuthor_allowed() throws Exception {
        String payload = String.format(
                "{\"query\":\"mutation { updateOpinion("
                        + "id:%d,description:\\\"Updated Desc\\\",stars:3,reported:true)"
                        + "{ id description stars reported }}\"}",
                existingOpinion.getId()
        );
        mockMvc.perform(post("/graphql")
                        .with(jwt().jwt(t -> t.subject("author1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updateOpinion.description").value("Updated Desc"))
                .andExpect(jsonPath("$.data.updateOpinion.stars").value(3))
                .andExpect(jsonPath("$.data.updateOpinion.reported").value(true));
    }

    @Test
    void updateOpinion_asNonAuthor_forbidden() throws Exception {
        String payload = String.format(
                "{\"query\":\"mutation { updateOpinion("
                        + "id:%d,description:\\\"X\\\",stars:1,reported:false){ id }}\"}",
                existingOpinion.getId()
        );
        mockMvc.perform(post("/graphql")
                        .with(jwt().jwt(t -> t.subject("otherUser")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("Forbidden")));
    }

    @Test
    void deleteOpinion_asAuthor_allowed() throws Exception {
        String payload = String.format(
                "{\"query\":\"mutation { deleteOpinion(id:%d) }\"}",
                existingOpinion.getId()
        );
        mockMvc.perform(post("/graphql")
                        .with(jwt().jwt(t -> t.subject("author1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleteOpinion").value(true));
    }

    @Test
    void deleteOpinion_asNonAuthor_forbidden() throws Exception {
        String payload = String.format(
                "{\"query\":\"mutation { deleteOpinion(id:%d) }\"}",
                existingOpinion.getId()
        );
        mockMvc.perform(post("/graphql")
                        .with(jwt().jwt(t -> t.subject("otherUser")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].message",
                        containsString("Forbidden")));
    }
}
