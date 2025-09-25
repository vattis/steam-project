package com.example.steam.module.gallery.application;
import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.gallery.domain.Gallery;
import com.example.steam.module.gallery.repository.GalleryRepository;
import com.example.steam.module.product.domain.Product;
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
import org.springframework.test.util.ReflectionTestUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GalleryServiceTest {
    @InjectMocks GalleryService galleryService;
    @Mock GalleryRepository galleryRepository;
    List<Company> companies = new ArrayList<>();
    List<Product> products = new ArrayList<>();
    List<Gallery> galleries = new ArrayList<>();

    @BeforeEach
    void setUp(){
        for(int i = 1; i <= 10; i++){
            Company company = Company.makeSample(i);
            Product product = Product.makeSample(i, company);
            Gallery gallery = Gallery.makeSample(product);
            ReflectionTestUtils.setField(gallery, "id", (long)i);
            companies.add(company);
            products.add(product);
            galleries.add(gallery);
        }
    }

    @Test
    void createGalleryTest() {
        //given
        Gallery gallery = galleries.get(0);
        Product product = gallery.getProduct();

        given(galleryRepository.save(any(Gallery.class))).willReturn(galleries.get(0));

        //when
        Gallery result = galleryService.createGallery(product);

        //then
        assertThat(result.getId()).isEqualTo(gallery.getId());
        verify(galleryRepository).save(any(Gallery.class));
    }

    @Test
    void findGalleryWithProductNameTest() {
        //given
        Gallery gallery = galleries.get(0);
        String name = gallery.getProduct().getName();
        given(galleryRepository.findByProduct_Name(name)).willReturn(Optional.of(gallery));

        //when
        Gallery result = galleryService.findGalleryWithProductName(name);

        //then
        assertThat(result.getId()).isEqualTo(gallery.getId());
        verify(galleryRepository).findByProduct_Name(name);
    }

    @Test
    void findAllGalleryTest() {
        //given
        int pageNo = 0;
        Page<Gallery> galleryPage = new PageImpl<>(galleries);
        Pageable pageable = PageRequest.of(pageNo, PageConst.GALLERY_LIST_PAGE_SIZE);
        given(galleryRepository.findAll(pageable)).willReturn(galleryPage);

        //when
        Page<Gallery> result = galleryService.findAllGallery(pageNo);


        //then
        assertThat(result.getTotalElements()).isEqualTo(galleries.size());
        verify(galleryRepository).findAll(any(Pageable.class));
    }

    @Test
    void searchTest() {
        //given
        int pageNo = 0;
        String searchWord = "name1";
        Pageable pageable = PageRequest.of(pageNo, PageConst.GALLERY_LIST_PAGE_SIZE);
        List<Gallery> galleryList = new ArrayList<>();
        galleryList.add(galleries.get(0));
        galleryList.add(galleries.get(9));
        Page<Gallery> galleryPage = new PageImpl<>(galleryList, pageable, 10);
        given(galleryRepository.findByProduct_NameContaining(searchWord, pageable)).willReturn(galleryPage);

        //when
        Page<Gallery> result = galleryService.search(searchWord, pageNo);

        //then
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(galleryRepository).findByProduct_NameContaining(any(String.class), any(Pageable.class));
    }
}