package stubidp.test.utils.httpstub;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterators.forEnumeration;

public class ReceivedRequest {
    protected final String requestURI;
    protected final ImmutableMultimap<String, String> headers;
    protected final String querystring;
    protected final String method;
    protected final String url;
    protected byte[] entity;

    public ReceivedRequest(HttpServletRequest request) {
        this.method = request.getMethod();
        this.url = request.getRequestURL().toString();
        this.querystring = request.getQueryString();
        this.requestURI = request.getRequestURI();
        this.headers = getHeaders(request);
        this.entity = readEntity(request);
    }

    protected static byte[] readEntity(HttpServletRequest request) {
        try (InputStream in = request.getInputStream()) {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static ImmutableMultimap<String, String> getHeaders(HttpServletRequest request) {
        ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();
        for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements(); ) {
            String headerName = headerNames.nextElement();
            builder.putAll(headerName.toLowerCase(), copyOf(forEnumeration(request.getHeaders(headerName))));
        }
        return builder.build();
    }

    public String getPath() {
        return requestURI;
    }

    public String getUrl() {
        return url;
    }

    public ImmutableMultimap<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        ImmutableCollection<String> values = headers.get(name.toLowerCase());
        return values == null ? null : Iterables.getFirst(values, null);
    }

    public String getQuerystring() {
        return querystring;
    }

    public String getMethod() {
        return method;
    }

    /**
     * @return raw entity bytes, could be compressed etc
     */
    public byte[] getEntityBytes() {
        return entity;
    }

}
