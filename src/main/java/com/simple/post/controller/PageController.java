package com.simple.post.controller;

import com.simple.post.controller.dto.PostCreateRequest;
import com.simple.post.controller.dto.PostSearchRequest;
import com.simple.post.controller.dto.PostUpdateRequest;
import com.simple.post.domain.post.domain.Post;
import com.simple.post.domain.post.domain.PostRepository;
import com.simple.post.domain.post.service.PostCreator;
import com.simple.post.domain.post.service.PostDeleter;
import com.simple.post.domain.post.service.PostIncrement;
import com.simple.post.domain.post.service.PostUpdater;
import com.simple.post.exception.NotFoundFileException;
import com.simple.post.exception.NotFoundPostException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final PostRepository postRepository;
    private final PostCreator postCreator;
    private final PostIncrement postIncrement;
    private final PostUpdater postUpdater;
    private final PostDeleter postDeleter;
    private final FileConverter fileConverter;


    /**
     * {@link PostSearchRequest}을 통해 검색 조건을 선택
     * {@link Pageable}을 통해 페이징
     */
    @GetMapping
    public String list(PostSearchRequest postSearchRequest,
                       @PageableDefault Pageable pageable,
                       Model model) {

        Slice<Post> posts = postRepository.findAllForSearchCondition(postSearchRequest.toCommand(), pageable);

        posts.forEach(post ->
                postIncrement.increase(post.getId()));

        model.addAttribute("posts", posts);
        return "thymeleaf/select-page";
    }

    @GetMapping("/create")
    public String create() {
        return "thymeleaf/create-page";
    }

    /**
     * {@link PostCreateRequest}를 통해 Post 생성
     * {@link MultipartFile}을 통해 파일 업로드
     */
    @PostMapping
    public String create(PostCreateRequest postCreateRequest,
                         MultipartFile multipartFile) throws IOException {
        postCreator.create(postCreateRequest.toCommand(LocalDate.now()), fileConverter.convert(multipartFile));

        return "thymeleaf/create-check";
    }

    /**
     * postId로 Post를 찾아 화면단에 띄워줌.
     * 찾지 못할 경우 {@link NotFoundFileException} 발생
     */
    @GetMapping("/update")
    public String update(Long postId, Model model) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundPostException("Not Found Post"));

        model.addAttribute("post", post);

        return "thymeleaf/update-page";
    }

    /**
     * 파일 업로드 때문에 Put 메소드를 사용하지 못함.
     * postId와 password로 Post가 수정 가능한지 검증함
     * {@link PostUpdateRequest}의 정보를 통해 게시물 수정
     * {@link MultipartFile}을 통해 파일 업로드
     */
    @PostMapping("/update/{postId}")
    public String update(@PathVariable Long postId,
                         String password,
                         PostUpdateRequest postUpdateRequest,
                         MultipartFile multipartFile) throws IOException {
        postUpdater.update(postId, password, postUpdateRequest.toCommand(), fileConverter.convert(multipartFile));

        return "thymeleaf/update-check";
    }

    @GetMapping("/delete")
    public String delete() {
        return "thymeleaf/delete-page";
    }

    /**
     * postId와 password로 Post가 삭제 가능한지 검증함,.
     */
    @DeleteMapping("/{postId}")
    public String delete(@PathVariable Long postId, String password) throws IOException {
        postDeleter.delete(postId, password);

        return "thymeleaf/delete-check";
    }
}