package com.mentoree.reply.domain;

import com.mentoree.reply.domain.dto.ReplyDto;
import com.mentoree.reply.domain.entity.Reply;
import com.mentoree.reply.domain.repository.ReplyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ReplyRepositoryTest {

    @Autowired
    ReplyRepository replyRepository;

    private Reply replyA;
    private Reply replyB;

    @BeforeEach
    void setUp() {

        replyA = Reply.builder()
                .nickname("testA")
                .memberId(1L)
                .boardId(1L)
                .content("test reply")
                .build();

        replyB = Reply.builder()
                .nickname("testB")
                .memberId(1L)
                .boardId(1L)
                .content("test reply B")
                .build();

        replyA = replyRepository.save(replyA);
        replyB = replyRepository.save(replyB);
    }

    @Test
    @DisplayName("모든_댓글_불러오기")
    void 모든_댓글_불러오기() {
        //given
        //when
        List<ReplyDto> allReplies = replyRepository.findAllReplyDtoByBoardId(1L);
        //then
        assertThat(allReplies.size()).isEqualTo(2);
        assertThat(allReplies.get(0).getReplyId()).isEqualTo(replyA.getId());
        assertThat(allReplies.get(1).getReplyId()).isEqualTo(replyB.getId());
    }



}
