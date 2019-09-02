package com.flutter.example.sgs.node.actor.mapping;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.flutter.example.sgs.cluster.FeedShardMsg;
import com.flutter.example.sgs.node.actor.feed.FeedCommand;
import com.flutter.example.sgs.node.actor.feed.FeedMappingUpdateCommand;
import com.flutter.example.sgs.node.model.Feed;
import com.flutter.example.sgs.node.util.Util;

import java.time.Duration;

public class MappingActor extends AbstractActor {

    public static Props props(ActorRef feedShardRegion) {
        return Props.create(MappingActor.class, () -> new MappingActor(feedShardRegion));
    }

    private ActorRef feedShardRegion;

    private MappingActor(ActorRef feedShardRegion) {
        this.feedShardRegion = feedShardRegion;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        getContext().getSystem().scheduler().schedule(
                Duration.ofSeconds(5),
                Duration.ofSeconds(60),
                getSelf(),
                UpdateMappingCommand.of(Feed.OPTA, "1", "aggregatorId"),
                getContext().getDispatcher(),
                self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateMappingCommand.class, this::mapping)
                .build();
    }

    private void mapping(UpdateMappingCommand mappingCommand) {

        FeedCommand feedCommand = FeedMappingUpdateCommand.of(
                mappingCommand.getFeed(),
                mappingCommand.getFeedId(),
                mappingCommand.getAggregatorId());

        FeedShardMsg msg = Util.generateFeedShardMessage(
                mappingCommand.getFeed(),
                mappingCommand.getFeedId(),
                feedCommand);

        feedShardRegion.tell(msg, getSelf());
    }

}
