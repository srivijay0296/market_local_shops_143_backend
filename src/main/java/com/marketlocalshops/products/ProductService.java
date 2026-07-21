package com.marketlocalshops.products;

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
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findProductsWithFilters(Long shopId, String category, Boolean isApproved, String search, Pageable pageable) {
        return productRepository.findProductsWithFilters(shopId, category, isApproved, search, pageable)
                .map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findByShopIdAndCategory(Long shopId, String category) {
        return productRepository.findByShop_IdAndCategory(shopId, category).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findByShopId(Long shopId) {
        return productRepository.findByShop_Id(shopId).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toDto(product);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        
        Long shopId = product.getShopId();
        if (shopId == null && productDTO.getShop() != null) {
            shopId = productDTO.getShop().getId();
        }
        
        if (shopId != null) {
            Long targetShopId = shopId;
            Shop shop = shopRepository.findById(targetShopId)
                    .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + targetShopId));
            product.setShop(shop);
            product.setShopId(shop.getId());
        }

        if (product.getStock() == null) product.setStock(10);
        if (product.getIsApproved() == null) product.setIsApproved(true);
        if (product.getViewCount() == null) product.setViewCount(0);
        
        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO updateProduct(Long id, ProductDTO updates) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
                
        if (updates.getName() != null) product.setName(updates.getName());
        if (updates.getPrice() != null) product.setPrice(updates.getPrice());
        if (updates.getDescription() != null) product.setDescription(updates.getDescription());
        if (updates.getCategory() != null) product.setCategory(updates.getCategory());
        if (updates.getStock() != null) product.setStock(updates.getStock());
        if (updates.getImageUrl() != null) product.setImageUrl(updates.getImageUrl());
        if (updates.getIsApproved() != null) product.setIsApproved(updates.getIsApproved());
        if (updates.getViewCount() != null) product.setViewCount(updates.getViewCount());
        
        Long shopId = updates.getShopId();
        if (shopId == null && updates.getShop() != null) {
            shopId = updates.getShop().getId();
        }
        if (shopId != null) {
            Long targetShopId = shopId;
            Shop shop = shopRepository.findById(targetShopId)
                    .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + targetShopId));
            product.setShop(shop);
            product.setShopId(shop.getId());
        }
        
        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }
}
