package com.flutter.example.sgs.node.stream;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.*;
import akka.kafka.javadsl.Committer;
import akka.kafka.javadsl.Consumer;
import akka.stream.Materializer;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Source;
import com.flutter.example.sgs.node.config.InboundConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

public class StreamFactory {

    public static Consumer.DrainingControl<Done> inboundStreamOf(InboundConfig inboundConfig,
                                                                 ActorSystem system,
                                                                 Materializer materializer) {
        ConsumerSettings<String, String> settings = ConsumerSettings.create(inboundConfig.KAFKA_CONSUMER_CONFIG,
                new StringDeserializer(), new StringDeserializer());

        CommitterSettings committerSettings = CommitterSettings.create(system);

        Source<ConsumerMessage.CommittableMessage<String, String>, Consumer.Control> source =
                Consumer.committableSource(settings, Subscriptions.topics(inboundConfig.TOPIC));

        return source
                .map(m -> {
                    System.out.println("MESSAGE:" + m);
                    return m;})
                .map(ConsumerMessage.CommittableMessage::committableOffset)
                .toMat(Committer.sink(committerSettings.withMaxBatch(1)), Keep.both())
                .mapMaterializedValue(Consumer::createDrainingControl)
                .run(materializer);
    }

    private StreamFactory() {
    }

}
