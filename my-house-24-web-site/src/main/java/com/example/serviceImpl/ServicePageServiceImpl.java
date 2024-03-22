package com.example.serviceImpl;

import com.example.entity.ServicePageBlock;
import com.example.repository.ServicePageBlockRepo;
import com.example.service.ServicePageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServicePageServiceImpl implements ServicePageService {
    private final ServicePageBlockRepo servicePageBlockRepo;
    private final Logger logger = LogManager.getLogger(ServicePageServiceImpl.class);

    public ServicePageServiceImpl(ServicePageBlockRepo servicePageBlockRepo) {
        this.servicePageBlockRepo = servicePageBlockRepo;
    }

    @Override
    public Page<ServicePageBlock> getServicePageBlocks(int page, int pageSize) {
        logger.info("getServicePageBlocks() - Getting service page blocks");
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ServicePageBlock> servicePageBlocks = servicePageBlockRepo.findAll(pageable);
        logger.info("getServicePageBlocks() - Service page blocks were got");
        return servicePageBlocks;
    }
}
