package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.MasterRequest;
import com.example.myhouse24admin.entity.MasterRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface MasterRequestRepo extends JpaRepository<MasterRequest, Long>, JpaSpecificationExecutor<MasterRequest> {

    Optional<MasterRequest> findByIdAndDeletedIsFalse(Long masterRequestId);

    @Async
    CompletableFuture<Integer> countMasterRequestsByStatus(MasterRequestStatus aNew);
}
