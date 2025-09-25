package com.example.steam.module.company.application;

import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {
    @InjectMocks CompanyService companyService;
    @Mock CompanyRepository companyRepository;

    private Company company;

    @BeforeEach
    void setUp() {
        int sampleNum = 1;
        company = Company.makeSample(sampleNum);
        ReflectionTestUtils.setField(company, "id", 1L);
    }

    @Test
    @DisplayName("회사 등록 성공")
    void makeCompanyTest1() {
        //given
        String companyName = "companyName";
        given(companyRepository.findByName(companyName)).willReturn(Optional.empty());

        //when
        Company company = companyService.makeCompany(companyName);

        //then
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    @DisplayName("이미 존재하는 회사 등록 시도 실패")
    void makeCompanyTest2() {
        //given
        String companyName = company.getName();
        given(companyRepository.findByName(companyName)).willReturn(Optional.of(company));

        //when
        //Company company = companyService.makeCompany(companyName);

        //then
        assertThrows(RuntimeException.class, () -> companyService.makeCompany(companyName));
        verify(companyRepository, never()).save(any(Company.class));
    }
}