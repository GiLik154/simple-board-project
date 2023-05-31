package com.simple.post.controller;

import com.simple.post.controller.advice.FileExceptionHandler;
import com.simple.post.controller.advice.PasswordExceptionHandler;
import com.simple.post.controller.advice.PostExceptionHandler;
import com.simple.post.domain.post.domain.Post;
import com.simple.post.domain.post.domain.PostRepository;
import com.simple.post.domain.post.service.FileDeleter;
import com.simple.post.enums.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PageControllerDeleteTest {
    private final PageController pageController;
    private final PostRepository postRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final PostExceptionHandler postExceptionHandler;
    private final FileExceptionHandler fileExceptionHandler;
    private final PasswordExceptionHandler passwordExceptionHandler;

    @MockBean
    private final FileDeleter fileDeleter;

    private MockMvc mockMvc;

    @Autowired
    public PageControllerDeleteTest(PageController pageController, PostRepository postRepository, PasswordEncoder bCryptPasswordEncoder, PostExceptionHandler postExceptionHandler, FileExceptionHandler fileExceptionHandler, PasswordExceptionHandler passwordExceptionHandler, FileDeleter fileDeleter) {
        this.pageController = pageController;
        this.postRepository = postRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.postExceptionHandler = postExceptionHandler;
        this.fileExceptionHandler = fileExceptionHandler;
        this.passwordExceptionHandler = passwordExceptionHandler;
        this.fileDeleter = fileDeleter;
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(pageController)
                .setControllerAdvice(postExceptionHandler, fileExceptionHandler, passwordExceptionHandler)
                .build();
    }

    @Test
    void Get_게시물_업데이트_정상작동() throws Exception {
        MockHttpServletRequestBuilder builder = get("/delete");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("thymeleaf/delete-page"));
    }

    @Test
    void Post_게시물_삭제_정상작동() throws Exception {
        Post newPost = new Post("deleteControlTestTitle", "testContent", "testCompany", "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now());
        newPost.uploadFile("test.jpg", "/files/testCompany/test.jpg");
        postRepository.save(newPost);

        MockHttpServletRequestBuilder builder = delete("/" + newPost.getId())
                .param("password", "testPassword");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("thymeleaf/delete-check"));

        List<Post> posts = postRepository.findAllByTitle("deleteControlTestTitle");

        verify(fileDeleter).delete("/files/testCompany/test.jpg");
        assertTrue(posts.isEmpty());
    }

    @Test
    void Post_게시물_생성_정상작동_ID_다름() throws Exception {
        Post newPost = new Post("deleteControlTestTitle", "testContent", "testCompany", "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now());
        newPost.uploadFile("test.jpg", "files/testCompany/test.jpg");
        postRepository.save(newPost);

        MockHttpServletRequestBuilder builder = delete("/" + newPost.getId() + 1000000L)
                .param("password", "testPassword");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", "게시물을 찾을 수 없습니다."))
                .andExpect(forwardedUrl("thymeleaf/error-page"));

        List<Post> posts = postRepository.findAllByTitle("deleteControlTestTitle");

        verifyNoInteractions(fileDeleter);
        assertFalse(posts.isEmpty());
    }

    @Test
    void Post_게시물_생성_정상작동_비밀번호_다름() throws Exception {
        Post newPost = new Post("deleteControlTestTitle", "testContent", "testCompany", "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now());
        newPost.uploadFile("test.jpg", "files/testCompany/test.jpg");
        postRepository.save(newPost);

        MockHttpServletRequestBuilder builder = delete("/" + newPost.getId())
                .param("password", "wrongPassword");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", ErrorMessages.NOT_MATCH_PASSWORD_EXCEPTION.getKorean()))
                .andExpect(forwardedUrl("thymeleaf/error-page"));

        List<Post> posts = postRepository.findAllByTitle("deleteControlTestTitle");

        verifyNoInteractions(fileDeleter);
        assertFalse(posts.isEmpty());
    }
}