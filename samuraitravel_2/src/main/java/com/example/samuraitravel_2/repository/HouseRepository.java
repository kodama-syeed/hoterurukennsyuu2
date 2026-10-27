package com.example.samuraitravel_2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel_2.entity.House;

public interface HouseRepository extends JpaRepository<House, Integer> {

	public Page<House> findByNameLike(String keyword, Pageable pageable);
}
