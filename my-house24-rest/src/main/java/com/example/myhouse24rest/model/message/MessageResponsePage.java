package com.example.myhouse24rest.model.message;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class MessageResponsePage extends PageImpl<MessageResponse> {
    public MessageResponsePage(List<MessageResponse> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}
