package com.flutter.example.sgs.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.sharding.ShardRegion.MessageExtractor;
import com.flutter.example.sgs.node.actor.feed.FeedActor;
import com.flutter.example.sgs.node.actor.feed.FeedShardMsg;
import com.flutter.example.sgs.node.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClusterFactory {

    private static final MessageExtractor FEED_MESSAGE_EXTRACTOR = new MessageExtractor() {
        private static final String ERROR_MSG = "Unidentified message type {}";
        private final int NUMBER_OF_SHARDS = ConfigFactory.INSTANCE.getClusterConfig().NUMBER_OF_SHARDS;

        @Override
        public String entityId(Object message) {
            if (message instanceof FeedShardMsg) {
                return String.valueOf(((FeedShardMsg) message).getId());
            } else {
                log.error(ERROR_MSG, message);
                return null;
            }
        }

        @Override
        public Object entityMessage(Object message) {
            if (!(message instanceof FeedShardMsg))
            {
                log.error(ERROR_MSG, message);
            }
            return message;
        }

        @Override
        public String shardId(Object message) {
            if (message instanceof FeedShardMsg) {
                String id = ((FeedShardMsg) message).getId();
                //TODO NEED TO TEST THE DISPERSION OF THIS KEY
                int hash = id.hashCode();
                return String.valueOf(hash % NUMBER_OF_SHARDS);
            } else {
                log.error(ERROR_MSG, message);
                return null;
            }
        }
    };

    public static ActorRef feedRegionOf(ActorSystem system) {
        final String SHARD_REGION_NAME = ConfigFactory.INSTANCE.getClusterConfig().SHARD_FEED_REGION_NAME;

        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        return ClusterSharding.get(system).start(SHARD_REGION_NAME, FeedActor.props(), settings, FEED_MESSAGE_EXTRACTOR);
    }

    private ClusterFactory() {

    }
}
