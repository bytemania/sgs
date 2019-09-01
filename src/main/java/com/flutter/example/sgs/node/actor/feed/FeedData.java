package com.flutter.example.sgs.node.actor.feed;

import com.flutter.example.sgs.node.model.Feed;
import com.flutter.example.sgs.node.model.InboudApi;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(staticName = "of")
@Builder
@Getter
@ToString
public class FeedData {
    private final String aggregateActorId;
    private final InboudApi data;

    public FeedData copy(String aggregateActorId) {
        return FeedData.builder()
                .aggregateActorId(aggregateActorId)
                .data(this.data)
                .build();
    }

    public FeedData copy(InboudApi api) {
        return FeedData.builder()
                .aggregateActorId(this.aggregateActorId)
                .data(api)
                .build();
    }
}
