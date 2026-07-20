package com.marketlocalshops.markets;

import com.marketlocalshops.exception.ResourceNotFoundException;
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
public class MarketService {

    private final MarketRepository marketRepository;
    private final MarketMapper marketMapper;

    @Transactional(readOnly = true)
    public Page<MarketDTO> findMarketsWithFilters(String search, Pageable pageable) {
        return marketRepository.findMarketsWithFilters(search, pageable)
                .map(marketMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<MarketDTO> findAll() {
        return marketRepository.findAll().stream()
                .map(marketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MarketDTO getMarketById(Long id) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Market not found"));
        return marketMapper.toDto(market);
    }

    @Transactional(readOnly = true)
    public MarketDTO getMarketBySlug(String slug) {
        Market market = marketRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Market not found"));
        return marketMapper.toDto(market);
    }

    @Transactional
    @CacheEvict(value = "markets", allEntries = true)
    public MarketDTO createMarket(MarketDTO marketDTO) {
        Market market = marketMapper.toEntity(marketDTO);
        Market saved = marketRepository.save(market);
        return marketMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "markets", allEntries = true)
    public MarketDTO updateMarket(Long id, MarketDTO updates) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Market not found"));
                
        if (updates.getName() != null) market.setName(updates.getName());
        if (updates.getLocation() != null) market.setLocation(updates.getLocation());
        if (updates.getSlug() != null) market.setSlug(updates.getSlug());
        if (updates.getDescription() != null) market.setDescription(updates.getDescription());
        if (updates.getImageUrl() != null) market.setImageUrl(updates.getImageUrl());
        
        Market saved = marketRepository.save(market);
        return marketMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "markets", allEntries = true)
    public void deleteMarket(Long id) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Market not found"));
        marketRepository.delete(market);
    }
}
