package com.simple.post.controller;

import com.simple.post.controller.advice.FileExceptionHandler;
import com.simple.post.controller.advice.PasswordExceptionHandler;
import com.simple.post.controller.advice.PostExceptionHandler;
import com.simple.post.domain.post.domain.Post;
import com.simple.post.domain.post.domain.PostRepository;
import com.simple.post.domain.post.service.FileDeleter;
import com.simple.post.domain.post.service.FileUploader;
import com.simple.post.enums.ErrorMessages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PageControllerUpdateTest {
    private final PageController pageController;
    private final PostRepository postRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final PostExceptionHandler postExceptionHandler;
    private final FileExceptionHandler fileExceptionHandler;
    private final PasswordExceptionHandler passwordExceptionHandler;

    @MockBean
    private final FileConverter fileConverter;

    @MockBean
    private final FileUploader fileUploader;

    @MockBean
    private final FileDeleter fileDeleter;

    private MockMvc mockMvc;

    @Autowired
    public PageControllerUpdateTest(PageController pageController, PostRepository postRepository, PasswordEncoder bCryptPasswordEncoder, PostExceptionHandler postExceptionHandler, FileExceptionHandler fileExceptionHandler, PasswordExceptionHandler passwordExceptionHandler, FileConverter fileConverter, FileUploader fileUploader, FileDeleter fileDeleter) {
        this.pageController = pageController;
        this.postRepository = postRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.postExceptionHandler = postExceptionHandler;
        this.fileExceptionHandler = fileExceptionHandler;
        this.passwordExceptionHandler = passwordExceptionHandler;
        this.fileConverter = fileConverter;
        this.fileUploader = fileUploader;
        this.fileDeleter = fileDeleter;
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(pageController)
                .setControllerAdvice(postExceptionHandler, fileExceptionHandler, passwordExceptionHandler)
                .build();
    }

    @AfterEach
    public void deleteFile() throws IOException {
        String path = "files/testCompany/";
        File folder = new File(path);

        Path uploadPath = Paths.get(path);
        Files.createDirectories(uploadPath);
        File[] deleteFolderList = folder.listFiles();

        for (File f : deleteFolderList) {
            f.delete();
        }

        folder.delete();
    }

    @Test
    void Get_게시물_업데이트_정상작동() throws Exception {
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", "testPassword", LocalDate.now());
        postRepository.save(newPost);

        MockHttpServletRequestBuilder builder = get("/update")
                .param("postId", String.valueOf(newPost.getId()));

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("thymeleaf/update-page"));
    }

    @Test
    void Post_게시물_업데이트_정상작동_원래파일_없음_새로운파일_없음() throws Exception {
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now());
        postRepository.save(newPost);

        given(fileConverter.convert(any(MultipartFile.class))).willReturn(new File("test.jpg"));
        given(fileUploader.upload(anyString(), any(File.class))).willReturn("fileSaved");

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", null, (byte[]) null);

        MockHttpServletRequestBuilder builder = multipart("/update/" + newPost.getId())
                .file(mockMultipartFile)
                .param("title", "updateControllerTitle")
                .param("content", "updateContent")
                .param("password", "testPassword");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("thymeleaf/update-check"));

        List<Post> posts = postRepository.findAllByTitle("updateControllerTitle");
        Post post = posts.get(posts.size() - 1);

        verify(fileConverter).convert(null);
        verifyNoInteractions(fileDeleter);
        verifyNoInteractions(fileUploader);

        assertEquals("updateControllerTitle", post.getTitle());
        assertEquals("updateContent", post.getContent());
        assertNull(post.getFilePath());
        assertNull(post.getFileName());
    }

    @Test
    void Post_게시물_업데이트_정상작동_파일_없음() throws Exception {
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now());
        newPost.uploadFile("test.jpg", "/files/testCompany/test.jpg");
        postRepository.save(newPost);

        given(fileConverter.convert(any(MultipartFile.class))).willReturn(new File("test.jpg"));
        given(fileUploader.upload(anyString(), any(File.class))).willReturn("fileSaved");

        MockMultipartFile testFile = new MockMultipartFile("file", "test.jpg", null, (byte[]) null);

        MockHttpServletRequestBuilder builder = multipart("/update/" + newPost.getId())
                .file(testFile)
                .param("title", "updateControllerTitle")
                .param("content", "updateContent")
                .param("password", "testPassword");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("thymeleaf/update-check"));

        List<Post> posts = postRepository.findAllByTitle("updateControllerTitle");
        Post post = posts.get(posts.size() - 1);

        verify(fileConverter).convert(null);
        verify(fileDeleter).delete("/files/testCompany/test.jpg");
        verifyNoInteractions(fileUploader);

        assertEquals("updateControllerTitle", post.getTitle());
        assertEquals("updateContent", post.getContent());
        assertNull(post.getFilePath());
        assertNull(post.getFileName());
    }

    @Test
    void Post_게시물_생성_정상작동_파일_있음() throws Exception {
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now());
        newPost.uploadFile("test.jpg", "/files/testCompany/test.jpg");
        postRepository.save(newPost);

        given(fileConverter.convert(any(MultipartFile.class))).willReturn(new File("test.jpg"));
        given(fileUploader.upload(anyString(), any(File.class))).willReturn("fileSaved");

        MockMultipartFile testFile = new MockMultipartFile("multipartFile", "test.jpg", "image/jpeg", (byte[]) null);

        MockHttpServletRequestBuilder builder = multipart("/update/" + newPost.getId())
                .file(testFile)
                .param("title", "updateControllerTitle")
                .param("content", "updateContent")
                .param("password", "testPassword");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("thymeleaf/update-check"));

        List<Post> posts = postRepository.findAllByTitle("updateControllerTitle");
        Post post = posts.get(posts.size() - 1);

        verify(fileConverter).convert(testFile);
        verify(fileDeleter).delete("/files/testCompany/test.jpg");
        verify(fileUploader).upload("testCompany", new File("test.jpg"));

        assertEquals("updateControllerTitle", post.getTitle());
        assertEquals("updateContent", post.getContent());
        assertNotNull(post.getFilePath());
        assertEquals("test.jpg", post.getFileName());
    }

    @Test
    void Post_게시물_생성_정상작동_ID_다름() throws Exception {
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now());
        newPost.uploadFile("test.jpg", "files/testCompany/test.jpg");
        postRepository.save(newPost);

        given(fileConverter.convert(any(MultipartFile.class))).willReturn(new File("test.jpg"));
        given(fileUploader.upload(anyString(), any(File.class))).willReturn("fileSaved");

        MockMultipartFile testFile = new MockMultipartFile("multipartFile", "test.jpg", "image/jpeg", (byte[]) null);

        MockHttpServletRequestBuilder builder = multipart("/update/" + newPost.getId() + 1000000L)
                .file(testFile)
                .param("title", "updateControllerTitle")
                .param("content", "updateContent")
                .param("password", "testPassword");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", "게시물을 찾을 수 없습니다."))
                .andExpect(forwardedUrl("thymeleaf/error-page"));

        List<Post> posts = postRepository.findAllByTitle("updateControllerTitle");

        verify(fileConverter).convert(testFile);
        verifyNoInteractions(fileDeleter);
        verifyNoInteractions(fileUploader);

        assertTrue(posts.isEmpty());
    }

    @Test
    void Post_게시물_생성_정상작동_비밀번호_다름() throws Exception {
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now());
        newPost.uploadFile("test.jpg", "files/testCompany/test.jpg");
        postRepository.save(newPost);

        given(fileConverter.convert(any(MultipartFile.class))).willReturn(new File("test.jpg"));
        given(fileUploader.upload(anyString(), any(File.class))).willReturn("fileSaved");

        MockMultipartFile testFile = new MockMultipartFile("multipartFile", "test.jpg", "image/jpeg", (byte[]) null);

        MockHttpServletRequestBuilder builder = multipart("/update/" + newPost.getId())
                .file(testFile)
                .param("title", "updateControllerTitle")
                .param("content", "updateContent")
                .param("password", "wrongPassword");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", ErrorMessages.NOT_MATCH_PASSWORD_EXCEPTION.getKorean()))
                .andExpect(forwardedUrl("thymeleaf/error-page"));

        List<Post> posts = postRepository.findAllByTitle("updateControllerTitle");


        verify(fileConverter).convert(testFile);
        verifyNoInteractions(fileDeleter);
        verifyNoInteractions(fileUploader);

        assertTrue(posts.isEmpty());
    }
}