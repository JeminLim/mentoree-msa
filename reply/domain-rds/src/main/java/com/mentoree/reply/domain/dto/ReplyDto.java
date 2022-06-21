package com.mentoree.reply.domain.dto;

import com.mentoree.reply.domain.entity.Reply;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReplyDto {

    private Long replyId;

    @NotNull
    private Long boardId;

    @NotNull
    private Long writerId;

    @NotNull
    private String writerNickname;

    @NotNull
    private String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public ReplyDto(Long replyId, Long boardId, Long writerId, String writerNickname, String content, LocalDateTime modifiedDate) {
        this.replyId = replyId;
        this.writerId = writerId;
        this.boardId = boardId;
        this.writerNickname = writerNickname;
        this.content = content;
        this.modifiedDate = modifiedDate;
    }

    public Reply toEntity() {
        return Reply.builder()
                .memberId(writerId)
                .nickname(writerNickname)
                .content(content)
                .boardId(boardId)
                .build();
    }



}
