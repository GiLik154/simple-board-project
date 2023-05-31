package com.simple.post.domain.post.service;

public interface PostIncrement {
    /**
     * PostId를 통해서 Post의 조회 카운트를 증가시킴.
     *
     * @param postId 조회 카운트를 증가시킬 Post의 고유키
     */
    void increase(Long postId);
}
