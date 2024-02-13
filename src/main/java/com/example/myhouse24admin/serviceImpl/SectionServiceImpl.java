package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Section;
import com.example.myhouse24admin.mapper.SectionMapper;
import com.example.myhouse24admin.model.houses.SectionResponse;
import com.example.myhouse24admin.repository.SectionRepo;
import com.example.myhouse24admin.service.SectionService;
import com.example.myhouse24admin.specification.SectionSpecification;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepo sectionRepo;
    private final SectionMapper sectionMapper;

    public SectionServiceImpl(SectionRepo sectionRepo, SectionMapper sectionMapper) {
        this.sectionRepo = sectionRepo;
        this.sectionMapper = sectionMapper;
    }

    @Override
    public Page<SectionResponse> getSectionsByHouseId(Long houseId, int page, int pageSize, String name) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("name").descending());
        SectionSpecification specification = new SectionSpecification(Map.of("name", name, "houseId", houseId.toString()));
        Page<Section> all = sectionRepo.findAll(specification, pageable);
        List<SectionResponse> responseList = sectionMapper.sectionListToSectionResponseList(all.getContent());
        Page<SectionResponse> responses = new PageImpl<>(responseList, pageable, all.getTotalElements());
        return responses;
    }
}
