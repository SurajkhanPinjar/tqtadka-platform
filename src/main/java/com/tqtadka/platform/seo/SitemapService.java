package com.tqtadka.platform.seo;

import com.tqtadka.platform.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SitemapService {

    private static final String BASE_URL = "https://futorch.com/en/";

    private final PostRepository postRepository;

    public String generateSitemap() {

        List<SitemapPost> posts = postRepository.findAllPublishedPosts();

        StringBuilder xml = new StringBuilder();

        xml.append("""
        <?xml version="1.0" encoding="UTF-8"?>
        <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
        """);

        // Homepage
        xml.append("""
        <url>
        <loc>https://futorch.com/en</loc>
        <changefreq>daily</changefreq>
        <priority>1.0</priority>
        </url>
        """);

        // Category pages
        xml.append("""
        <url>
        <loc>https://futorch.com/en/learn-ai</loc>
        <changefreq>weekly</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/en/ai-tools</loc>
        <changefreq>weekly</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/en/ai-at-work</loc>
        <changefreq>weekly</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/en/ai-news</loc>
        <changefreq>daily</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/en/ai-by-industry</loc>
        <changefreq>weekly</changefreq>
        <priority>0.8</priority>
        </url>
        <url>
        <loc>https://futorch.com/en/ai-future</loc>
        <changefreq>weekly</changefreq>
        <priority>0.8</priority>
        </url>
        """);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        for (SitemapPost post : posts) {

            xml.append("<url>");

            xml.append("<loc>")
                    .append(BASE_URL)
                    .append(post.getCategory().name().toLowerCase().replace("_","-"))
                    .append("/")
                    .append(post.getSlug())
                    .append("</loc>");

            if (post.getPublishedAt() != null) {
                xml.append("<lastmod>")
                        .append(post.getPublishedAt().format(formatter))
                        .append("</lastmod>");
            }

            xml.append("<changefreq>weekly</changefreq>");
            xml.append("<priority>0.8</priority>");

            xml.append("</url>");
        }

        xml.append("</urlset>");

        return xml.toString();
    }
}