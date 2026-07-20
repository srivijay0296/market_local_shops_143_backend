package com.marketlocalshops.posts;

import com.marketlocalshops.exception.ResourceNotFoundException;
import com.marketlocalshops.shops.Shop;
import com.marketlocalshops.shops.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerPostService {

    private final SellerPostRepository sellerPostRepository;
    private final ShopRepository shopRepository;
    private final SellerPostMapper sellerPostMapper;

    @Transactional(readOnly = true)
    public Page<SellerPostDTO> findPostsWithFilters(Long sellerId, String category, String search, Pageable pageable) {
        return sellerPostRepository.findPostsWithFilters(sellerId, category, search, pageable)
                .map(sellerPostMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<SellerPostDTO> findByShopIdAndCategory(Long sellerId, String category) {
        return sellerPostRepository.findByShop_IdAndCategory(sellerId, category).stream()
                .map(sellerPostMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SellerPostDTO> findByShopId(Long sellerId) {
        return sellerPostRepository.findByShop_Id(sellerId).stream()
                .map(sellerPostMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SellerPostDTO> findByCategory(String category) {
        return sellerPostRepository.findByCategory(category).stream()
                .map(sellerPostMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SellerPostDTO> findAll() {
        return sellerPostRepository.findAll().stream()
                .map(sellerPostMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SellerPostDTO getPostById(Long id) {
        SellerPost post = sellerPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return sellerPostMapper.toDto(post);
    }

    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public SellerPostDTO createPost(SellerPostDTO postDTO) {
        SellerPost post = sellerPostMapper.toEntity(postDTO);
        
        Long shopId = post.getShopId();
        if (shopId == null) {
            shopId = post.getSellerId();
        }
        
        if (shopId != null) {
            Shop shop = shopRepository.findById(shopId).orElse(null);
            post.setShop(shop);
        }
        if (post.getStatus() == null) {
            post.setStatus("approved");
        }
        
        SellerPost saved = sellerPostRepository.save(post);
        return sellerPostMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public SellerPostDTO updatePost(Long id, SellerPostDTO updates) {
        SellerPost post = sellerPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
                
        if (updates.getTitle() != null) post.setTitle(updates.getTitle());
        if (updates.getDescription() != null) post.setDescription(updates.getDescription());
        if (updates.getMediaUrl() != null) post.setMediaUrl(updates.getMediaUrl());
        if (updates.getMediaType() != null) post.setMediaType(updates.getMediaType());
        if (updates.getVideoUrl() != null) post.setVideoUrl(updates.getVideoUrl());
        if (updates.getPrice() != null) post.setPrice(updates.getPrice());
        if (updates.getOfferTag() != null) post.setOfferTag(updates.getOfferTag());
        if (updates.getLocation() != null) post.setLocation(updates.getLocation());
        if (updates.getCategory() != null) post.setCategory(updates.getCategory());
        if (updates.getStatus() != null) post.setStatus(updates.getStatus());
        if (updates.getMediaUrls() != null) post.setMediaUrls(updates.getMediaUrls());
        
        SellerPost saved = sellerPostRepository.save(post);
        return sellerPostMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public void deletePost(Long id) {
        SellerPost post = sellerPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        sellerPostRepository.delete(post);
    }
}
