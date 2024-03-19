package com.example.myhouse24user.repository;

import com.example.myhouse24user.entity.MasterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterRequestRepo extends JpaRepository<MasterRequest, Long>, JpaSpecificationExecutor<MasterRequest> {
}
