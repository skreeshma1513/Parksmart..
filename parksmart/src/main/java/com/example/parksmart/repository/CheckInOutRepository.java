package com.example.parksmart.repository;

import com.example.parksmart.entity.CheckInOut;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInOutRepository extends JpaRepository<CheckInOut, Long> {
}