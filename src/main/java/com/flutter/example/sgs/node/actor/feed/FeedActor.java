package com.flutter.example.sgs.node.actor.feed;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.flutter.example.sgs.node.model.Feed;
import com.flutter.example.sgs.node.model.InboudApi;

public class FeedActor extends AbstractActor {

    public static Props props() {
        return Props.create(FeedActor.class);
    }

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private FeedData data;

    private FeedActor() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(FeedUpdateCommand.class, this::processFeedUpdateCommand)
                .match(FeedMappingUpdateCommand.class, this::processFeedMappingUpdateCommand)
                .build();
    }

    private void processFeedUpdateCommand(FeedUpdateCommand feedUpdateCommand) {
        updateData(feedUpdateCommand.getApi());
    }


    private void processFeedMappingUpdateCommand(FeedMappingUpdateCommand feedMappingUpdateCommand) {
        if (feedMappingUpdateCommand.getFeed() == Feed.OPTA &&
                feedMappingUpdateCommand.getFeedId().equals(data.getData().getId())) {
            updateData(feedMappingUpdateCommand.getAggregatorId());
        } else {
            log.error("Error delivering the mapping {}", feedMappingUpdateCommand);
        }
    }

    private void updateData(InboudApi newData) {
        log.info("Actor:{} DATA RECEIVED:{} OLD_DATA:{}", getSelf().path(), newData, data);
        data = data.copy(newData);
    }

    private void updateData(String newAggregatorId) {
        log.info("Actor:{} AggregatorId RECEIVED:{} OLD_DATA:{}", getSelf().path(), newAggregatorId, data);
        data = data.copy(newAggregatorId);
    }

}
