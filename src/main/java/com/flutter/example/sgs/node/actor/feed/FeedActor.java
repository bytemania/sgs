package com.flutter.example.sgs.node.actor.feed;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.flutter.example.sgs.node.model.InboudApi;

public class FeedActor extends AbstractActor {

    public static Props props(ActorRef aggregatorRegion) {
        return Props.create(FeedActor.class, () -> new FeedActor(aggregatorRegion));
    }

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private FeedData data;
    private ActorRef aggregatorRegion;

    private FeedActor(ActorRef aggregatorRegion) {
        this.aggregatorRegion = aggregatorRegion;
        this.data = FeedData.builder().version(0).lastSentVersion(0).build();
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
        updateData(feedMappingUpdateCommand.getAggregatorId());
    }

    private void updateData(InboudApi newData) {
        log.info("Actor:{} DATA RECEIVED:{} OLD_DATA:{}", getSelf().path(), newData, data);
        data = data.copy(newData);
        log.info("Actor:{} AggregatorId RECEIVED:{} NEW_DATA:{}", getSelf().path(), newData, data);
    }

    private void updateData(String newAggregatorId) {
        log.info("Actor:{} AggregatorId RECEIVED:{} OLD_DATA:{}", getSelf().path(), newAggregatorId, data);
        data = data.copy(newAggregatorId);
        log.info("Actor:{} AggregatorId RECEIVED:{} NEW_DATA:{}", getSelf().path(), newAggregatorId, data);
    }

}
