package stubidp.saml.hub.hub.transformers.outbound;

import stubidp.saml.utils.core.domain.OutboundResponseFromHub;

import java.util.function.Function;

public class OutboundLegacyResponseFromHubToStringFunctionSHA256 implements Function<OutboundResponseFromHub, String> {
    private Function<OutboundResponseFromHub, String> transformer;

    public OutboundLegacyResponseFromHubToStringFunctionSHA256(Function<OutboundResponseFromHub,String> transformer) {
        this.transformer = transformer;
    }

    @Override
    public String apply(OutboundResponseFromHub outboundResponseFromHub) {
        return transformer.apply(outboundResponseFromHub);
    }
}
