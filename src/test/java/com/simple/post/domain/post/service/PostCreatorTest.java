package com.simple.post.domain.post.service;

import com.simple.post.domain.post.domain.Post;
import com.simple.post.domain.post.domain.PostRepository;
import com.simple.post.domain.post.service.command.PostCreatorCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostCreatorTest {
    private final PostCreator postCreator;
    private final PostRepository postRepository;

    @MockBean
    private final FileUploader fileUploader;

    @Autowired
    public PostCreatorTest(PostCreator postCreator, PostRepository postRepository, FileUploader fileUploader) {
        this.postCreator = postCreator;
        this.postRepository = postRepository;
        this.fileUploader = fileUploader;
    }

    @Test
    void 게시물_생성_정상작동_파일_없음() throws IOException {
        File file = null;

        given(fileUploader.upload(anyString(), any(File.class))).willReturn("fileSaved");

        PostCreatorCommand postCreatorCommand = new PostCreatorCommand("testServiceTitle", "testContent", "testCompany", "testResistant", "testPassword", LocalDate.now());
        postCreator.create(postCreatorCommand, file);

        List<Post> posts = postRepository.findAllByTitle("testServiceTitle");
        Post post = posts.get(posts.size() - 1);

        verifyNoMoreInteractions(fileUploader);
        assertNotNull(post.getId());
        assertEquals("testCompany", post.getCompany());
        assertEquals("testResistant", post.getRegistrant());
        assertNotEquals("testPassword", post.getPassword());
        assertEquals("testServiceTitle", post.getTitle());
        assertEquals("testContent", post.getContent());
        assertNull(post.getFilePath());
        assertNull(post.getFileName());
        assertEquals(LocalDate.now(), post.getRegistrationDate());
    }

    @Test
    void 게시물_생성_정상작동_파일_있음() throws IOException {
        File file = new File("test.jpg");

        given(fileUploader.upload(anyString(), any(File.class))).willReturn("fileSaved");

        PostCreatorCommand postCreatorCommand = new PostCreatorCommand(
                "testServiceTitle", "testContent", "testCompany",
                "testResistant", "testPassword", LocalDate.now()
        );

        postCreator.create(postCreatorCommand, file);

        List<Post> posts = postRepository.findAllByTitle("testServiceTitle");
        Post post = posts.get(posts.size() - 1);

        verify(fileUploader).upload("testCompany", file);
        assertNotNull(post.getId());
        assertEquals("testCompany", post.getCompany());
        assertEquals("testResistant", post.getRegistrant());
        assertNotEquals("testPassword", post.getPassword());
        assertEquals("testServiceTitle", post.getTitle());
        assertEquals("testContent", post.getContent());
        assertEquals("fileSaved", post.getFilePath());
        assertEquals("test.jpg", post.getFileName());
        assertEquals(LocalDate.now(), post.getRegistrationDate());
    }
}

