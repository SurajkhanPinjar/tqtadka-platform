//package com.tqtadka.platform.seo;
//
//import com.tqtadka.platform.entity.Post;
//import com.tqtadka.platform.repository.PostRepository;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.http.MediaType;
//
//import java.util.List;
//
//@RestController
//public class SitemapController {
//
//    private final PostRepository postRepository;
//
//    public SitemapController(PostRepository postRepository) {
//        this.postRepository = postRepository;
//    }
//
//    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
//    public String generateSitemap() {
//
//        List<Post> posts = postRepository.findAllPublished();
//
//        StringBuilder xml = new StringBuilder();
//
//        xml.append("""
//                <?xml version="1.0" encoding="UTF-8"?>
//                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
//                """);
//
//        // Homepage
//        xml.append("""
//                <url>
//                <loc>https://futorch.com/en</loc>
//                </url>
//                """);
//
//        // Category Pages
//        xml.append("""
//                <url>
//                <loc>https://futorch.com/en/learn-ai</loc>
//                </url>
//                <url>
//                <loc>https://futorch.com/en/ai-tools</loc>
//                </url>
//                <url>
//                <loc>https://futorch.com/en/ai-at-work</loc>
//                </url>
//                <url>
//                <loc>https://futorch.com/en/ai-news</loc>
//                </url>
//                """);
//
//        for (Post post : posts) {
//
//            xml.append("<url>");
//            xml.append("<loc>");
//            xml.append("https://futorch.com/en/" +
//                    post.getCategorySlug() +
//                    "/" +
//                    post.getSlug());
//            xml.append("</loc>");
//            xml.append("</url>");
//        }
//
//        xml.append("</urlset>");
//
//        return xml.toString();
//    }
//}