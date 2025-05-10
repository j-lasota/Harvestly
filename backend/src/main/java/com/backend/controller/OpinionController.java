package com.backend.controller;

import com.backend.model.Opinion;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.service.OpinionService;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class OpinionController {
    private final OpinionService opinionService;
    private final StoreService storeService;
    private final UserService userService;

    public OpinionController(OpinionService opinionService, StoreService storeService, UserService userService) {
        this.opinionService = opinionService;
        this.storeService = storeService;
        this.userService = userService;
    }

    @QueryMapping
    public List<Opinion> opinions() {
        return opinionService.getAllOpinions();
    }

    @QueryMapping
    public Optional<Opinion> opinionById(@Argument Long id) {
        return opinionService.getOpinionById(id);
    }

    @MutationMapping
    public Opinion createOpinion(@Argument Long storeId, @Argument Long userId, @Argument String description, @Argument Integer stars) {
        Optional<Store> shop = storeService.getStoreById(storeId);
        Optional<User> user = userService.getUserById(userId);
        if(shop.isEmpty()) {
            throw new IllegalArgumentException("Shop not found");
        }
        if(user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return opinionService.saveOpinion(new Opinion(shop.get(), user.get(), description, stars));
    }

    @MutationMapping
    public Boolean deleteOpinion(@Argument Long id) {
        return opinionService.deleteOpinionById(id);
    }

    @MutationMapping
    public Opinion updateOpinion(@Argument Long id, @Argument String description, @Argument Integer stars) {
        return opinionService.updateOpinion(id, description, stars);
    }
}
