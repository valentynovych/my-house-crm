package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Section;
import com.example.myhouse24admin.mapper.SectionMapper;
import com.example.myhouse24admin.model.houses.SectionResponse;
import com.example.myhouse24admin.model.meterReadings.SectionNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.repository.SectionRepo;
import com.example.myhouse24admin.service.SectionService;
import com.example.myhouse24admin.specification.SectionSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.example.myhouse24admin.specification.SectionInterfaceSpecification.*;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepo sectionRepo;
    private final SectionMapper sectionMapper;
    private final Logger logger = LogManager.getLogger(SectionServiceImpl.class);

    public SectionServiceImpl(SectionRepo sectionRepo, SectionMapper sectionMapper) {
        this.sectionRepo = sectionRepo;
        this.sectionMapper = sectionMapper;
    }

    @Override
    public Page<SectionResponse> getSectionsByHouseId(Long houseId, int page, int pageSize, String name) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("name").ascending());
        SectionSpecification specification = new SectionSpecification(Map.of("name", name, "houseId", houseId.toString()));
        Page<Section> all = sectionRepo.findAll(specification, pageable);
        List<SectionResponse> responseList = sectionMapper.sectionListToSectionResponseList(all.getContent());
        Page<SectionResponse> responses = new PageImpl<>(responseList, pageable, all.getTotalElements());
        return responses;
    }

    @Override
    public Page<SectionNameResponse> getSectionForSelect(Map<String, String> requestMap) {
        logger.info("getSectionForSelect - Getting section name responses for select " + requestMap.toString());
        Pageable pageable = PageRequest.of(Integer.valueOf(requestMap.get("page"))-1, 10);
        Page<Section> sections = getFilteredSectionsForSelect(requestMap, pageable);
        List<SectionNameResponse> sectionNameResponses = sectionMapper.sectionListToSectionNameResponseList(sections.getContent());
        Page<SectionNameResponse> sectionNameResponsePage = new PageImpl<>(sectionNameResponses, pageable, sections.getTotalElements());
        logger.info("getSectionForSelect - Section name responses were got");
        return sectionNameResponsePage;
    }

    private Page<Section> getFilteredSectionsForSelect(Map<String, String> requestMap, Pageable pageable) {
        Specification<Section> sectionSpecification = Specification.where(byDeleted().and(byHouseId(Long.valueOf(requestMap.get("houseId")))));
        if(!requestMap.get("search").isEmpty()){
            sectionSpecification = sectionSpecification.and(byNameLike(requestMap.get("search")));
        }
        if(requestMap.containsKey("apartmentId") && !requestMap.get("apartmentId").isEmpty()){
            sectionSpecification = sectionSpecification.and(byApartmentId(Long.valueOf(requestMap.get("apartmentId"))));
        }
        if(requestMap.containsKey("apartmentNumber") && !requestMap.get("apartmentNumber").isEmpty()){
            sectionSpecification = sectionSpecification.and(byApartmentNumberLike(requestMap.get("apartmentNumber")));
        }
        return sectionRepo.findAll(sectionSpecification, pageable);
    }

    @Override
    public void deleteSectionsByHouseId(Long houseId) {
        logger.info("deleteSectionsByHouseId - Deleting sections by house id " + houseId);
        List<Section> sections = sectionRepo.findAll(byHouseId(houseId));
        for(Section section: sections){
            section.setDeleted(true);
        }
        sectionRepo.saveAll(sections);
        logger.info("deleteSectionsByHouseId - Sections were deleted");
    }
}
