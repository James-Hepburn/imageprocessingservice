package com.example.imageprocessingservice.repository;

import com.example.imageprocessingservice.model.Image;
import com.example.imageprocessingservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository <Image, Long> {
    Page <Image> findAllByOwner (User owner, Pageable pageable);
}