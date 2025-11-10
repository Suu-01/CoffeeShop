package com.example.post.service;

import com.example.post.model.Post;
import com.example.post.repository.PostRepository;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange blogEventsExchange;
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    public PostService(PostRepository postRepository, RabbitTemplate rabbitTemplate, TopicExchange blogEventsExchange) {
        this.postRepository = postRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.blogEventsExchange = blogEventsExchange;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    public Post createPost(Post post) {
        Post saved = postRepository.save(post);
        // 이벤트 발행: post.created
        PostEvent evt = new PostEvent();
        evt.setEventId(UUID.randomUUID().toString());
        evt.setEventType("PostCreated");
        evt.setEventVersion(1);
        evt.setPostId(saved.getId());
        evt.setCategoryId(saved.getCategoryId());
        try {
            rabbitTemplate.convertAndSend(blogEventsExchange.getName(), "post.created", evt);
        } catch (Exception e) {
            log.warn("Failed to publish post.created event: {}", e.getMessage());
        }
        return saved;
    }

    public Post updatePost(Long id, Post updatedPost) {
        Post post = getPostById(id);
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setAuthor(updatedPost.getAuthor());
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        // 카테고리ID 확보 후 삭제
        Post existing = postRepository.findById(id).orElse(null);
        Long categoryId = existing != null ? existing.getCategoryId() : null;
        postRepository.deleteById(id);
        // 이벤트 발행: post.deleted
        PostEvent evt = new PostEvent();
        evt.setEventId(UUID.randomUUID().toString());
        evt.setEventType("PostDeleted");
        evt.setEventVersion(1);
        evt.setPostId(id);
        evt.setCategoryId(categoryId);
        try {
            rabbitTemplate.convertAndSend(blogEventsExchange.getName(), "post.deleted", evt);
        } catch (Exception e) {
            log.warn("Failed to publish post.deleted event: {}", e.getMessage());
        }
    }

    @Transactional
    public int unsetCategoryForPosts(Long categoryId) {
        return postRepository.unsetCategoryForPosts(categoryId);
    }

    @Data
    public static class PostEvent {
        private String eventId;
        private String eventType;
        private int eventVersion;
        private Long postId;
        private Long categoryId;
    }
}

