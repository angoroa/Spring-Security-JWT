package org.example.springjwt.repository;

import org.example.springjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
// DB에 접근할 수 있는 Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
}
