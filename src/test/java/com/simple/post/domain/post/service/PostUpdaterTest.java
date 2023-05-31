package com.simple.post.domain.post.service;

import com.simple.post.domain.post.domain.Post;
import com.simple.post.domain.post.domain.PostRepository;
import com.simple.post.domain.post.service.command.PostUpdaterCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostUpdaterTest {
    private final PostUpdater postUpdater;
    private final PostRepository postRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private final FileUploader fileUploader;

    @MockBean
    private final FileDeleter fileDeleter;

    @Autowired
    public PostUpdaterTest(PostUpdater postUpdater, PostRepository postRepository, PasswordEncoder bCryptPasswordEncoder, FileUploader fileUploader, FileDeleter fileDeleter) {
        this.postUpdater = postUpdater;
        this.postRepository = postRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.fileUploader = fileUploader;
        this.fileDeleter = fileDeleter;
    }

    @Test
    void 게시물_업데이트_정상작동() throws IOException {
        File file = new File("update.jpg");

        Post newPost = new Post(
                "testTitle", "testContent", "testCompany",
                "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now()
        );
        newPost.uploadFile("test.jpg", "files/testCompany/test.jpg");
        postRepository.save(newPost);

        given(fileUploader.upload(anyString(), any(File.class))).willReturn("updateFilePath");

        PostUpdaterCommand postUpdaterCommand = new PostUpdaterCommand("updateTitle", "updateContent");
        postUpdater.update(newPost.getId(), "testPassword", postUpdaterCommand, file);

        List<Post> posts = postRepository.findAllByTitle("updateTitle");
        Post post = posts.get(posts.size() - 1);

        verify(fileDeleter).delete("files/testCompany/test.jpg");
        verify(fileUploader).upload("testCompany", file);

        assertEquals("updateTitle", post.getTitle());
        assertEquals("updateContent", post.getContent());
        assertEquals("updateFilePath", post.getFilePath());
        assertEquals("update.jpg", post.getFileName());

    }

    @Test
    void 게시물_업데이트_정상작동_파일_없음() throws IOException {
        File file = null;
        Post newPost = new Post("testTitle", "testContent", "testCompany", "testResistant", bCryptPasswordEncoder.encode("testPassword"), LocalDate.now());
        newPost.uploadFile("test.jpg", "files/testCompany/test.jpg");
        postRepository.save(newPost);

        verifyNoInteractions(fileDeleter);
        verifyNoInteractions(fileUploader);
        PostUpdaterCommand postUpdaterCommand = new PostUpdaterCommand("updateTitle", "updateContent");
        postUpdater.update(newPost.getId(), "testPassword", postUpdaterCommand, file);
    }
}