package com.flutter.example.sgs.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.sharding.ShardRegion.MessageExtractor;
import com.flutter.example.sgs.node.actor.aggregator.AggregatorActor;
import com.flutter.example.sgs.node.actor.feed.FeedActor;
import com.flutter.example.sgs.node.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClusterFactory {

    private static final String ERROR_MSG = "Unidentified message type {}";
    private static final int NUMBER_OF_SHARDS = ConfigFactory.INSTANCE.getClusterConfig().NUMBER_OF_SHARDS;

    private static final MessageExtractor FEED_MESSAGE_EXTRACTOR = new MessageExtractor() {
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
            if (message instanceof FeedShardMsg) {
                return ((FeedShardMsg) message).getFeedUpdateCommand();
            } else {
                log.error(ERROR_MSG, message);
                return null;
            }
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

    private static final MessageExtractor AGGREGATOR_MESSAGE_EXTRACTOR = new MessageExtractor() {
        @Override
        public String entityId(Object message) {
            if (message instanceof AggregatorShardMsg) {
                return String.valueOf(((AggregatorShardMsg) message).getId());
            } else {
                log.error(ERROR_MSG, message);
                return null;
            }
        }

        @Override
        public Object entityMessage(Object message) {
            if (message instanceof AggregatorShardMsg) {
                return ((AggregatorShardMsg) message).getAggregatorCommand();
            } else {
                log.error(ERROR_MSG, message);
                return null;
            }
        }

        @Override
        public String shardId(Object message) {
            if (message instanceof AggregatorShardMsg) {
                String id = ((AggregatorShardMsg) message).getId();
                //TODO NEED TO TEST THE DISPERSION OF THIS KEY
                int hash = id.hashCode();
                return String.valueOf(hash % NUMBER_OF_SHARDS);
            } else {
                log.error(ERROR_MSG, message);
                return null;
            }
        }
    };

    public static ActorRef feedRegionOf(ActorSystem system, ActorRef aggregatorRegion) {
        final String SHARD_REGION_NAME = ConfigFactory.INSTANCE.getClusterConfig().SHARD_FEED_REGION_NAME;
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);

        return ClusterSharding.get(system).start(
                SHARD_REGION_NAME,
                FeedActor.props(aggregatorRegion),
                settings,
                FEED_MESSAGE_EXTRACTOR);
    }

    public static ActorRef aggregatorRegionOf(ActorSystem system) {
        final String SHARD_AGGREGATOR_REGION_NAME = ConfigFactory.INSTANCE.getClusterConfig().SHARD_AGGREGATOR_REGION_NAME;
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);

        return ClusterSharding.get(system).start(
                SHARD_AGGREGATOR_REGION_NAME,
                AggregatorActor.props(),
                settings,
                AGGREGATOR_MESSAGE_EXTRACTOR);
    }

    private ClusterFactory() {

    }
}
