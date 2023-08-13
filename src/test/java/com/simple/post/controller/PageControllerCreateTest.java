package com.simple.post.controller;

import com.simple.post.domain.post.domain.Post;
import com.simple.post.domain.post.domain.PostRepository;
import com.simple.post.domain.post.service.FileUploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PageControllerCreateTest {
    private final PageController pageController;
    private final PostRepository postRepository;

    @MockBean
    private final FileConverter fileConverter;

    @MockBean
    private final FileUploader fileUploader;


    private MockMvc mockMvc;

    @Autowired
    public PageControllerCreateTest(PageController pageController, PostRepository postRepository, FileUploader fileUploader, FileConverter fileConverter) {
        this.pageController = pageController;
        this.postRepository = postRepository;
        this.fileUploader = fileUploader;
        this.fileConverter = fileConverter;
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(pageController)
                .build();
    }

    @Test
    void Get_게시물_생성_정상작동() throws Exception {
        MockHttpServletRequestBuilder builder = get("/create");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("thymeleaf/create-page"));
    }

    @Test
    void Post_게시물_생성_정상작동_파일_없음() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile("file", "test.jpg", null, (byte[]) null);

        given(fileConverter.convert(any(MultipartFile.class))).willReturn(new File("test.jpg"));
        given(fileUploader.upload(anyString(), any(File.class))).willReturn("fileSaved");

        MockHttpServletRequestBuilder builder = multipart("/")
                .file(testFile)
                .param("title", "testControllerTitle")
                .param("content", "testContent")
                .param("company", "testCompany")
                .param("registrant", "testRegistrant")
                .param("password", "testPassword");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("thymeleaf/create-check"));

        List<Post> posts = postRepository.findAllByTitle("testControllerTitle");
        Post post = posts.get(posts.size() - 1);

        verifyNoMoreInteractions(fileUploader);
        assertNotNull(post.getId());
        assertEquals("testCompany", post.getCompany());
        assertEquals("testRegistrant", post.getRegistrant());
        assertNotEquals("testPassword", post.getPassword());
        assertEquals("testControllerTitle", post.getTitle());
        assertEquals("testContent", post.getContent());
        assertNull(post.getFilePath());
        assertNull(post.getFileName());
        assertEquals(LocalDate.now(), post.getRegistrationDate());
    }

    @Test
    void Post_게시물_생성_정상작동_파일_있음() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile("multipartFile", "test.jpg", "image/jpeg", (byte[]) null);

        given(fileConverter.convert(any(MultipartFile.class))).willReturn(new File("test.jpg"));
        given(fileUploader.upload(anyString(), any(File.class))).willReturn("fileSaved");

        MockHttpServletRequestBuilder builder = multipart("/")
                .file(testFile)
                .param("title", "testControllerTitle")
                .param("content", "testContent")
                .param("company", "testCompany")
                .param("registrant", "testRegistrant")
                .param("password", "testPassword");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("thymeleaf/create-check"));

        List<Post> posts = postRepository.findAllByTitle("testControllerTitle");
        Post post = posts.get(posts.size() - 1);

        verify(fileConverter).convert(testFile);
        verify(fileUploader).upload("testCompany", new File("test.jpg"));

        assertNotNull(post.getId());
        assertEquals("testCompany", post.getCompany());
        assertEquals("testRegistrant", post.getRegistrant());
        assertNotEquals("testPassword", post.getPassword());
        assertEquals("testControllerTitle", post.getTitle());
        assertEquals("testContent", post.getContent());
        assertEquals("fileSaved", post.getFilePath());
        assertEquals("test.jpg", post.getFileName());
        assertEquals(LocalDate.now(), post.getRegistrationDate());
    }
}