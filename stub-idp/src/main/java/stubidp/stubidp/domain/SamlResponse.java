package stubidp.stubidp.domain;

import java.net.URI;

public interface SamlResponse {
    public String getResponseString();
    public String getRelayState();
    public URI getHubUrl();
}
