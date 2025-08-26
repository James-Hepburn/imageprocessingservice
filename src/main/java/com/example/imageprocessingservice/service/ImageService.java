package com.example.imageprocessingservice.service;

import com.example.imageprocessingservice.model.Image;
import com.example.imageprocessingservice.model.User;
import com.example.imageprocessingservice.repository.ImageRepository;
import com.example.imageprocessingservice.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    private String uploadDir = new File (".").getAbsolutePath () + "/uploads";

    public Image uploadImage (MultipartFile file, User owner) throws IOException {
        File dir = new File (this.uploadDir);
        if (!dir.exists ()) dir.mkdirs ();

        String filename = UUID.randomUUID () + "_" + file.getOriginalFilename ();
        File savedFile = new File (dir, filename);
        file.transferTo (savedFile);

        Image image = new Image (filename, savedFile.getAbsolutePath (), getFileExtension (file.getOriginalFilename ()), file.getSize (), owner, null);
        image.setUploadedAt (LocalDateTime.now ());

        return this.imageRepository.save (image);
    }

    public Optional <Image> getImage (Long id) {
        return this.imageRepository.findById (id);
    }

    public Page <Image> listUserImages (User owner, Pageable pageable) {
        return this.imageRepository.findAllByOwner (owner, pageable);
    }

    public Image transformImage (Image image, String format, Integer resizeW, Integer resizeH, boolean grayscale, boolean sepia, String watermark) throws IOException {
        File inputFile = new File (image.getUrl ());
        BufferedImage bufferedImage = ImageUtils.readImage (inputFile);

        if (resizeW != null && resizeH != null) {
            bufferedImage = ImageUtils.resize (bufferedImage, resizeW, resizeH);
        }
        if (grayscale) {
            bufferedImage = ImageUtils.grayscale (bufferedImage);
        }
        if (sepia) {
            bufferedImage = ImageUtils.sepia (bufferedImage);
        }
        if (watermark != null) {
            bufferedImage = ImageUtils.addWatermark (bufferedImage, watermark);
        }

        String newFilename = UUID.randomUUID () + "." + format;
        File outputFile = new File (this.uploadDir, newFilename);
        ImageUtils.writeImage (bufferedImage, format, outputFile);

        image.setFilename (newFilename);
        image.setUrl (outputFile.getAbsolutePath ());
        image.setFormat (format);
        image.setTransformations ("resize=" + resizeW + "x" + resizeH + ", grayscale=" + grayscale + ", sepia=" + sepia + ", watermark=" + (watermark != null));

        return this.imageRepository.save (image);
    }

    private String getFileExtension (String filename) {
        int dotIndex = filename.lastIndexOf (".");
        return (dotIndex == -1) ? "" : filename.substring (dotIndex + 1);
    }
}