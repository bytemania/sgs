package com.flutter.example.sgs.node.actor.feed;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.flutter.example.sgs.node.actor.aggregator.AggregatorUpdateCommand;
import com.flutter.example.sgs.node.model.InboudApi;
import com.flutter.example.sgs.node.util.Util;

public class FeedActor extends AbstractActor {

    public static Props props(ActorRef aggregatorShardRegion) {
        return Props.create(FeedActor.class, () -> new FeedActor(aggregatorShardRegion));
    }

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private FeedData data;
    private ActorRef aggregatorShardRegion;

    private FeedActor(ActorRef aggregatorShardRegion) {
        this.aggregatorShardRegion = aggregatorShardRegion;
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
        sendToAggregator();
    }

    private void updateData(String newAggregatorId) {
        log.info("Actor:{} AggregatorId RECEIVED:{} OLD_DATA:{}", getSelf().path(), newAggregatorId, data);
        data = data.copy(newAggregatorId);
        log.info("Actor:{} AggregatorId RECEIVED:{} NEW_DATA:{}", getSelf().path(), newAggregatorId, data);
    }

    private void sendToAggregator() {
        if (data.getAggregateActorId() != null) {
            AggregatorUpdateCommand aggregatorUpdateCommand = AggregatorUpdateCommand.of(String.valueOf(data.getVersion()), data.getData());
            aggregatorShardRegion.tell(Util.generateAggregatorShardMessage(data.getAggregateActorId(), aggregatorUpdateCommand), self());
        } else {
            log.info("Cannot send the message data:{}, no aggregatorId defined", data);
        }
    }

}
