package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicesRepo extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {
}