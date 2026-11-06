package com.example.samuraitravel_3.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.samuraitravel_3.entity.House;

public interface HouseRepository extends JpaRepository<House, Integer> {

	public Page<House> findByNameLike(String keyword, Pageable pageable);

	public Page<House> findByNameLikeOrAddressLike(
			String nameKeyword, String addressKeyword, Pageable pageable);

	public Page<House> findByAddressLike(String area, Pageable pageable);

	public Page<House> findByPriceLessThanEqual(Integer price, Pageable pageable);

	public Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(
			String nameKeyword, String addressKeyword, Pageable pageable);

	public Page<House> findByNameLikeOrAddressLikeOrderByPriceAsc(
			String nameKeyword, String addressKeyword, Pageable pageable);

	public Page<House> findByAddressLikeOrderByCreatedAtDesc(
			String area, Pageable pageable);

	public Page<House> findByAddressLikeOrderByPriceAsc(
			String area, Pageable pageable);

	public Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(
			Integer price, Pageable pageable);

	public Page<House> findByPriceLessThanEqualOrderByPriceAsc(
			Integer price, Pageable pageable);

	public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);

	public Page<House> findAllByOrderByPriceAsc(Pageable pageable);

	public List<House> findTop10ByOrderByCreatedAtDesc();

	// 全ホテルを総予約人数の多い順に取得
	@Query(value = "SELECT house FROM House house " +
			"LEFT JOIN Reservation reservation ON reservation.house = house " +
			"GROUP BY house " +
			"ORDER BY COALESCE(SUM(reservation.numberOfPeople), 0) DESC", countQuery = "SELECT COUNT(house) FROM House house")
	public Page<House> findAllOrderByTotalNumberOfPeopleDesc(Pageable pageable);

	// キーワード検索：総予約人数の多い順
	@Query(value = "SELECT house FROM House house " +
			"LEFT JOIN Reservation reservation ON reservation.house = house " +
			"WHERE house.name LIKE :keyword OR house.address LIKE :keyword " +
			"GROUP BY house " +
			"ORDER BY COALESCE(SUM(reservation.numberOfPeople), 0) DESC", countQuery = "SELECT COUNT(house) FROM House house "
					+
					"WHERE house.name LIKE :keyword OR house.address LIKE :keyword")
	public Page<House> findByKeywordOrderByTotalNumberOfPeopleDesc(
			@Param("keyword") String keyword, Pageable pageable);

	// エリア検索：総予約人数の多い順
	@Query(value = "SELECT house FROM House house " +
			"LEFT JOIN Reservation reservation ON reservation.house = house " +
			"WHERE house.address LIKE :area " +
			"GROUP BY house " +
			"ORDER BY COALESCE(SUM(reservation.numberOfPeople), 0) DESC", countQuery = "SELECT COUNT(house) FROM House house "
					+
					"WHERE house.address LIKE :area")
	public Page<House> findByAreaOrderByTotalNumberOfPeopleDesc(
			@Param("area") String area, Pageable pageable);

	// 価格検索：総予約人数の多い順
	@Query(value = "SELECT house FROM House house " +
			"LEFT JOIN Reservation reservation ON reservation.house = house " +
			"WHERE house.price <= :price " +
			"GROUP BY house " +
			"ORDER BY COALESCE(SUM(reservation.numberOfPeople), 0) DESC", countQuery = "SELECT COUNT(house) FROM House house "
					+
					"WHERE house.price <= :price")
	public Page<House> findByPriceOrderByTotalNumberOfPeopleDesc(
			@Param("price") Integer price, Pageable pageable);
}