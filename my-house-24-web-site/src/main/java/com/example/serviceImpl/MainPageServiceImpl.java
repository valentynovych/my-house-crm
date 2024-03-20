package com.example.serviceImpl;

import com.example.entity.ContactsPage;
import com.example.entity.MainPage;
import com.example.entity.MainPageBlock;
import com.example.mapper.MainPageMapper;
import com.example.model.mainPage.MainPageResponse;
import com.example.repository.ContactsPageRepo;
import com.example.repository.MainPageBlockRepo;
import com.example.repository.MainPageRepo;
import com.example.service.MainPageService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainPageServiceImpl implements MainPageService {
    private final MainPageRepo mainPageRepo;
    private final MainPageBlockRepo mainPageBlockRepo;
    private final ContactsPageRepo contactsPageRepo;
    private final MainPageMapper mainPageMapper;
    private final Logger logger = LogManager.getLogger(MainPageServiceImpl.class);

    public MainPageServiceImpl(MainPageRepo mainPageRepo, MainPageBlockRepo mainPageBlockRepo,
                               ContactsPageRepo contactsPageRepo, MainPageMapper mainPageMapper) {
        this.mainPageRepo = mainPageRepo;
        this.mainPageBlockRepo = mainPageBlockRepo;
        this.contactsPageRepo = contactsPageRepo;
        this.mainPageMapper = mainPageMapper;
    }

    @Override
    public MainPageResponse getMainPageResponse() {
        logger.info("getMainPageResponse() - Getting main page response");
        MainPage mainPage = mainPageRepo.findById(1L).orElseThrow(()-> new EntityNotFoundException("Main page was not found by id 1"));
        List<MainPageBlock> mainPageBlocks = mainPageBlockRepo.findAll();
        ContactsPage contactsPage = contactsPageRepo.findById(1L).orElseThrow(()-> new EntityNotFoundException("Contacts page was not found by id 1"));
        MainPageResponse mainPageResponse = mainPageMapper.mainPageToMainPageResponse(mainPage,
                mainPageBlocks, contactsPage);
        logger.info("getMainPageResponse() - Main page response was got");
        return mainPageResponse;
    }
}
