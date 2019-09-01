package com.flutter.example.sgs.node.actor.guardian;

import akka.Done;
import akka.actor.*;
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.kafka.javadsl.Consumer;
import com.flutter.example.sgs.cluster.ClusterFactory;
import com.flutter.example.sgs.node.config.ConfigFactory;
import com.flutter.example.sgs.node.stream.StreamFactory;

public class GuardianActor extends AbstractActor {

    public static Props props() {
        return Props.create(GuardianActor.class);
    }

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private Consumer.DrainingControl<Done> control;
    private ActorRef feedClusterRegion;
    private ActorRef aggregateClusterRegion;

    private GuardianActor() {
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        ActorSystem system = getContext().getSystem();
        createClusterRegions(system);
        control = createStream(system);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        log.info("Shutting Down Inbound Stream and Shard Regions");
        control.drainAndShutdown(getContext().getDispatcher());
        feedClusterRegion.tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
        aggregateClusterRegion.tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }

    private void createClusterRegions(ActorSystem system) {
        feedClusterRegion = ClusterFactory.feedRegionOf(system);
        aggregateClusterRegion = ClusterFactory.aggregatorRegionOf(system);
    }

    private Consumer.DrainingControl<Done> createStream(ActorSystem system) {

        return StreamFactory.inboundStreamOf(
                ConfigFactory.INSTANCE.getInboundConfig(),
                system,
                self(),
                feedClusterRegion);
    }
}
