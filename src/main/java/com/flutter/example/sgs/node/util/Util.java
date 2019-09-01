package com.flutter.example.sgs.node.util;

import akka.kafka.ConsumerMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flutter.example.sgs.cluster.FeedShardMsg;
import com.flutter.example.sgs.node.actor.feed.FeedUpdateCommand;
import com.flutter.example.sgs.node.exception.ParseException;
import com.flutter.example.sgs.node.model.Feed;
import com.flutter.example.sgs.node.model.InboudApi;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Util {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Tuple2<ConsumerMessage.CommittableMessage, FeedShardMsg> translateInbound(
            ConsumerMessage.CommittableMessage committableMessage) {
        String value = committableMessage.record().value().toString();

        InboudApi inboudApi = null;
        try {
            inboudApi = OBJECT_MAPPER.readValue(value, InboudApi.class);
        } catch (IOException e) {
            throw new ParseException(e, committableMessage);
        }

        String key = Feed.OPTA.toString() + "_" + inboudApi.getId();
        return Tuple.of(committableMessage, FeedShardMsg.of(key, FeedUpdateCommand.of(inboudApi)));
    }

    private Util () {
    }
}
