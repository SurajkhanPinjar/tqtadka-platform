package com.tqtadka.platform.service;

import com.tqtadka.platform.dto.DashboardPostView;
import com.tqtadka.platform.dto.DashboardStats;
import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.Post;
import com.tqtadka.platform.entity.Role;
import com.tqtadka.platform.entity.User;
import com.tqtadka.platform.repository.PostRepository;
import com.tqtadka.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public DashboardStats getStats(User user) {

        boolean isAdmin = user.getRole() == Role.ADMIN;
        LocalDateTime now = LocalDateTime.now();

        return DashboardStats.builder()
                .totalPosts(
                        isAdmin
                                ? postRepository.countPublishedPosts()
                                : postRepository.countByCreatedBy(user)
                )
                .totalViews(
                        isAdmin
                                ? postRepository.totalViews()
                                : postRepository.totalViewsByCreatedBy(user)
                )
                .viewsThisMonth(
                        isAdmin
                                ? postRepository.viewsFrom(now.withDayOfMonth(1))
                                : postRepository.viewsFromByCreatedBy(
                                now.withDayOfMonth(1), user)
                )
                .viewsThisWeek(
                        isAdmin
                                ? postRepository.viewsFrom(now.minusDays(7))
                                : postRepository.viewsFromByCreatedBy(
                                now.minusDays(7), user)
                )
                .viewsToday(
                        isAdmin
                                ? postRepository.viewsFrom(
                                now.toLocalDate().atStartOfDay())
                                : postRepository.viewsFromByCreatedBy(
                                now.toLocalDate().atStartOfDay(), user)
                )
                .build();
    }

    public List<DashboardPostView> filterPosts(
            User user,
            CategoryType category,
            Long authorId,
            String sort,
            String dir
    ) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime monthStart = now
                .withDayOfMonth(1)
                .toLocalDate()
                .atStartOfDay();

        LocalDateTime lastMonthStart = monthStart.minusMonths(1);

        User authorFilter = null;

        // ---------- AUTHOR FILTER ----------
        if (user.getRole() == Role.ADMIN) {
            if (authorId != null) {
                authorFilter = userRepository.findById(authorId).orElse(null);
            }
        } else {
            authorFilter = user; // author sees only own posts
        }

        // ---------- DB SORT (ONLY REAL COLUMNS) ----------
        Sort dbSort = Sort.by(Sort.Direction.DESC, "publishedAt");

        if ("publishedAt".equals(sort)) {
            Sort.Direction direction =
                    "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
            dbSort = Sort.by(direction, "publishedAt");
        }

        // ---------- FETCH ----------
        List<DashboardPostView> posts =
                postRepository.findDashboardPosts(
                        category,
                        authorFilter,
                        monthStart,
                        lastMonthStart,
                        dbSort
                );

        // ---------- JAVA SORT (COMPUTED FIELDS) ----------
        Comparator<DashboardPostView> comparator = null;

        boolean asc = "asc".equalsIgnoreCase(dir);

        switch (sort) {
            case "viewsMonth" ->
                    comparator = Comparator.comparing(DashboardPostView::getViewsThisMonth);
            case "totalViews" ->
                    comparator = Comparator.comparing(DashboardPostView::getTotalViews);
        }

        if (comparator != null) {
            if (!asc) comparator = comparator.reversed();
            posts.sort(comparator);
        }

        return posts;
    }
}