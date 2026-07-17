package com.marketlocalshops.config;

import com.marketlocalshops.users.User;
import com.marketlocalshops.users.UserRepository;
import com.marketlocalshops.roles.Role;
import com.marketlocalshops.categories.Category;
import com.marketlocalshops.categories.CategoryRepository;
import com.marketlocalshops.markets.Market;
import com.marketlocalshops.markets.MarketRepository;
import com.marketlocalshops.shops.Shop;
import com.marketlocalshops.shops.ShopRepository;
import com.marketlocalshops.products.Product;
import com.marketlocalshops.products.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MarketRepository marketRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           MarketRepository marketRepository,
                           ShopRepository shopRepository,
                           ProductRepository productRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.marketRepository = marketRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // 1. Create Admin User
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            // User with Sownd143@
            User srivi1 = new User();
            srivi1.setUsername("srivijay95");
            srivi1.setEmail("srivijay0296@gmail.com");
            srivi1.setPassword(passwordEncoder.encode("Sownd143@"));
            srivi1.setRole(Role.ADMIN);
            userRepository.save(srivi1);

            // User with Sownd143
            User srivi2 = new User();
            srivi2.setUsername("srivi");
            srivi2.setEmail("srivi@gmail.com");
            srivi2.setPassword(passwordEncoder.encode("Sownd143"));
            srivi2.setRole(Role.ADMIN);
            userRepository.save(srivi2);

            // 2. Create Categories
            Category groceries = categoryRepository.save(new Category(null, "groceries"));
            Category electronics = categoryRepository.save(new Category(null, "electronics"));
            Category clothing = categoryRepository.save(new Category(null, "clothing"));
            categoryRepository.save(new Category(null, "accessories"));

            // 3. Create Market
            Market market = new Market();
            market.setName("Kalignar Market");
            market.setLocation("Bargur");
            market.setStatus("active");
            marketRepository.save(market);

            // 4. Create Shop
            Shop shop = new Shop();
            shop.setName("Vijayraj Store");
            shop.setDescription("Local Grocery and Wholesale Store");
            shop.setOwner(admin);
            shopRepository.save(shop);

            // 5. Create Products
            Product apple = new Product();
            apple.setName("Fresh Apple");
            apple.setPrice(120.0);
            apple.setShop(shop);
            productRepository.save(apple);

            Product tshirt = new Product();
            tshirt.setName("Cotton T-Shirt");
            tshirt.setPrice(499.0);
            tshirt.setShop(shop);
            productRepository.save(tshirt);

            System.out.println("====== SEED DATA INITIALIZED SUCCESSFULLY ======");
        }
    }
}
