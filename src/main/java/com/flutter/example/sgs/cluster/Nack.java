package com.flutter.example.sgs.cluster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@RequiredArgsConstructor(staticName = "of")
@Getter
@ToString
public class Nack implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
}

