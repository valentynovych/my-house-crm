package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.MasterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MasterRequestRepo extends JpaRepository<MasterRequest, Long>, JpaSpecificationExecutor<MasterRequest> {
}
