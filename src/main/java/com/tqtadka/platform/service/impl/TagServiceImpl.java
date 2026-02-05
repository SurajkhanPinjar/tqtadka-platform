package com.tqtadka.platform.service.impl;

import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.entity.Post;
import com.tqtadka.platform.entity.Tag;
import com.tqtadka.platform.repository.PostRepository;
import com.tqtadka.platform.repository.TagRepository;
import com.tqtadka.platform.service.TagService;
import com.tqtadka.platform.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public TagServiceImpl(PostRepository postRepository,
                          TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Post> getPublishedPostsByTag(String tagSlug, LanguageType language) {
        return postRepository.findPublishedPostsByTag(tagSlug, language);
    }

    @Override
    public Tag getBySlug(String slug) {
        return tagRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
    }

    @Transactional
    public Tag findOrCreate(String name) {
        String slug = SlugUtil.slugify(name);

        return tagRepository.findBySlug(slug)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(name);
                    tag.setSlug(slug);
                    return tagRepository.save(tag);
                });
    }
}