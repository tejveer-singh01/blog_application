package com.mountblue.blog.service;

import com.mountblue.blog.entitites.Tag;
import com.mountblue.blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagService {
    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag createOrFetchTag(String tagName) {
        // Try to find the tag by its name in the database
        Tag existingTag = tagRepository.findByName(tagName);

        // If the tag exists, return it
        if (existingTag != null) {
            return existingTag;
        }

        // If the tag does not exist, create a new tag and save it to the database
        Tag newTag = new Tag();
        newTag.setName(tagName);
        return tagRepository.save(newTag);
    }

}
