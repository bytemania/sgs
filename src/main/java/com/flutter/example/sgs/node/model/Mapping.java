package com.flutter.example.sgs.node.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor(staticName = "of")
@Data
public class Mapping {
    private Feed feed;
    private String feedId;
    private String aggregatorId;
}
