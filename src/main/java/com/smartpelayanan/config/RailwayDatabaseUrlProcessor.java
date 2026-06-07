package com.smartpelayanan.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RailwayDatabaseUrlProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "railwayDatabaseUrl";
    private static final String[] DATABASE_URL_ENV_NAMES = {
            "DATABASE_URL",
            "DATABASE_PRIVATE_URL",
            "DATABASE_PUBLIC_URL"
    };

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String explicitDatasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
        String databaseUrl = findDatabaseUrl();

        if (StringUtils.hasText(explicitDatasourceUrl)) {
            return;
        }

        if (!StringUtils.hasText(databaseUrl)) {
            if (isRailwayEnvironment()) {
                throw new IllegalStateException(
                        "Railway database belum dikonfigurasi. Tambahkan variable DATABASE_URL=${{Postgres.DATABASE_URL}} "
                                + "atau set SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, dan SPRING_DATASOURCE_PASSWORD.");
            }
            return;
        }

        try {
            URI uri = URI.create(databaseUrl);
            String scheme = uri.getScheme();
            if (!"postgres".equalsIgnoreCase(scheme) && !"postgresql".equalsIgnoreCase(scheme)) {
                return;
            }

            String[] userInfo = parseUserInfo(uri.getRawUserInfo());
            String query = StringUtils.hasText(uri.getRawQuery()) ? "?" + uri.getRawQuery() : "";
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath() + query;

            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.datasource.url", jdbcUrl);
            if (StringUtils.hasText(userInfo[0])) {
                properties.put("spring.datasource.username", userInfo[0]);
            }
            if (StringUtils.hasText(userInfo[1])) {
                properties.put("spring.datasource.password", userInfo[1]);
            }

            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        } catch (Exception ex) {
            throw new IllegalStateException("DATABASE_URL Railway tidak valid: " + ex.getMessage(), ex);
        }
    }

    private String[] parseUserInfo(String rawUserInfo) {
        if (!StringUtils.hasText(rawUserInfo)) {
            return new String[]{"", ""};
        }

        String[] parts = rawUserInfo.split(":", 2);
        String username = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
        String password = parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
        return new String[]{username, password};
    }

    private String findDatabaseUrl() {
        for (String envName : DATABASE_URL_ENV_NAMES) {
            String value = System.getenv(envName);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return "";
    }

    private boolean isRailwayEnvironment() {
        return StringUtils.hasText(System.getenv("RAILWAY_ENVIRONMENT"))
                || StringUtils.hasText(System.getenv("RAILWAY_ENVIRONMENT_NAME"))
                || StringUtils.hasText(System.getenv("RAILWAY_PROJECT_ID"))
                || StringUtils.hasText(System.getenv("RAILWAY_SERVICE_ID"));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
