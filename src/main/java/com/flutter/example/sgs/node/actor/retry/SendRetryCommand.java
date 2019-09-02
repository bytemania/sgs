package com.flutter.example.sgs.node.actor.retry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class SendRetryCommand implements Serializable, RetryCommand {
    private final String id;
    private final Object message;
}
