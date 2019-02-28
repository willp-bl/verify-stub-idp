package stubidp.utils.rest.eventsink;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import stubidp.utils.rest.common.ServiceInfoConfiguration;
import stubidp.utils.rest.common.SessionId;
import stubidp.eventemitter.Event;
import stubidp.eventemitter.EventDetailsKey;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class EventSinkHubEvent implements Event {
    private UUID eventId;
    private DateTime timestamp = DateTime.now();
    private String originatingService;
    private SessionId sessionId;
    private String eventType;
    private EnumMap<EventDetailsKey, String> details;

    @SuppressWarnings("unused") //Needed by Jaxb.
    private EventSinkHubEvent() {
    }

    public EventSinkHubEvent(ServiceInfoConfiguration serviceInfo, SessionId sessionId, String eventType, Map<EventDetailsKey, String> details) {
        this.eventId = UUID.randomUUID();
        this.originatingService = serviceInfo.getName();
        this.sessionId = sessionId;
        this.eventType = eventType;
        this.details = Maps.newEnumMap(details);
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public DateTime getTimestamp() {
        return timestamp;
    }

    public String getOriginatingService() {
        return originatingService;
    }

    public String getSessionId() {
        return sessionId.toString();
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public EnumMap<EventDetailsKey, String> getDetails() {
        return details;
    }
}
