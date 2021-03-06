package com.flutter.example.sgs.node.actor.feed;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.flutter.example.sgs.cluster.Ack;
import com.flutter.example.sgs.cluster.AggregatorShardMsg;
import com.flutter.example.sgs.cluster.Nack;
import com.flutter.example.sgs.node.actor.aggregator.AggregatorUpdateCommand;
import com.flutter.example.sgs.node.actor.retry.RetryActor;
import com.flutter.example.sgs.node.actor.retry.SendRetryCommand;
import com.flutter.example.sgs.node.model.InboudApi;
import com.flutter.example.sgs.node.util.Util;

public class FeedActor extends AbstractActor {

    public static Props props(ActorRef aggregatorShardRegion, int retries, int timeout, int interval) {
        return Props.create(FeedActor.class, () -> new FeedActor(aggregatorShardRegion, retries, timeout, interval));
    }

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private FeedData data;
    private final ActorRef aggregatorShardRegion;
    private final int retries;
    private final int timeout;
    private final int interval;

    private FeedActor(ActorRef aggregatorShardRegion, int retries, int timeout, int interval) {
        this.aggregatorShardRegion = aggregatorShardRegion;
        this.data = FeedData.builder().version(0).lastSentVersion(0).build();
        this.retries = retries;
        this.timeout = timeout;
        this.interval = interval;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(FeedUpdateCommand.class, this::processFeedUpdateCommand)
                .match(FeedMappingUpdateCommand.class, this::processFeedMappingUpdateCommand)
                .match(Ack.class, this::processAck)
                .match(Nack.class, this::processNack)
                .build();
    }

    private void processFeedUpdateCommand(FeedUpdateCommand feedUpdateCommand) {
        updateData(feedUpdateCommand.getApi());
    }


    private void processFeedMappingUpdateCommand(FeedMappingUpdateCommand feedMappingUpdateCommand) {
        updateData(feedMappingUpdateCommand.getAggregatorId());
    }

    private void processAck(Ack ack) {
        int lastVersionSent = Integer.parseInt(ack.getId());
        updateData(lastVersionSent);
    }

    private void processNack(Nack nack) {
        log.error("Message with version {} not sent.", nack.getId());
    }

    private void updateData(InboudApi newData) {
        var oldData = FeedData.builder().aggregateActorId(data.getAggregateActorId()).data(data.getData()).version(data.getVersion()).lastSentVersion(data.getLastSentVersion()).build();
        data = data.copy(newData);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> Actor:{} AggregatorId RECEIVED:{} NEW_DATA:{} OLD_DATA:{}",
                getSelf().path(), newData, data, oldData);
        sendToAggregator();
    }

    private void updateData(String newAggregatorId) {
        var oldData = FeedData.builder().aggregateActorId(data.getAggregateActorId()).data(data.getData()).version(data.getVersion()).lastSentVersion(data.getLastSentVersion()).build();

        boolean firstTimeMapping = data.getAggregateActorId() == null || !data.getAggregateActorId().equals(newAggregatorId);

        data = data.copy(newAggregatorId);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> Actor:{} AggregatorId RECEIVED:{} NEW_DATA:{} OLD_DATA:{}",
                getSelf().path(), newAggregatorId, data, oldData);
        if (firstTimeMapping) {
            sendToAggregator();
        }
    }

    private void updateData(int lastVersionSent) {
        var oldData = FeedData.builder().aggregateActorId(data.getAggregateActorId()).data(data.getData()).version(data.getVersion()).lastSentVersion(data.getLastSentVersion()).build();
        data = data.copy(lastVersionSent);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> Actor:{} ACK RECEIVED:{} NEW_DATA:{} OLD_DATA:{}",
                getSelf().path(), lastVersionSent, data, oldData);
    }

    private void sendToAggregator() {
        if (data.getAggregateActorId() != null) {
            AggregatorUpdateCommand aggregatorUpdateCommand = AggregatorUpdateCommand.of(String.valueOf(data.getVersion()), data.getData());
            AggregatorShardMsg shardMsg = Util.generateAggregatorShardMessage(data.getAggregateActorId(), aggregatorUpdateCommand);
            SendRetryCommand retryMessage= SendRetryCommand.of(String.valueOf(data.getVersion()), shardMsg);

            ActorRef retry = getContext().getSystem().actorOf(RetryActor.props(retries, timeout, interval, aggregatorShardRegion));
            retry.tell(retryMessage, self());
        } else {
            log.info("Cannot send the message data:{}, no aggregatorId defined", data);
        }
    }

}
