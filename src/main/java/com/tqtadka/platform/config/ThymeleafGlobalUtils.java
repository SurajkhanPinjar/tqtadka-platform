package com.tqtadka.platform.config;

import com.tqtadka.platform.util.TimeAgoUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ModelAttribute;

@Configuration
public class ThymeleafGlobalUtils {

    @ModelAttribute("timeAgo")
    public TimeAgoUtil timeAgo() {
        return new TimeAgoUtil();
    }
}