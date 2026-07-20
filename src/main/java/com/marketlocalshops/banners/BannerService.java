package com.marketlocalshops.banners;

import com.marketlocalshops.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {
    private final BannerRepository bannerRepository;
    private final BannerMapper bannerMapper;

    @Transactional(readOnly = true)
    @Cacheable("banners")
    public List<BannerDTO> getActiveBanners() {
        return bannerRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(bannerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BannerDTO> getAllBanners() {
        return bannerRepository.findAll().stream()
                .map(bannerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "banners", allEntries = true)
    public BannerDTO createBanner(BannerDTO bannerDTO) {
        Banner banner = bannerMapper.toEntity(bannerDTO);
        return bannerMapper.toDto(bannerRepository.save(banner));
    }

    @Transactional
    @CacheEvict(value = "banners", allEntries = true)
    public BannerDTO updateBanner(Long id, Map<String, Object> updates) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found"));
                
        if (updates.containsKey("active")) {
            banner.setActive((Boolean) updates.get("active"));
        }
        
        return bannerMapper.toDto(bannerRepository.save(banner));
    }

    @Transactional
    @CacheEvict(value = "banners", allEntries = true)
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found"));
        bannerRepository.delete(banner);
    }
}
