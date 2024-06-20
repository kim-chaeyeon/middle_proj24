package com.example.blog.domain.restaurant;

import com.example.blog.DataNotFoundException;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.restaurantComment.RC;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;


    @Value("${custom.fileDirPath}")
    private String fileDirPath;

    public List<Restaurant> getList() {
        return restaurantRepository.findAll();
    }


    public Restaurant getRestaurant(Integer id) {
        Optional<Restaurant> or = restaurantRepository.findById(id);
        if (or.isEmpty()) throw new DataNotFoundException("restaurant not found");
        return or.get();
    }

    public Restaurant create(String title, String content, MultipartFile thumbnail, String cuisineType, String address, String restaurantName, Member member) {
        String thumbnailRelPath = "restaurant/" + UUID.randomUUID().toString() + ".jpg";
        File thumbnailFile = new File(fileDirPath + "/" + thumbnailRelPath);

        try {
            thumbnail.transferTo(thumbnailFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Restaurant restaurant = Restaurant.builder()
                .title(title)
                .content(content)
                .author(member)
                .createDate(LocalDateTime.now())
                .thumbnailImg(thumbnailRelPath)
                .cuisineType(cuisineType)
                .address(address)
                .restaurantName(restaurantName)
                .build();
        restaurantRepository.save(restaurant);

        return restaurant;
    }

    public Page<Restaurant> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        if (kw == null || kw.trim().length() == 0) {
            return restaurantRepository.findAll(pageable);
        }
        Specification<Restaurant> spec = search(kw);
        return restaurantRepository.findAll(spec, pageable);
    }

    public void modify(Restaurant restaurant, String title, String content, String cuisineType, String address, String restaurantName) {
        restaurant.setTitle(title);
        restaurant.setContent(content);
        restaurant.setModifyDate(LocalDateTime.now());
        restaurant.setCuisineType(cuisineType);
        restaurant.setAddress(address);
        restaurant.setRestaurantName(restaurantName);
        restaurantRepository.save(restaurant);
    }

    public void delete(Restaurant restaurant) {
        restaurantRepository.delete(restaurant);
    }

    public void vote(Restaurant restaurant, Member voter) {
        restaurant.addVoter(voter);
        restaurantRepository.save(restaurant);
    }

    private Specification<Restaurant> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Restaurant> r, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Restaurant, Member> u1 = r.join("author", JoinType.LEFT);
                Join<Restaurant, RC> a = r.join("rcList", JoinType.LEFT);
                Join<RC, Member> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(r.get("title"), "%" + kw + "%"), // 제목
                        cb.like(r.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }
}