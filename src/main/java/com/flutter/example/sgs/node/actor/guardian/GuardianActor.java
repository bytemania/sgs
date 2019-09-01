package com.flutter.example.sgs.node.actor.guardian;

import akka.Done;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.flutter.example.sgs.cluster.ClusterFactory;
import com.flutter.example.sgs.node.config.ConfigFactory;
import com.flutter.example.sgs.node.stream.StreamFactory;

public class GuardianActor extends AbstractActor {

    public static Props props() {
        return Props.create(GuardianActor.class);
    }

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private Consumer.DrainingControl<Done> control;

    private GuardianActor() {
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        control = createStream(getContext().getSystem());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        log.info("Shutting Down Inbound Stream");
        control.drainAndShutdown(getContext().getDispatcher());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }

    private Consumer.DrainingControl<Done> createStream(ActorSystem system) {
        ActorRef clusterRegion = ClusterFactory.feedRegionOf(system);

        return StreamFactory.inboundStreamOf(
                ConfigFactory.INSTANCE.getInboundConfig(),
                system,
                self(),
                clusterRegion);
    }
}
