package com.example.imageprocessingservice.controller;

import com.example.imageprocessingservice.model.Image;
import com.example.imageprocessingservice.model.User;
import com.example.imageprocessingservice.repository.UserRepository;
import com.example.imageprocessingservice.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/images")
public class ImageController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity <Image> uploadImage (@RequestParam("file") MultipartFile file, Authentication authentication) throws IOException {
        User user = this.userRepository.findByUsername (authentication.getName()).orElseThrow();
        Image image = this.imageService.uploadImage (file, user);
        return ResponseEntity.ok (image);
    }

    @GetMapping("/{id}")
    public ResponseEntity <Image> getImage (@PathVariable Long id) {
        Optional <Image> image = this.imageService.getImage (id);
        return image.map (ResponseEntity::ok).orElse (ResponseEntity.notFound ().build ());
    }

    @GetMapping("/my")
    public ResponseEntity <Page <Image>> listMyImages (Authentication authentication, Pageable pageable) {
        User user = this.userRepository.findByUsername (authentication.getName ()).orElseThrow ();
        Page <Image> images = this.imageService.listUserImages (user, pageable);
        return ResponseEntity.ok (images);
    }

    @PostMapping("/{id}/transform")
    public ResponseEntity <Image> transformImage (@PathVariable Long id, @RequestParam (required = false) String format, @RequestParam (required = false) Integer resizeW, @RequestParam (required = false) Integer resizeH, @RequestParam (defaultValue = "false") boolean grayscale, @RequestParam (defaultValue = "false") boolean sepia, @RequestParam (required = false) String watermark) throws IOException {
        Image image = this.imageService.getImage (id).orElseThrow ();
        Image updated = this.imageService.transformImage (image, format != null ? format : image.getFormat (), resizeW, resizeH, grayscale, sepia, watermark);
        return ResponseEntity.ok (updated);
    }
}