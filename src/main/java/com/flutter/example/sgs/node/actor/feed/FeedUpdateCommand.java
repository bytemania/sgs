package com.flutter.example.sgs.node.actor.feed;

import com.flutter.example.sgs.node.model.InboudApi;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class FeedUpdateCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private final InboudApi api;
}
