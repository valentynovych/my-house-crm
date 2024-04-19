package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Section;
import com.example.myhouse24admin.mapper.SectionMapper;
import com.example.myhouse24admin.model.houses.SectionResponse;
import com.example.myhouse24admin.model.meterReadings.SectionNameResponse;
import com.example.myhouse24admin.repository.SectionRepo;
import com.example.myhouse24admin.specification.SectionSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectionServiceImplTest {

    @Mock
    private SectionRepo sectionRepo;
    @Mock
    private SectionMapper sectionMapper;
    @InjectMocks
    private SectionServiceImpl sectionService;
    private static List<Section> sections;
    private static List<SectionResponse> sectionResponses;

    @BeforeEach
    void setUp() {
        sections = new ArrayList<>();
        sectionResponses = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Section section = new Section();
            section.setId((long) i);
            section.setName("Section " + i);
            section.setRangeApartmentNumbers(i + "00-" + i + "99");
            sections.add(section);

            SectionResponse sectionResponse = new SectionResponse();
            sectionResponse.setId(section.getId());
            sectionResponse.setName(section.getName());
            sectionResponse.setRangeApartmentNumbers(section.getRangeApartmentNumbers());
            sectionResponses.add(sectionResponse);
        }
    }

    @Test
    void getSectionsByHouseId() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Section> sectionPage = new PageImpl<>(sections, pageable, 5);

        // when
        when(sectionRepo.findAll(any(SectionSpecification.class), any(Pageable.class)))
                .thenReturn(sectionPage);
        when(sectionMapper.sectionListToSectionResponseList(anyList()))
                .thenReturn(sectionResponses);

        // call the method
        Page<SectionResponse> search = sectionService.getSectionsByHouseId(1L, 0, 10, "search");

        // then
        assertFalse(search.getContent().isEmpty());
        assertEquals(5, search.getContent().size());

        verify(sectionRepo, times(1)).findAll(any(SectionSpecification.class), any(Pageable.class));
        verify(sectionMapper, times(1)).sectionListToSectionResponseList(anyList());
    }

    @Test
    void getSectionForSelect() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Section> sectionPage = new PageImpl<>(sections, pageable, 5);
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("page", "1");
        requestMap.put("houseId", "1");
        requestMap.put("search", "1");
        requestMap.put("apartmentId", "1");
        requestMap.put("apartmentNumber", "1");
        List<SectionNameResponse> sectionNameResponses = new ArrayList<>();
        sectionNameResponses.add(new SectionNameResponse(sections.get(0).getId(), sections.get(0).getName()));
        sectionNameResponses.add(new SectionNameResponse(sections.get(1).getId(), sections.get(1).getName()));
        sectionNameResponses.add(new SectionNameResponse(sections.get(2).getId(), sections.get(2).getName()));

        // when
        when(sectionRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(sectionPage);
        when(sectionMapper.sectionListToSectionNameResponseList(anyList()))
                .thenReturn(sectionNameResponses);

        // call the method
        Page<SectionNameResponse> sectionNameResponsesResult = sectionService.getSectionForSelect(requestMap);

        // then
        assertFalse(sectionNameResponsesResult.getContent().isEmpty());
        assertEquals(3, sectionNameResponsesResult.getContent().size());
        verify(sectionRepo, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(sectionMapper, times(1)).sectionListToSectionNameResponseList(anyList());

    }
    @Test
    void deleteSectionsByHouseId(){
        when(sectionRepo.findAll(any(Specification.class))).thenReturn(List.of(new Section()));
        when(sectionRepo.saveAll(anyList())).thenReturn(List.of(new Section()));

        sectionService.deleteSectionsByHouseId(1L);

        verify(sectionRepo, times(1)).findAll(any(Specification.class));
        verify(sectionRepo, times(1)).saveAll(anyList());

        verifyNoMoreInteractions(sectionRepo);
    }

}