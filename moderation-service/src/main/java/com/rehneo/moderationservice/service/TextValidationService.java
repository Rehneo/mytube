package com.rehneo.moderationservice.service;

import com.rehneo.moderationservice.domain.ForbiddenWord;
import com.rehneo.moderationservice.repository.ForbiddenWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TextValidationService {
    private final ForbiddenWordRepository forbiddenWordRepository;

    public boolean containsForbiddenWord(String text) {
        List<ForbiddenWord> forbiddenWords = forbiddenWordRepository.findAll();
        String lowerCaseText = text.toLowerCase();
        return forbiddenWords.stream().anyMatch(forbiddenWord -> lowerCaseText.contains(
                forbiddenWord.getWord().toLowerCase())
        );
    }
}