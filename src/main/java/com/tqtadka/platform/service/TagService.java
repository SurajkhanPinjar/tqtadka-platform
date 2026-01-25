package com.tqtadka.platform.service;

import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.entity.Post;
import com.tqtadka.platform.entity.Tag;

import java.util.List;

public interface TagService {
    List<Post> getPublishedPostsByTag(String tagSlug, LanguageType language);
    Tag getBySlug(String slug);
}