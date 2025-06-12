package com.backend.controller;

import com.backend.model.OwnProduct;
import com.backend.model.Store;
import com.backend.repository.OwnProductRepository;
import com.backend.repository.StoreRepository;
import com.backend.service.ImageUploadService;
import com.backend.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {
    private final ImageUploadService imageUploadService;
    private final StoreService storeService;
    private final StoreRepository storeRepository;
    private final OwnProductRepository ownProductRepository;

    public ImageUploadController(ImageUploadService imageUploadService, StoreService storeService, StoreRepository storeRepository, OwnProductRepository ownProductRepository) {
        this.imageUploadService = imageUploadService;
        this.storeService = storeService;
        this.storeRepository = storeRepository;
        this.ownProductRepository = ownProductRepository;
    }

    /**
     * Endpoint to upload and set an image for a store.
     *
     * @param file the image file to upload
     * @param id   the ID of the store
     * @return the URL of the uploaded image or an error message
     */
    @PostMapping("/stores/{id}/image")
    public ResponseEntity<String> uploadStoreImage(@RequestParam("file") MultipartFile file,
                                                   @PathVariable("id") Long id) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            return ResponseEntity.badRequest().body("Nieprawidłowy plik.");
        }

        Optional<Store> storeOpt = storeService.getStoreById(id);
        if (storeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shop not found.");
        }
        try {
            String imageUrl = imageUploadService.uploadImage(file);
            Store store = storeOpt.get();
            store.setImageUrl(imageUrl);
            storeRepository.save(store);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint to upload and set an image for a product.
     *
     * @param id   the ID of the product
     * @param file the image file to upload
     * @return the URL of the uploaded image or an error message
     */
    @PostMapping("/products/{id}/image")
    public ResponseEntity<?> uploadProductImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            return ResponseEntity.badRequest().body("Nieprawidłowy plik.");
        }

        Optional<OwnProduct> productOpt = ownProductRepository.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produkt nie znaleziony.");
        }

        try {
            String imageUrl = imageUploadService.uploadImage(file);
            OwnProduct product = productOpt.get();
            product.setImageUrl(imageUrl);
            ownProductRepository.save(product);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint to upload an image without associating it with a specific store or product.
     *
     * @param file the image file to upload
     * @return the URL of the uploaded image or an error message
     */
    @PostMapping("/images")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            return ResponseEntity.badRequest().body("Nieprawidłowy plik.");
        }
        try {
            String imageUrl = imageUploadService.uploadImage(file);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
