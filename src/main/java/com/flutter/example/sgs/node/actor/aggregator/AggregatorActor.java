package com.flutter.example.sgs.node.actor.aggregator;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class AggregatorActor extends AbstractActor {

    public static Props props() {
        return Props.create(AggregatorActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }
}
