import com.tqtadka.platform.entity.Post;
import jakarta.persistence.*;

@Entity
@Table(name = "post_image_sections")
public class PostImageSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private String heading;

    @Column(length = 2000)
    private String description;

    private String imageUrl;

    private int displayOrder;
}