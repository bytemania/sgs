package com.flutter.example.sgs.node.exception;

import akka.kafka.ConsumerMessage;
import lombok.Getter;

public class ParseException extends RuntimeException {

    @Getter
    private ConsumerMessage.CommittableMessage msg;

    public ParseException(Throwable cause, ConsumerMessage.CommittableMessage msg) {
        super(cause);
        this.msg = msg;
    }

}
