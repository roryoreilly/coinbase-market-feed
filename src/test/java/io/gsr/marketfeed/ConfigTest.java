package io.gsr.marketfeed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ConfigTest {
    private Config config;

    @BeforeEach
    void setUp() {
        config = new Config(new String[]{"BTC-USD"});
    }

    @Test
    void getPropertiesForKeyReturnsExpectedResultForValidInput() throws InvalidKeyException {
        assertThat(config.getCoinbaseWebsocketUrl(), is("wss://ws-feed.pro.coinbase.com"));
    }
}