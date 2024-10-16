package com.minet.walletservice;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import static org.junit.Assert.assertTrue;
@SpringBootTest
class WalletServiceApplicationTest {
    @Test
    void contextLoads() {
        assertTrue(true);
    }
}