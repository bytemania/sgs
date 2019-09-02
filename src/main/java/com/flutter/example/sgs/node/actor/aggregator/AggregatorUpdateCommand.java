package com.flutter.example.sgs.node.actor.aggregator;

import com.flutter.example.sgs.node.model.InboudApi;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@RequiredArgsConstructor(staticName = "of")
@Getter
@ToString
public class AggregatorUpdateCommand implements AggregatorCommand, Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final InboudApi api;
}
