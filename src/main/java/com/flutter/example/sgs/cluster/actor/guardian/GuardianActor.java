package com.flutter.example.sgs.cluster.actor.guardian;

import akka.actor.AbstractActor;
import akka.actor.Props;

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
