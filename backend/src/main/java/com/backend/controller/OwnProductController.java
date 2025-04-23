package com.backend.controller;

import com.backend.model.OwnProduct;
import com.backend.service.ownProductService;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class OwnProductController {
    private final ownProductService ownProductService;
    public OwnProductController(ownProductService ownProductService) {
        this.ownProductService = ownProductService;
    }

    @SchemaMapping(typeName="Product", field="ownProducts")
    public List<OwnProduct> getOwnProducts(Long id) {
        return ownProductService.getByProduct(id);
    }
}
