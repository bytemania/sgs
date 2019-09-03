package com.flutter.example.sgs.node.stream;

import akka.Done;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.kafka.*;
import akka.kafka.javadsl.Committer;
import akka.kafka.javadsl.Consumer;
import akka.stream.*;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Source;
import com.flutter.example.sgs.cluster.FeedShardMsg;
import com.flutter.example.sgs.node.config.InboundConfig;
import com.flutter.example.sgs.node.config.RetryConfig;
import com.flutter.example.sgs.node.exception.ParseException;
import com.flutter.example.sgs.node.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
public class StreamFactory {

    public static Consumer.DrainingControl<Done> inboundStreamOf(InboundConfig inboundConfig,
                                                                 ActorSystem system,
                                                                 ActorRef sender,
                                                                 ActorRef clusterShard) {

        Materializer materializer = ActorMaterializer.create(system);

        ConsumerSettings<String, String> settings = ConsumerSettings.create(inboundConfig.KAFKA_CONSUMER_CONFIG,
                new StringDeserializer(), new StringDeserializer());

        CommitterSettings committerSettings = CommitterSettings.create(system);

        Source<ConsumerMessage.CommittableMessage<String, String>, Consumer.Control> source =
                Consumer.committableSource(settings, Subscriptions.topics(inboundConfig.TOPIC));

        return source
                .map(Util::translateInbound)
                .mapAsync(1, msg ->
                                processMessage(sender, clusterShard, msg._2)
                                        .thenApply(done -> msg._1.committableOffset()))
                .toMat(Committer.sink(committerSettings.withMaxBatch(1)), Keep.both())
                .mapMaterializedValue(Consumer::createDrainingControl)
                .withAttributes(ActorAttributes.withSupervisionStrategy(StreamFactory::decider))
                .run(materializer);
    }

    private static CompletionStage<Void> processMessage(ActorRef sender,
                                         ActorRef clusterShard,
                                         FeedShardMsg feedShardMsg) {
        return CompletableFuture.runAsync(() -> clusterShard.tell(feedShardMsg, sender));
    }

    private static Supervision.Directive decider(Throwable exception) {
        if (exception instanceof ParseException) {
            ParseException e = (ParseException) exception;
            e.getMsg().committableOffset();
            log.warn("Cannot parse the message {}", e.getMsg());
            return Supervision.restart();
        } else {
            log.error("Fatal error occurred", exception);
            return Supervision.stop();
        }
    }

    private StreamFactory() {
    }

}
