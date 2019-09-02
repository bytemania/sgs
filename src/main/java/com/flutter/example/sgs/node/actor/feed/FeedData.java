package com.flutter.example.sgs.node.actor.feed;

import com.flutter.example.sgs.node.model.InboudApi;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class FeedData {
    private String aggregateActorId;
    private InboudApi data;
    private int version;
    private int lastSentVersion;

    public FeedData copy(String aggregateActorId) {
        return FeedData.builder()
                .aggregateActorId(aggregateActorId)
                .data(this.data)
                .version(this.version)
                .lastSentVersion(this.lastSentVersion)
                .build();
    }

    public FeedData copy(InboudApi api) {
        return FeedData.builder()
                .aggregateActorId(this.aggregateActorId)
                .data(api)
                .version(this.version + 1)
                .lastSentVersion(this.lastSentVersion)
                .build();
    }
}
