package com.flutter.example.sgs.cluster;

import com.flutter.example.sgs.node.actor.feed.FeedCommand;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@RequiredArgsConstructor(staticName = "of")
@Getter
@ToString
public class FeedShardMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final FeedCommand feedUpdateCommand;
}
