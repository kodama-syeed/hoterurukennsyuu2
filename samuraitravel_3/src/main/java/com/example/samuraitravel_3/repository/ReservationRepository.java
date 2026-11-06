package com.example.samuraitravel_3.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.samuraitravel_3.entity.Reservation;
import com.example.samuraitravel_3.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

	public Page<Reservation> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	@Query("SELECT SUM(reservationUser.numberOfPeople), reservationUser.house FROM Reservation reservationUser    GROUP BY reservationUser.house ORDER BY SUM(reservationUser.numberOfPeople) DESC")
	public List<Object[]> reservationUsers();

}
