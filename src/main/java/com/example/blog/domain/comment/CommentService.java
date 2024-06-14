package com.example.blog.domain.comment;

import com.example.blog.DataNotFoundException;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment create(Post p, String content, Member author) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(p);
        comment.setAuthor(author);
        comment.setCreateDate(LocalDateTime.now());
        commentRepository.save(comment);

        return comment;
    }

    public Comment getComment(Integer id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        }else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        commentRepository.save(comment);
    }


    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    public void vote(Comment comment, Member voter) {
        comment.addVoter(voter);
        commentRepository.save(comment);
    }

}
