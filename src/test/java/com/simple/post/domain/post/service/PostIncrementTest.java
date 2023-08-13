package com.simple.post.domain.post.service;

import com.simple.post.domain.post.domain.Post;
import com.simple.post.domain.post.domain.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostIncrementTest {
    private final PostIncrement postIncrement;
    private final PostRepository postRepository;

    @Autowired
    public PostIncrementTest(PostIncrement postIncrement, PostRepository postRepository) {
        this.postIncrement = postIncrement;
        this.postRepository = postRepository;
    }

    @Test
    void 게시물_조회카운트_정상작동() {
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", "testPassword", LocalDate.now());
        Post increasePost = postRepository.save(newPost);

        postIncrement.increase(newPost.getId());

        assertEquals(newPost.getId(), increasePost.getId());
        assertEquals(1, increasePost.getViewCount());
    }

    @Test
    void 게시물_조회카운트_반복조회() {
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", "testPassword", LocalDate.now());
        Post increasePost = postRepository.save(newPost);

        postIncrement.increase(newPost.getId());

        for (int i = 0; i < 5; i++) {
            postIncrement.increase(newPost.getId());
        }

        assertEquals(newPost.getId(), increasePost.getId());
        assertEquals(6, increasePost.getViewCount());
    }
}