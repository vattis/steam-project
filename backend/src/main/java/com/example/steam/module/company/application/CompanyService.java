package com.example.steam.module.company.application;

import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public Company makeCompany(String companyName){
        Company company = Company.of(companyName);
        if(companyRepository.findByName(companyName).isPresent()){
            log.info("이미 존재하는 회사 이름 등록 시도");
            throw new RuntimeException("중복된 이름의 Company 저장 시도");
        }
        return companyRepository.save(company);
    }
}
