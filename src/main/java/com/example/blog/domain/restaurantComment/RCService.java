package com.example.blog.domain.restaurantComment;

import com.example.blog.DataNotFoundException;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.restaurant.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RCService {
    private final RCRepository rcRepository;

    public RC create(Restaurant r, String content, Member author) {
        RC rc = new RC();
        rc.setContent(content);
        rc.setRestaurant(r);
        rc.setAuthor(author);
        rc.setCreateDate(LocalDateTime.now());
        rcRepository.save(rc);

        return rc;
    }

    public RC getRC(Integer id) {
        Optional<RC> rc = rcRepository.findById(id);
        if (rc.isPresent()) {
            return rc.get();
        }else {
            throw new DataNotFoundException("rc not found");
        }
    }

    public void modify(RC rc, String content) {
        rc.setContent(content);
        rc.setModifyDate(LocalDateTime.now());
        rcRepository.save(rc);
    }


    public void delete(RC rc) {
        rcRepository.delete(rc);
    }

    public void vote(RC rc, Member voter) {
        rc.addVoter(voter);
        rcRepository.save(rc);
    }

}