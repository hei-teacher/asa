package school.hei.asa.endpoint.event.consumer.model;

import school.hei.asa.PojaGenerated;
import school.hei.asa.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
