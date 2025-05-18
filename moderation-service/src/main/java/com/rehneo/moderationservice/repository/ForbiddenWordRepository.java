package com.rehneo.moderationservice.repository;

import com.rehneo.moderationservice.domain.ForbiddenWord;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForbiddenWordRepository extends JpaRepository<ForbiddenWord, Integer> {
    @NotNull List<ForbiddenWord> findAll();
}