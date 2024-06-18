package com.example.blog.domain.friend;

import com.example.blog.DataNotFoundException;
import com.example.blog.domain.restaurant.Restaurant;
import com.example.blog.domain.restaurantComment.RC;
import com.example.blog.domain.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FriendService {
    private final FriendRepository friendRepository;

    public List<Friend> getList() {
        return friendRepository.findAll();
    }

    public Friend getFriend(Integer id) {
        Optional<Friend> of = friendRepository.findById(id);
        if (of.isEmpty()) throw new DataNotFoundException("friend not found");
        return of.get();
    }

    public Friend create(String title, String content, int capacity, String cuisineType, String address, String restaurantName, SiteUser siteUser) {
        Friend friend = Friend.builder()
                .title(title)
                .content(content)
                .capacity(capacity)
                .cuisineType(cuisineType)
                .address(address)
                .restaurantName(restaurantName)
                .build();
        friendRepository.save(friend);

        return friend;
    }

    public Page<Friend> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        if (kw == null || kw.trim().length() == 0) {
            return friendRepository.findAll(pageable);
        }
        Specification<Friend> spec = search(kw);
        return friendRepository.findAll(spec, pageable);
    }

    public void modify(Friend friend, String title, String content, int capacity, String cuisineType, String address, String restaurantName) {
        friend.setTitle(title);
        friend.setContent(content);
        friend.setCapacity(capacity);
        friend.setCuisineType(cuisineType);
        friend.setAddress(address);
        friend.setRestaurantName(restaurantName);
        friendRepository.save(friend);
    }

    public void delete(Friend friend) {
        friendRepository.delete(friend);
    }

    public void vote(Friend friend, SiteUser voter) {
        friend.addVoter(voter);
        friendRepository.save(friend);
    }

    public Friend incrementParticipants(Integer id) {
        Friend friend = getFriend(id);
        if (friend.getCurrentParticipants() < friend.getCapacity()) {
            friend.setCurrentParticipants(friend.getCurrentParticipants() + 1);
            friendRepository.save(friend);
        }
        return friend;
    }

    private Specification<Friend> search(String kw) {
        return new Specification<>() {
            @Override
            public Predicate toPredicate(Root<Friend> r, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Friend, SiteUser> u1 = r.join("author", JoinType.LEFT);
//                Join<Restaurant, RC> a = r.join("rcList", JoinType.LEFT);
//                Join<RC, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(r.get("title"), "%" + kw + "%"), // 제목
                        cb.like(r.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"));    // 질문 작성자
//                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
//                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }
}

