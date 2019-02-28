package stubidp.utils.rest.eventsink;

import stubidp.eventemitter.Event;

public interface EventSinkProxy {

    void logHubEvent(Event eventSinkHubEvent);
}
