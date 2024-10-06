package org.example.springjwt.repository;

import org.example.springjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
// DB에 접근할 수 있는 Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    // User가 존재하는지 확인하는 메서드
    // 아래의 JPA 구문을 이용해서 User가 존재하는지 확인하기
    Boolean existsByUsername(String username);
}
