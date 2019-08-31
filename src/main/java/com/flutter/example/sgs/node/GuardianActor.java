package com.flutter.example.sgs.node;

import akka.actor.AbstractActor;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuardianActor extends AbstractActor {

    private GuardianActor() {
    }

    public static Props props() {
        return Props.create(GuardianActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }
}
