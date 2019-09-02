package com.flutter.example.sgs.node.actor.aggregator;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.flutter.example.sgs.cluster.Ack;

public class AggregatorActor extends AbstractActor {

    public static Props props() {
        return Props.create(AggregatorActor.class);
    }

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AggregatorUpdateCommand.class, this::processReceived)
                .build();
    }

    private void processReceived(AggregatorUpdateCommand command) {
        log.info("AGGREGATOR {} MESSAGE RECEIVED :{}", self().path(), command);
        Ack ack = Ack.of(command.getId());
        sender().tell(ack, self());
    }

}
