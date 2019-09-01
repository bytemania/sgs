package com.flutter.example.sgs.node.actor.feed;

import com.flutter.example.sgs.node.model.Feed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@RequiredArgsConstructor(staticName = "of")
@Getter
@ToString
public class FeedMappingUpdateCommand implements FeedCommand, Serializable {
    private static final long serialVersionUID = 1L;

    private final Feed feed;
    private final String feedId;
    private final String aggregatorId;
}
