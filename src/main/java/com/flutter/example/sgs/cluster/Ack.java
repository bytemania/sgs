package com.flutter.example.sgs.cluster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@RequiredArgsConstructor(staticName = "of")
@Getter
@ToString
public class Ack implements Serializable {
    private final String id;
}
