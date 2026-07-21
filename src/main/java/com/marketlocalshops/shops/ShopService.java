package com.marketlocalshops.shops;

import com.marketlocalshops.exception.ResourceNotFoundException;
import com.marketlocalshops.markets.Market;
import com.marketlocalshops.markets.MarketRepository;
import com.marketlocalshops.users.User;
import com.marketlocalshops.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final MarketRepository marketRepository;
    private final ShopMapper shopMapper;

    @Transactional(readOnly = true)
    public Page<ShopDTO> findShopsWithFilters(Long marketId, String status, String search, Pageable pageable) {
        return shopRepository.findShopsWithFilters(marketId, status, search, pageable)
                .map(shopMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ShopDTO> findByMarketIdAndStatus(Long marketId, String status) {
        return shopRepository.findByMarket_IdAndStatus(marketId, status).stream()
                .map(shopMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShopDTO> findByMarketId(Long marketId) {
        return shopRepository.findByMarket_Id(marketId).stream()
                .map(shopMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShopDTO> findByStatus(String status) {
        return shopRepository.findByStatus(status).stream()
                .map(shopMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShopDTO> findAll() {
        return shopRepository.findAll().stream()
                .map(shopMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShopDTO getShopById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        return shopMapper.toDto(shop);
    }

    @Transactional
    @CacheEvict(value = "shops", allEntries = true)
    public ShopDTO createShop(ShopDTO shopDTO) {
        Shop shop = shopMapper.toEntity(shopDTO);
        resolveOwnerAndMarket(shop);
        if (shop.getStatus() == null || shop.getStatus().isBlank()) {
            shop.setStatus("approved");
        }
        
        Shop saved = shopRepository.save(shop);
        return shopMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "shops", allEntries = true)
    public ShopDTO createShopRequest(ShopRequest request) {
        Shop shop = shopMapper.toEntity(request);
        resolveOwnerAndMarket(shop);
        if (shop.getStatus() == null || shop.getStatus().isBlank()) {
            shop.setStatus("pending");
        }
        
        Shop saved = shopRepository.save(shop);
        return shopMapper.toDto(saved);
    }

    private void resolveOwnerAndMarket(Shop shop) {
        Long ownerId = shop.getOwnerId();
        if (ownerId == null && shop.getOwner() != null) {
            ownerId = shop.getOwner().getId();
        }

        if (ownerId == null) {
            try {
                org.springframework.security.core.Authentication auth = 
                        org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    String username = auth.getName();
                    User currentUser = userRepository.findByUsername(username)
                            .or(() -> userRepository.findByEmail(username)).orElse(null);
                    if (currentUser != null) {
                        shop.setOwner(currentUser);
                        shop.setOwnerId(currentUser.getId());
                    }
                }
            } catch (Exception ignored) {}
        } else {
            Long targetId = ownerId;
            User owner = userRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + targetId));
            shop.setOwner(owner);
        }

        Long marketId = shop.getMarketId();
        if (marketId == null && shop.getMarket() != null) {
            marketId = shop.getMarket().getId();
        }

        if (marketId != null) {
            Long targetMarketId = marketId;
            Market market = marketRepository.findById(targetMarketId)
                    .orElseThrow(() -> new ResourceNotFoundException("Market not found with id: " + targetMarketId));
            shop.setMarket(market);
        }
    }

    @Transactional
    @CacheEvict(value = "shops", allEntries = true)
    public ShopDTO updateShop(Long id, ShopDTO updates) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
                
        if (updates.getName() != null) shop.setName(updates.getName());
        if (updates.getDescription() != null) shop.setDescription(updates.getDescription());
        if (updates.getCategory() != null) shop.setCategory(updates.getCategory());
        if (updates.getImageUrl() != null) shop.setImageUrl(updates.getImageUrl());
        if (updates.getVendorName() != null) shop.setVendorName(updates.getVendorName());
        if (updates.getLocation() != null) shop.setLocation(updates.getLocation());
        if (updates.getPhone() != null) shop.setPhone(updates.getPhone());
        
        if (updates.getMarketId() != null) {
            Market market = marketRepository.findById(updates.getMarketId()).orElse(null);
            shop.setMarket(market);
        }
        
        Shop saved = shopRepository.save(shop);
        return shopMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "shops", allEntries = true)
    public void deleteShop(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        shopRepository.delete(shop);
    }

    @Transactional
    @CacheEvict(value = "shops", allEntries = true)
    public ShopDTO updateStatus(Long id, Map<String, String> body) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        String status = body.get("status");
        if (status != null) {
            shop.setStatus(status);
        }
        return shopMapper.toDto(shopRepository.save(shop));
    }

    @Transactional
    @CacheEvict(value = "shops", allEntries = true)
    public ShopDTO approveRequest(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        shop.setStatus("approved");
        return shopMapper.toDto(shopRepository.save(shop));
    }

    @Transactional
    @CacheEvict(value = "shops", allEntries = true)
    public ShopDTO rejectRequest(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        shop.setStatus("rejected");
        return shopMapper.toDto(shopRepository.save(shop));
    }
}
