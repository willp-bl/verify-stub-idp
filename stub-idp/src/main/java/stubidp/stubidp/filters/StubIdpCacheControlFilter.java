package stubidp.stubidp.filters;

import stubidp.stubidp.configuration.StubIdpConfiguration;
import stubidp.utils.rest.cache.CacheControlFilter;

public class StubIdpCacheControlFilter extends CacheControlFilter {
    public StubIdpCacheControlFilter(final StubIdpConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected boolean isCacheableAsset(final String localAddr) {
        return localAddr.contains("/assets/fonts/") || localAddr.contains("/assets/images/");
    }
}

