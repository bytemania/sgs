package com.flutter.example.sgs;

import akka.actor.ActorSystem;
import com.flutter.example.sgs.node.actor.guardian.GuardianActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class Main {

    public static void main(String[] args) {
            startup("2551", "2552", "2553");
    }

    private static void startup (String ... ports) {
        Arrays.stream(ports).forEach(Main::createSystem);
    }

    private static void createSystem(String port) {
        Config config = ConfigFactory.parseString("akka.remote.artery.canonical.port=" + port)
                .withFallback(ConfigFactory.load());
        ActorSystem system = ActorSystem.create("SgsShardingSystem", config);
        system.actorOf(GuardianActor.props());
    }

}
