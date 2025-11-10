package com.example.post.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryEventsListener {

    private final PostService postService;

    @RabbitListener(queues = "post-service.category-events")
    public void onCategoryDeleted(CategoryDeletedEvent event) {
        // 멱등성/검증은 실제 운영 시 보강
        postService.unsetCategoryForPosts(event.getCategoryId());
    }

    @Data
    public static class CategoryDeletedEvent {
        private String eventId;
        private String eventType;
        private int eventVersion;
        private Long categoryId;
        private String occurredAt;
    }
}


