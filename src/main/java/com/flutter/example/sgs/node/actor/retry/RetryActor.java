package com.flutter.example.sgs.node.actor.retry;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.flutter.example.sgs.cluster.Nack;
import io.vavr.control.Either;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import scala.concurrent.Future;

import java.time.Duration;

public class RetryActor extends AbstractActor {

    public static Props props(int tries, int timeoutSeconds, int intervalSeconds, ActorRef forwardTo) {
        return Props.create(RetryActor.class, () -> new RetryActor(tries, timeoutSeconds, intervalSeconds, forwardTo));
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    private static class InternalRetry {
        private final ActorRef originalSender;
        private final Object message;
        private final int times;
        private final String id;
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    private static class InternalResponse {
        private final ActorRef originalSender;
        private final Either<Throwable, Object> result;
        private final String id;
    }

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final int tries;
    private Timeout timeout;
    private final Duration interval;
    private final ActorRef forwardTo;

    private AbstractActor.Receive loop;

    private RetryActor(int tries, int timeoutSeconds, int intervalSeconds, ActorRef forwardTo) {
        this.tries = tries;
        this.timeout = Timeout.create(Duration.ofSeconds(timeoutSeconds));
        this.interval = Duration.ofSeconds(intervalSeconds);
        this.forwardTo = forwardTo;

        loop = receiveBuilder()
                .match(InternalRetry.class, this::retryMessage)
                .match(InternalResponse.class, this::responseMessage)
                .matchAny(m ->log.warning("No handling defined for message {}", m))
                .build();

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SendRetryCommand.class, this::receiveMessage)
                .build();
    }

    private void receiveMessage(SendRetryCommand command) {
        getContext().become(loop);
        self().tell(InternalRetry.of(sender(), command.getMessage(), tries, command.getId()), Actor.noSender());
    }

    private void retryMessage(InternalRetry internalRetry) {
        Future<Object> scalaFuture = Patterns.ask(forwardTo, internalRetry.message, timeout);
        scalaFuture.onComplete(new OnComplete<>() {
            @Override
            public void onComplete(Throwable failure, Object response) {
                if (failure == null) {
                    log.info("@@@@@@@@@@@@@@@@@@@@@@@@ SUCCESS RESPONSE:{}", response);
                    self().tell(InternalResponse.of(
                            internalRetry.originalSender,
                            Either.right(response),
                            internalRetry.getId()), Actor.noSender());

                } else if (internalRetry.getTimes() > 1) {
                    log.info("@@@@@@@@@@@@@@@@@@@@@@@@ ERROR TIMES:{}", internalRetry.getTimes());
                    getContext().getSystem().scheduler().scheduleOnce(
                            interval,
                            self(),
                            InternalRetry.of(
                                    internalRetry.originalSender,
                                    internalRetry.getMessage(),
                                    internalRetry.getTimes() - 1,
                                    internalRetry.getId()),
                            getContext().getDispatcher(),
                            Actor.noSender());

                } else if(internalRetry.getTimes() == 1) {
                    log.info("@@@@@@@@@@@@@@@@@@@@@@@@ ERROR NO MORE TIMES FAILURE :{}", failure);
                    self().tell(InternalResponse.of(
                            internalRetry.originalSender,
                            Either.left(failure),
                            internalRetry.getId()), Actor.noSender());

                } else {
                    log.error("Error occurred", failure);
                }
            }
        }, getContext().getDispatcher());
    }

    private void responseMessage(InternalResponse response) {
        if (response.result.isLeft()) {
            log.error("Message not delivered id:{} from:{} to:{}", response.getId(), response.originalSender.path(),
                    forwardTo.path());
            response.originalSender.tell(Nack.of(response.getId()), Actor.noSender());
        } else {
            response.originalSender.tell(response.result.get(), Actor.noSender());
        }

        log.info("@@@@@@@@@@@@@@@@@@@@@@@@ STOPPING ACTOR for id: {}", response.getId());
        getContext().stop(self());
    }

}
