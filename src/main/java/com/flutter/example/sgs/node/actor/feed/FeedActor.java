package com.flutter.example.sgs.node.actor.feed;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.flutter.example.sgs.node.model.InboudApi;

public class FeedActor extends AbstractActor {

    public static Props props() {
        return Props.create(FeedActor.class);
    }

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private InboudApi data;

    private FeedActor() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(FeedShardMsg.class, this::processeShardMessage)
                .build();
    }

    private void processeShardMessage(FeedShardMsg msg) {
        FeedUpdateCommand command = msg.getFeedUpdateCommand();
        processUpdateCommand(command);
    }

    private void processUpdateCommand(FeedUpdateCommand command) {
        updateData(command.getApi());
    }

    private void updateData(InboudApi newData) {
        log.info("Actor:{} DATA RECEIVED:{} OLD_DATA:{}",getSelf().path(), newData, data);
        data = newData;
    }
}
