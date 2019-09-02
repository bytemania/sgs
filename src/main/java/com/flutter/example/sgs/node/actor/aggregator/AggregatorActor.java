package com.flutter.example.sgs.node.actor.aggregator;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.text.MessageFormat;

public class AggregatorActor extends AbstractActor {

    public static Props props() {
        return Props.create(AggregatorActor.class);
    }

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AggregatorUpdateCommand.class, m -> log.info(MessageFormat.format("AGGREGATOR {0} MESSAGE RECEIVED :{1}", self().path(), m)))
                .build();
    }
}
