package com.flutter.example.sgs.node.model;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
public class InboudApi implements Serializable {
    private static final long serialVersionUID = 1L;
    private Feed feed;
    private String id;
}
