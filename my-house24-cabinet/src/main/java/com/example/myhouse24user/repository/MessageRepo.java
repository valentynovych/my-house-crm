package com.example.myhouse24user.repository;

import com.example.myhouse24user.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
}
