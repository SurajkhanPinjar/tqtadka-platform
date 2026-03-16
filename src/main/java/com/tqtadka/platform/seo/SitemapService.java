package com.tqtadka.platform.seo;

import com.tqtadka.platform.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SitemapService {

    private static final String BASE_URL = "https://futorch.com/";

    private final PostRepository postRepository;

    public String generateSitemap() {

        List<SitemapPost> posts = postRepository.findAllPublishedPosts();

        StringBuilder xml = new StringBuilder();

        xml.append("""
        <?xml version="1.0" encoding="UTF-8"?>
        <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
        """);

        // Homepages
        xml.append("""
        <url>
        <loc>https://futorch.com/en</loc>
        <changefreq>daily</changefreq>
        <priority>1.0</priority>
        </url>
        <url>
        <loc>https://futorch.com/kn</loc>
        <changefreq>daily</changefreq>
        <priority>1.0</priority>
        </url>
        """);

        // Category pages (EN)
        xml.append("""
        <url>
        <loc>https://futorch.com/en/ai</loc>
        <changefreq>weekly</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/en/social-media</loc>
        <changefreq>weekly</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/en/jobs</loc>
        <changefreq>daily</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/en/career</loc>
        <changefreq>weekly</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/en/schemes</loc>
        <changefreq>daily</changefreq>
        <priority>0.9</priority>
        </url>
        """);

        // Category pages (KN)
        xml.append("""
        <url>
        <loc>https://futorch.com/kn/ai</loc>
        <changefreq>weekly</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/kn/social-media</loc>
        <changefreq>weekly</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/kn/jobs</loc>
        <changefreq>daily</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/kn/career</loc>
        <changefreq>weekly</changefreq>
        <priority>0.9</priority>
        </url>
        <url>
        <loc>https://futorch.com/kn/schemes</loc>
        <changefreq>daily</changefreq>
        <priority>0.9</priority>
        </url>
        """);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        for (SitemapPost post : posts) {

            xml.append("<url>");

            xml.append("<loc>")
                    .append(BASE_URL)
                    .append(post.getLang())
                    .append("/")
                    .append(post.getCategory().name().toLowerCase().replace("_", "-"))
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