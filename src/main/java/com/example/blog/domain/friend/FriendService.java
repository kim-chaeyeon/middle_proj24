package com.example.blog.domain.friend;

import com.example.blog.DataNotFoundException;
import com.example.blog.domain.member.entity.Member;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
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

    public Page<Friend> getList(int page, String kw, String region) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Friend> spec = Specification.where(region(region));

        if (kw != null && !kw.trim().isEmpty()) {
            spec = spec.and(search(kw));
        }

        return friendRepository.findAll(spec, pageable);
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

// 특정 지역에 해당하는 게시글을 검색할때 필요함! 이거 없으면 그냥 전처럼 지역 상관없이 다 출력됨
// Specification을 사용하여 데이터베이스 쿼리를 동적으로 생성하기 위한 메서드 -> 이 문장은 이해가 안되.. 찌발..

    private Specification<Friend> region(String region) {
        return (root, query, cb) -> cb.equal(root.get("region"), region);
    }



    public Friend create(String title, String content, int capacity, String cuisineType, String address, String restaurantName, LocalDate meetingDate, LocalTime meetingTime, Member member, String region) {
        Friend friend = Friend.builder()
                .title(title)
                .content(content)
                .capacity(capacity)
                .cuisineType(cuisineType)
                .address(address)
                .restaurantName(restaurantName)
                .region(region)
                .meetingDate(meetingDate)
                .meetingTime(meetingTime)
                .author(member)
                .build();
        friendRepository.save(friend);

        return friend;
    }

    public Page<Friend> getList1(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        if (kw == null || kw.trim().length() == 0) {
            return friendRepository.findAll(pageable);
        }
        Specification<Friend> spec = search(kw);
        return friendRepository.findAll(spec, pageable);
    }

    public void modify(Friend friend, String title, String content, int capacity, String cuisineType, String address, String restaurantName, LocalDate meetingDate, LocalTime meetingTime) {
        friend.setTitle(title);
        friend.setContent(content);
        friend.setCapacity(capacity);
        friend.setCuisineType(cuisineType);
        friend.setAddress(address);
        friend.setRestaurantName(restaurantName);
        friend.setMeetingDate(meetingDate);
        friend.setMeetingTime(meetingTime);
        friendRepository.save(friend);
    }

    public void delete(Friend friend) {
        friendRepository.delete(friend);
    }

    public void vote(Friend friend, Member voter) {
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
                Join<Friend, Member> u1 = r.join("author", JoinType.LEFT);
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