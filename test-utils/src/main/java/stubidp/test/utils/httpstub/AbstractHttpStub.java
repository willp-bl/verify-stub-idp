package stubidp.test.utils.httpstub;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHttpStub {
    protected static final int RANDOM_PORT = 0;
    protected Server server;
    protected List<RecordedRequest> recordedRequests = new CopyOnWriteArrayList<>();
    protected List<RequestAndResponse> requestsAndResponses = new CopyOnWriteArrayList<>();

    public AbstractHttpStub(int port) {
        server = new Server(port);
        server.setHandler(createHandler());
    }

    public final void start() {
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final void stop() throws Exception {
        server.setStopTimeout(0);
        server.stop();
    }

    public final void reset() {
        recordedRequests.clear();
        requestsAndResponses.clear();
    }

    public final int getHttpPort() {
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    public final void register(RequestAndResponse requestAndResponse) {
        requestsAndResponses.add(requestAndResponse);
    }

    public final long getCountOfRequestsTo(final String path) {
        return recordedRequests.stream()
                .filter(r -> r.getPath().equals(path))
                .count();
    }

    public final List<RecordedRequest> getRecordedRequests() { return recordedRequests; }

    public final RecordedRequest getLastRequest() {
        return recordedRequests.stream().reduce((a, b) -> b).get();
    }

    public final int getCountOfRequests() {
        return recordedRequests.size();
    }

    public abstract StubHandler createHandler();
}
