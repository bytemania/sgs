package com.flutter.example.sgs.node.util;

import akka.kafka.ConsumerMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flutter.example.sgs.cluster.FeedShardMsg;
import com.flutter.example.sgs.node.actor.feed.FeedCommand;
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

    private static String generateFeedShardKey(Feed feed, String id) {
        return feed.toString() + "_" + id;
    }

    public static Tuple2<ConsumerMessage.CommittableMessage, FeedShardMsg> translateInbound(
            ConsumerMessage.CommittableMessage committableMessage) {
        try {
            String value = committableMessage.record().value().toString();
            InboudApi inboudApi = OBJECT_MAPPER.readValue(value, InboudApi.class);

            return Tuple.of(committableMessage,
                    FeedShardMsg.of(generateFeedShardKey(Feed.OPTA, inboudApi.getId()),
                            FeedUpdateCommand.of(inboudApi)));
        } catch (IOException e) {
            throw new ParseException(e, committableMessage);
        }
    }

    public static FeedShardMsg generateFeedShardMessage(Feed feed, String id, FeedCommand command){
        return FeedShardMsg.of(generateFeedShardKey(feed, id), command);
    }

    private Util () {
    }
}
