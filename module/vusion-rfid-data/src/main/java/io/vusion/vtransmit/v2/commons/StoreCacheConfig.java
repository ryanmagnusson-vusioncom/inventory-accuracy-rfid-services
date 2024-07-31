package io.vusion.vtransmit.v2.commons;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.google.gson.JsonObject;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.secure.logs.VusionLogger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class StoreCacheConfig {
    private static final VusionLogger LOGGER = new VusionLogger(StoreCacheConfig.class);
    private static final String TIME_TO_LIVE_JSON_NAME = "timeToLive";

    @Getter @Setter
    public static class StoreCacheProperties {
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration timeToLive = Duration.of(30, ChronoUnit.SECONDS);

        private Integer maxEntries = null;
        private boolean verbose = false;

        public void setVerbose(String flag) {
            if (isBlank(flag)) {
                this.verbose = false;
            }
            this.verbose = BooleanUtils.toBooleanObject(flag);
        }

        public void setVerbose(Boolean flag) {
            this.verbose = BooleanUtils.toBoolean(flag);
        }

        @Override
        public String toString() {
            final JsonObject json = GsonHelper.toJsonObject(this);
            json.remove(TIME_TO_LIVE_JSON_NAME);
            json.addProperty(TIME_TO_LIVE_JSON_NAME,
                             Optional.ofNullable(timeToLive)
                                     .map(ttl -> String.format("%d seconds", ttl.toSeconds()))
                                     .orElse(null));
            return GsonHelper.toJson(json);
        }
    }

    @ConfigurationProperties(prefix="cache.store")
    @Bean("storeCacheProperties")
    public StoreCacheProperties getStoreCacheProperties() {
        return new StoreCacheProperties();
    }

    @Bean({"storeCacheCaffeine"})
    public Caffeine<?, ?> getStoreCacheCaffeine(StoreCacheProperties storeCacheProperties) {
        final Caffeine<?, ?> caffeine = Caffeine.newBuilder()
                                                .expireAfterWrite(storeCacheProperties.getTimeToLive());

        if (storeCacheProperties.getMaxEntries() != null) {
            caffeine.maximumSize(storeCacheProperties.getMaxEntries());
        }

        if (storeCacheProperties.isVerbose()) {
            caffeine.evictionListener((Object key, Object value, RemovalCause cause) ->
                                              LOGGER.info(String.format("Store %s was evicted (%s)%n", key, cause)))
                    .removalListener((Object key, Object value, RemovalCause cause) ->
                                             LOGGER.info(String.format("Store %s was removed (%s)%n", key, cause)));
        }
        return caffeine;
    }

//    @Bean({"storeCache","storeIdCache"})
//    @DependsOn("storeDao")
//    public Cache<String,String> getStoreIdCache(Caffeine<String, String> storeCacheCaffeine) {
//        return storeCacheCaffeine.build();
//    }
}
