package com.example.serviceImpl;

import com.example.entity.ServicePageBlock;
import com.example.repository.ServicePageBlockRepo;
import com.example.service.ServicePageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServicePageServiceImpl implements ServicePageService {
    private final ServicePageBlockRepo servicePageBlockRepo;

    public ServicePageServiceImpl(ServicePageBlockRepo servicePageBlockRepo) {
        this.servicePageBlockRepo = servicePageBlockRepo;
    }

    @Override
    public Page<ServicePageBlock> getServicePageBlocks(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ServicePageBlock> servicePageBlocks = servicePageBlockRepo.findAll(pageable);
        return servicePageBlocks;
    }
}
