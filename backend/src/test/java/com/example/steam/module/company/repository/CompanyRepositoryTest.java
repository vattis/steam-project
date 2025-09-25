package com.example.steam.module.company.repository;

import com.example.steam.module.company.application.CompanyService;
import com.example.steam.module.company.domain.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CompanyRepositoryTest {
    @Autowired CompanyRepository companyRepository;
    private Company company;
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        int sampleNum = 1;
        company = Company.makeSample(sampleNum);
        companyRepository.save(company);
    }

    @Test
    void findByName() {
        //given
        String companyName = company.getName();

        //when
        Optional<Company> companyResult = companyRepository.findByName(companyName);

        //then
        assertThat(company.getId()).isEqualTo(companyResult.get().getId());
    }
}