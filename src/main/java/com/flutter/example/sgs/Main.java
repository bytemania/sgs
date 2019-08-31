package com.flutter.example.sgs;

import akka.actor.ActorSystem;
import com.flutter.example.sgs.cluster.actor.guardian.GuardianActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class Main {

    public static void main(String[] args) {
        if (args.length != 0) {
            startup(args);
        }
        else {
            startup("2551", "2552", "2553", "0");
        }
    }

    private static void startup (String ... ports) {
        Arrays.stream(ports).forEach(Main::createSystem);
    }

    private static void createSystem(String port) {
        Config config = ConfigFactory.parseString("akka.remote.artery.canonical.port=" + port)
                .withFallback(ConfigFactory.load());

        log.info("Creating actorsystem for port {}", port);
        ActorSystem system = ActorSystem.create("SgsShardingSystem", config);
        system.actorOf(GuardianActor.props());

    }

}
