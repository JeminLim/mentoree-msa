package com.mentoree.reply.domain.repository;

import com.mentoree.reply.domain.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long>, CustomReplyRepository {


}
