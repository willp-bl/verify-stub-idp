package stubidp.saml.hub.hub.transformers.outbound;

import stubidp.saml.utils.core.domain.OutboundResponseFromHub;

import java.util.function.Function;

/**
 * This concrete class is necessary to convince guice to inject two Function<OutboundResponseFromHub, String> implementations.
 */
public class OutboundLegacyResponseFromHubToStringFunction implements Function<OutboundResponseFromHub, String> {
    private Function<OutboundResponseFromHub, String> transformer;

    public OutboundLegacyResponseFromHubToStringFunction(Function<OutboundResponseFromHub,String> transformer) {
        this.transformer = transformer;
    }

    @Override
    public String apply(OutboundResponseFromHub outboundResponseFromHub) {
        return transformer.apply(outboundResponseFromHub);
    }
}
