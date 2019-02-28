package uk.gov.ida.eventsink;

import stubidp.eventemitter.Event;

public interface EventSinkProxy {

    void logHubEvent(Event eventSinkHubEvent);
}
