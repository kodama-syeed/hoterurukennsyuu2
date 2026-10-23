package com.example.samuraitravel_1.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel_1.entity.House;

public interface HouseRepository extends JpaRepository<House, Integer> {

	public Page<House> findByNameLike(String keyword, Pageable pageable);

	public Page<House> findByNameLikeOrAddressLike(String nameKeyword, String addressKeyword, Pageable pageable);

	public Page<House> findByAddressLike(String area, Pageable pageable);

	public Page<House> findByPriceLessThanEqual(Integer price, Pageable pageable);

	public Page<House> findByNameLikeOrderByCreatedAtDesc(String nameKeywird, String addressKeyword, Pageable pageable);

	public Page<House> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword,
			Pageable pageable);

	public Page<House> findByAddressLikeOrderByCreatedAtDesc(String nameKeyword, Pageable pageable);

	public Page<House> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);

	public Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer proce, Pageable pageable);

	public Page<House> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);

	public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);

	public Page<House> findAllByOrderByPriceAsc(Pageable pageable);

	public Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword,
			Pageable pageable);

	public List<House> findTop10ByOrderByCreatedAtDesc();

}
