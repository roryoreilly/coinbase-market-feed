package io.gsr.marketfeed;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class Config {
    private static final String PROPERTIES_FILE_NAME = "app.properties";
    private static final String PROPERTIES_TEST_NAME = "test.properties";
    private Properties properties;

    public Config(final String[] args) {
        try {
            properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME));
            addCoinbaseProductsFromArgs(args);
        } catch (IOException | NullPointerException e) {
            log.error("Failed to load properties file", e);
        }
    }

    private void addCoinbaseProductsFromArgs(final String[] args) {
        if (args.length > 0) {
            if (args[0].equals("all")) {
                properties.setProperty("coinbase.products", properties.getProperty("coinbase.products.all"));
            } else {
                properties.setProperty("coinbase.products", args[0]);
            }
        }
    }

    public String getCoinbaseWebsocketUrl() throws IllegalArgumentException, InvalidKeyException {
        return (String) getPropertiesForKeyAsType("coinbase.url", String.class);
    }

    public int getCoinbaseOrderBookLevels() throws IllegalArgumentException, InvalidKeyException {
        return (int) getPropertiesForKeyAsType("coinbase.orderbook.levels", int.class);
    }

    public List<String> getCoinbaseProducts() throws InvalidKeyException {
        String products = (String) getPropertiesForKeyAsType("coinbase.products", String.class);
        return Stream.of(products.split(",", -1)).collect(Collectors.toList());
    }

    public List<String> getCoinbaseProductsAll() throws InvalidKeyException {
        String products = (String) getPropertiesForKeyAsType("coinbase.products.all", String.class);
        return Stream.of(products.split(",", -1)).collect(Collectors.toList());
    }

    private Object getPropertiesForKeyAsType(String name, Class<?> type) throws IllegalArgumentException, InvalidKeyException {
        String value = properties.getProperty(name);
        if (value == null) {
            throw new InvalidKeyException("Missing configuration value: " + name);
        }
        if (type == String.class) {
            return value;
        }
        if (type == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (type == int.class) {
            return Integer.parseInt(value);
        }
        if (type == float.class) {
            return Float.parseFloat(value);
        }
        throw new IllegalArgumentException("Unknown configuration value type: " + type.getName());
    }
}
