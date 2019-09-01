package com.flutter.example.sgs.node.actor.feed;

import com.flutter.example.sgs.node.model.Mapping;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class MappingUpdateCommand implements FeedCommand, Serializable {
    private static final long serialVersionUID = 1L;

    private Mapping mapping;
}
