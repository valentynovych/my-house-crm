package com.example.serviceImpl;

import com.example.entity.ContactsPage;
import com.example.entity.ServicePageBlock;
import com.example.repository.ServicePageBlockRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ServicePageServiceImplTest {
    @Mock
    private ServicePageBlockRepo servicePageBlockRepo;
    @InjectMocks
    private ServicePageServiceImpl servicePageService;

    @Test
    void getServicePageBlocks_Should_Return_ServicePageBlocks() {
        when(servicePageBlockRepo.findAll((Pageable) any()))
                .thenReturn(new PageImpl<>(List.of(new ServicePageBlock())));

        Page<ServicePageBlock> servicePageBlocks = servicePageService.getServicePageBlocks(0,1);
        assertThat(servicePageBlocks).hasSize(1);

        verify(servicePageBlockRepo, times(1)).findAll((Pageable) any());
        verifyNoMoreInteractions(servicePageBlockRepo);
    }
}