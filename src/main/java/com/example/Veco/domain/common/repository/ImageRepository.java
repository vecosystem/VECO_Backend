package com.example.Veco.domain.common.repository;

import com.example.Veco.domain.common.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
