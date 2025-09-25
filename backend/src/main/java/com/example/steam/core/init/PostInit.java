package com.example.steam.core.init;

import com.example.steam.module.article.domain.Article;
import com.example.steam.module.article.repository.ArticleRepository;
import com.example.steam.module.comment.domain.ArticleComment;
import com.example.steam.module.comment.domain.ProfileComment;
import com.example.steam.module.comment.repository.ArticleCommentRepository;
import com.example.steam.module.comment.repository.ProfileCommentRepository;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import com.example.steam.module.discount.domain.Discount;
import com.example.steam.module.discount.repository.DiscountRepository;
import com.example.steam.module.friendship.application.FriendshipService;
import com.example.steam.module.friendship.domain.Friendship;
import com.example.steam.module.gallery.application.GalleryService;
import com.example.steam.module.gallery.domain.Gallery;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.domain.MemberGame;
import com.example.steam.module.member.repository.MemberGameRepository;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.application.ProductService;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Component
@RequiredArgsConstructor
public class PostInit {
    private final MemberRepository memberRepository;
    private final MemberGameRepository memberGameRepository;
    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final FriendshipService friendshipService;
    private final DiscountRepository discountRepository;
    private final GalleryService galleryService;
    private final ProductService productService;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ProfileCommentRepository profileCommentRepository;
    private Random random = new Random();

    @PostConstruct
    public void init(){
        List<Company> companies = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Member> members = new ArrayList<>();
        List<Gallery> galleries = new ArrayList<>();
        List<Discount> discounts = new ArrayList<>();
        for(int i = 1; i <= 5; i++){
            companies.add(Company.makeSample(i));
        }
        companies = companyRepository.saveAll(companies);

        for(int i = 1; i <= 44; i++){
            Product product = Product.makeSample(i, companies.get(i%5));
            product = productRepository.save(product);
            products.add(product);
            Discount discount = Discount.makeSample(i, product);
            discount.activeDiscount();
            discountRepository.save(discount);
            galleries.add(galleryService.createGallery(product));
        }

        for(int i = 1; i <= 10; i++){
            Member member = memberRepository.save(Member.makeSample(i));
            members.add(member);
            for(int j = 1;  j <= 4; j++){
                memberGameRepository.save(MemberGame.of(products.get(i*j/2), member));
            }
        }
        for(int i = 1; i <= 10; i++){
            Member profileMember = members.get(i-1);
            for(int j = 1; j <= 5; j++){
                Member member = members.get(random.nextInt(10));
                ProfileComment profileComment = ProfileComment.makeSample(i*10+j, member, profileMember);
                profileCommentRepository.save(profileComment);
            }
        }
        for(int i = 0; i < 9; i++){
            Friendship friendship = friendshipService.inviteFriend(members.get(i).getId(), members.get(i+1).getId());
            friendshipService.acceptFriend(friendship.getFromMember().getId(), friendship.getToMember().getId());
        }
        for(int i = 0; i < 8; i++){
            Friendship friendship = friendshipService.inviteFriend(members.get(i).getId(), members.get(i+2).getId());
            friendshipService.acceptFriend(friendship.getFromMember().getId(), friendship.getToMember().getId());
        }
        for(int i = 0; i <= 10; i++){
            for(int j = 0; j <= 50; j++){
                Article article = Article.makeSample(i*100+j, galleries.get(i), members.get((i+j)%10));
                article = articleRepository.save(article);
                for(int k = 1; k <= 5; k++){
                    ArticleComment articleComment = ArticleComment.makeSample(members.get((i+j+k)%10), article, (i*1000+j*10+k));
                    articleCommentRepository.save(articleComment);
                }
            }
        }
    }
}
