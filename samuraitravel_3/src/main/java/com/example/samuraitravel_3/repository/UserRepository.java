package com.example.samuraitravel_3.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel_3.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	public User findByEmail(String email);

}
