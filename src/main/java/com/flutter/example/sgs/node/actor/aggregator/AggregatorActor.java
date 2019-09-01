package com.flutter.example.sgs.node.actor.aggregator;

import akka.actor.AbstractActor;

public class AggregatorActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }
}
