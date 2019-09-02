package com.flutter.example.sgs.node.actor.guardian;

import akka.Done;
import akka.actor.*;
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.kafka.javadsl.Consumer;
import com.flutter.example.sgs.cluster.ClusterFactory;
import com.flutter.example.sgs.node.actor.mapping.MappingActor;
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
    private ActorRef mappingActor;

    private GuardianActor() {
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        start();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        stop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }

    private void start() {
        log.info("Starting: Inbound Stream, Shard Regions, Mapping and Publisher Actors");
        ActorSystem system = getContext().getSystem();
        createClusterRegions(system);
        control = createStream(system);
        createActors(feedClusterRegion);
    }

    private void stop() {
        log.info("Shutting Down: Inbound Stream, Shard Regions, Mapping and Publisher Actors");
        control.drainAndShutdown(getContext().getDispatcher());
        feedClusterRegion.tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
        aggregateClusterRegion.tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
        stopActors();
    }

    private void createClusterRegions(ActorSystem system) {
        aggregateClusterRegion = ClusterFactory.aggregatorRegionOf(system);
        feedClusterRegion = ClusterFactory.feedRegionOf(system, aggregateClusterRegion,
                ConfigFactory.INSTANCE.getRetryConfig());
    }

    private Consumer.DrainingControl<Done> createStream(ActorSystem system) {

        return StreamFactory.inboundStreamOf(
                ConfigFactory.INSTANCE.getInboundConfig(),
                system,
                self(),
                feedClusterRegion);
    }

    private void createActors(ActorRef feedClusterRegion) {
        mappingActor = getContext().actorOf(MappingActor.props(feedClusterRegion), "mappingActor");
    }

    private void stopActors() {
        context().stop(mappingActor);
    }

}
