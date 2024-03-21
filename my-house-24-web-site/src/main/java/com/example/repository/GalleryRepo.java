package com.example.repository;

import com.example.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryRepo extends JpaRepository<Gallery, Long> {
}
