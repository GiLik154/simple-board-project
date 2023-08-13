package com.simple.post.domain.post.service;

import com.simple.post.domain.post.domain.Post;
import com.simple.post.domain.post.domain.PostRepository;
import com.simple.post.exception.NotFoundPostException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostDeleterTest {
    private final PostDeleter postDeleter;
    private final PostRepository postRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private final FileDeleter fileDeleter;

    @Autowired
    public PostDeleterTest(PostDeleter postDeleter, PostRepository postRepository, PasswordEncoder bCryptPasswordEncoder, FileDeleter fileDeleter) {
        this.postDeleter = postDeleter;
        this.postRepository = postRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.fileDeleter = fileDeleter;
    }

    @Test
    void 게시물_삭제_정상작동() throws IOException {
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now());
        newPost.uploadFile("test.jpg", "files/testCompany/test.jpg");
        postRepository.save(newPost);

        postDeleter.delete(newPost.getId(), "testPassword");

        verify(fileDeleter).delete("files/testCompany/test.jpg");
        assertFalse(postRepository.existsById(newPost.getId()));
    }

    @Test
    void 게시물_삭제_Id_다름() throws IOException {
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now());
        newPost.uploadFile("test.jpg", "files/testCompany/test.jpg");
        postRepository.save(newPost);
        Long wrongId = newPost.getId() + 100000000L;

        NotFoundPostException e = assertThrows(NotFoundPostException.class, () ->
                postDeleter.delete(wrongId, "testPassword"));

        verifyNoInteractions(fileDeleter);
        assertEquals("The requested post was not found.", e.getMessage());
        assertTrue(postRepository.existsById(newPost.getId()));
    }
}