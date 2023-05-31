package com.simple.post.domain.post.domain;

import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "BOARD_OGS")
public class Post {
    /** Post의 고유키 */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "boardSeqGenerator")
    @SequenceGenerator(name = "boardSeqGenerator", sequenceName = "BOARD_OGS_seq", allocationSize = 1)
    private Long id;

    /** 작성 회사 */
    @Column(length = 50, nullable = false)
    private String company;

    /** 작성자 */
    @Column(length = 50, nullable = false)
    private String registrant;

    /** 게시글의 비밀번호 */
    @Column(length = 100, nullable = false)
    private String password;

    /** 게시글 제목 */
    @Column(length = 255, nullable = false)
    private String title;

    /** 게시글 내용 */
    @Column(length = 3000, nullable = false)
    private String content;

    /** 게시글의 FilePath */
    @Column(length = 255, nullable = true)
    private String filePath;

    /** 게시글의 파일 이름 */
    @Column(length = 255, nullable = true)
    private String fileName;

    /** 생성 날짜 */
    @Column(length = 50, nullable = false)
    private LocalDate registrationDate;

    /** 조회 횟수 */
    private int viewCount;


    protected Post() {
    }

    public Post(String title, String content, String company, String registrant, String password, LocalDate registrationDate) {
        this.title = title;
        this.content = content;
        this.company = company;
        this.registrant = registrant;
        this.password = password;
        this.registrationDate = registrationDate;
    }

    public void uploadFile(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public boolean validPassword(String password, PasswordEncoder bCryptPasswordEncoder) {
        return bCryptPasswordEncoder.matches(password, this.password);
    }
}
