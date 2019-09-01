package com.flutter.example.sgs.node.actor.mapping;

import com.flutter.example.sgs.node.model.Feed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class UpdateMappingCommand implements MappingCommand, Serializable {
    private static final long serialVersionUID = 1L;

    private final Feed feed;
    private final String feedId;
    private final String aggregatorId;
}
