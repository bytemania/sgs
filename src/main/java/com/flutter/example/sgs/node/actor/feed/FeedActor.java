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
                .match(FeedShardMsg.class, this::processShardMessage)
                .build();
    }

    private void processShardMessage(FeedShardMsg msg) {
        FeedCommand command = msg.getFeedUpdateCommand();
        processCommand(command);
    }

    private void processCommand(FeedCommand command) {
        if (command instanceof FeedUpdateCommand) {
            processFeedUpdateCommand((FeedUpdateCommand) command);
        } else {
            log.error("Unprocessed Message: {}", command);
        }
    }

    private void processFeedUpdateCommand(FeedUpdateCommand feedUpdateCommand) {
        updateData(feedUpdateCommand.getApi());
    }

    private void updateData(InboudApi newData) {
        log.info("Actor:{} DATA RECEIVED:{} OLD_DATA:{}",getSelf().path(), newData, data);
        data = newData;
    }
}
