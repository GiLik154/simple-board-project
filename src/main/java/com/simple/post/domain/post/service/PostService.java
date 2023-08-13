package com.simple.post.domain.post.service;

import com.simple.post.domain.post.domain.Post;
import com.simple.post.domain.post.domain.PostRepository;
import com.simple.post.domain.post.service.command.PostCreatorCommand;
import com.simple.post.domain.post.service.command.PostUpdaterCommand;
import com.simple.post.exception.NotFoundPostException;
import com.simple.post.exception.NotMatchPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService implements PostCreator, PostUpdater, PostDeleter, PostIncrement {
    private final PostRepository postRepository;
    private final FileUploader fileUploader;
    private final FileDeleter fileDeleter;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Override
    public void create(PostCreatorCommand command, File file) throws IOException {
        Post post = new Post(
                command.getTitle(), command.getContent(),
                command.getCompany(), command.getRegistrant(),
                bCryptPasswordEncoder.encode(command.getPassword()), command.getRegistrationDate()
        );

        uploadFile(post, command.getCompany(), file);

        postRepository.save(post);
    }

    @Override
    public void update(Long postId, String password, PostUpdaterCommand command, File file) throws IOException {
        Post post = findByIdOrThrow(postId);

        validatePassword(post, password);

        post.update(command.getTitle(), command.getContent());

        deleteFile(post.getFilePath());

        uploadFile(post, post.getCompany(), file);
    }

    @Override
    public void increase(Long postId) {
        Optional<Post> post = postRepository.findByIdWithLock(postId);

        post.ifPresent(Post::increaseViewCount);
    }

    @Override
    public void delete(Long postId, String password) throws IOException {
        Post post = findByIdOrThrow(postId);

        validatePassword(post, password);

        postRepository.delete(post);

        deleteFile(post.getFilePath());
    }

    private void uploadFile(Post post, String company, File file) throws IOException {
        if (file != null) {
            post.uploadFile(file.getName(), fileUploader.upload(company, file));
        } else {
            post.uploadFile(null, null);
        }
    }

    private void deleteFile(String filePath) throws IOException {
        if (filePath == null) {
            return;
        }
        fileDeleter.delete(filePath);
    }

    private Post findByIdOrThrow(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundPostException("The requested post was not found."));
    }

    private void validatePassword(Post post, String password) {
        if (!post.validPassword(password, bCryptPasswordEncoder)) {
            throw new NotMatchPasswordException("The password provided is incorrect.");
        }
    }
}