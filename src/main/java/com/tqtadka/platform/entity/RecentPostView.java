import com.tqtadka.platform.entity.LanguageType;

public interface RecentPostView {
    Long getId();
    String getTitle();
    String getSlug();
    LanguageType getLanguage();
    String getImageUrl();
    java.time.LocalDateTime getPublishedAt();
}