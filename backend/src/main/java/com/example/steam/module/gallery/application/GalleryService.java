package com.example.steam.module.gallery.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.gallery.domain.Gallery;
import com.example.steam.module.gallery.repository.GalleryRepository;
import com.example.steam.module.product.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class GalleryService {
    private final GalleryRepository galleryRepository;

    //갤러리 생성
    public Gallery createGallery(Product product){
        Gallery gallery = Gallery.of(product);
        return galleryRepository.save(gallery);
    }

    //이름으로 갤러리 찾기
    public Gallery findGalleryWithProductName(String name){
        return galleryRepository.findByProduct_Name(name).orElseThrow(NoSuchElementException::new);
    }

    //갤러리 전체 찾기
    public Page<Gallery> findAllGallery(int pageNo){
        Pageable pageable = PageRequest.of(pageNo, PageConst.GALLERY_LIST_PAGE_SIZE);
        return galleryRepository.findAll(pageable);
    }

    //갤러리 검색
    public Page<Gallery> search(String searchWord, int pageNo){
        Pageable pageable = PageRequest.of(pageNo, PageConst.GALLERY_LIST_PAGE_SIZE);
        return galleryRepository.findByProduct_NameContaining(searchWord, pageable);
    }

    //Id로 찾기
    public Gallery findById(Long galleryId){
        return galleryRepository.findById(galleryId).orElseThrow(NoSuchElementException::new);
    }

    //member id로 해당하는 갤러리 찾기
    public List<Gallery> findOwnedGalleriesByMemberId(Long memberId){
        return galleryRepository.findAllByMemberOwnedProduct(memberId);
    }
}
