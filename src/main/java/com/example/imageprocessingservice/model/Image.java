package com.example.imageprocessingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String url;
    private String format;
    private Long size;

    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @ManyToOne
    private User owner;

    private String transformations;

    public Image (String filename, String url, String format, Long size, User owner, String transformations) {
        this.filename = filename;
        this.url = url;
        this.format = format;
        this.size = size;
        this.owner = owner;
        this.transformations = transformations;
    }
}