package com.example.post.repository;

import com.example.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.categoryId = null where p.categoryId = :categoryId")
    int unsetCategoryForPosts(@Param("categoryId") Long categoryId);
}
