package com.marketlocalshops.markets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketlocalshops.auth.AuthRequest;
import com.marketlocalshops.auth.AuthResponse;
import com.marketlocalshops.products.Product;
import com.marketlocalshops.shops.Shop;
import com.marketlocalshops.users.User;
import com.marketlocalshops.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MarketShopProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCrudOperations() throws Exception {
        // 1. Get Admin login token
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("admin@gmail.com");
        authRequest.setPassword("password");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        String token = authResponse.getToken();
        Long adminId = authResponse.getId();

        // 2. Create a Market (POST /api/markets)
        Market market = new Market();
        market.setName("Test Salem Market");
        market.setLocation("Salem Main Rd");
        market.setStatus("ACTIVE");

        mockMvc.perform(post("/api/markets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(market)))
                .andExpect(status().isOk());

        // 3. List Markets (GET /api/markets) - Publicly whitelisted
        mockMvc.perform(get("/api/markets"))
                .andExpect(status().isOk());

        // 4. Create a Shop (POST /api/shops)
        User owner = new User();
        owner.setId(adminId);

        Shop shop = new Shop();
        shop.setName("Test Vijayraj Shop");
        shop.setDescription("Premium quality goods");
        shop.setOwner(owner);

        MvcResult shopResult = mockMvc.perform(post("/api/shops")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shop)))
                .andExpect(status().isOk())
                .andReturn();

        Shop createdShop = objectMapper.readValue(shopResult.getResponse().getContentAsString(), Shop.class);

        // 5. List Shops (GET /api/shops) - Publicly whitelisted
        mockMvc.perform(get("/api/shops"))
                .andExpect(status().isOk());

        // 6. Create a Product in that Shop (POST /api/products)
        Product product = new Product();
        product.setName("Fresh Apple Test");
        product.setPrice(150.0);
        product.setShop(createdShop);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk());

        // 7. List Products (GET /api/products) - Publicly whitelisted
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }
}
