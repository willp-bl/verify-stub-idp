package uk.gov.ida.stub.idp.filters;

import stubidp.utils.rest.cache.CacheControlFilter;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;

public class StubIdpCacheControlFilter extends CacheControlFilter {
    public StubIdpCacheControlFilter(final StubIdpConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected boolean isCacheableAsset(final String localAddr) {
        return localAddr.contains("/assets/fonts/") || localAddr.contains("/assets/images/");
    }
}

