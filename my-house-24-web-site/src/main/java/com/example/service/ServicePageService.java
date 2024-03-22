package com.example.service;

import com.example.entity.ServicePageBlock;
import org.springframework.data.domain.Page;

public interface ServicePageService {
    Page<ServicePageBlock> getServicePageBlocks(int page, int pageSize);
}
