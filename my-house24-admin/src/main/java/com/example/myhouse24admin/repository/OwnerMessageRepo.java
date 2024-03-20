package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.Message;
import com.example.myhouse24admin.entity.OwnerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnerMessageRepo extends JpaRepository<OwnerMessage, Long> {
    void deleteAllByMessageIdIn(List<Long> ids);
    List<OwnerMessage> findAllByMessageIn(List<Message> ids);
}
