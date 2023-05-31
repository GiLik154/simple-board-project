package com.simple.post.controller;

import com.simple.post.domain.post.domain.Post;
import com.simple.post.domain.post.domain.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PageControllerListTest {
    private final PageController pageController;
    private final PostRepository postRepository;

    private MockMvc mockMvc;

    @Autowired
    public PageControllerListTest(PageController pageController, PostRepository postRepository) {
        this.pageController = pageController;
        this.postRepository = postRepository;
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(pageController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void 리스트_출력_정상작동() throws Exception {
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", "testPassword", LocalDate.now());
        postRepository.save(newPost);

        MockHttpServletRequestBuilder builder = get("/")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "id,desc");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("posts"))
                .andExpect(forwardedUrl("thymeleaf/select-page"));
    }
}