package com.simple.post.domain.post.domain;

import com.simple.post.domain.post.domain.command.PostSearchCommand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 조회 조건 설정.
     *
     * @param postSearchCommand 조회 조건이 담긴 Command
     * @param pageable          페이징
     * @return 조건에 맞게 검색되 Posts
     */
    default Slice<Post> findAllForSearchCondition(PostSearchCommand postSearchCommand, Pageable pageable) {
        Specification<Post> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (validCondition(postSearchCommand.getCompany())) { // null
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("company"), postSearchCommand.getCompany())));
            }

            if (validCondition(postSearchCommand.getRegistrant())) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("registrant"), postSearchCommand.getRegistrant())));
            }
            predicates.add(criteriaBuilder.and(criteriaBuilder.between(root.get("registrationDate"), postSearchCommand.getStartDate(), postSearchCommand.getEndDate())));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return findAll(specification, pageable);
    }

    private boolean validCondition(String condition) {
        return condition != null && !condition.isEmpty();
    }

    Slice<Post> findAll(Specification<Post> specification, Pageable pageable);

    /**
     * 조회 카운트를 올리기 위한 Select문
     * 동시성 이슈를 막기 위해 Lock 사용
     *
     * @param postId 조회할 postId
     * @return 카운트를 올릴 Post
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :postId")
    Optional<Post> findByIdWithLock(@Param("postId") Long postId);

    List<Post> findAllByTitle(String title);
}