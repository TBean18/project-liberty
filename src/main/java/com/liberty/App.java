package com.liberty;

import com.liberty.votes.VoteAFK;
import com.liberty.votes.VoteMute;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * Hello world!
 *
 */
@Slf4j
public class App {

    /**
     * Singleton JDA instance
     */
    private static JDA jda = null;

    public static void main(String[] args) {

        jda = getJda();
        VoteExecutor.cleanerThread.start();

    }

    public static JDA getJda() {
        if (jda == null) {
            Dotenv dotenv = new DotenvBuilder().load();
            String token = dotenv.get("DISCORD_TOKEN");
            log.info("Creating JDA instance with token: {}", token);
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(new VoteMute.Listener(),
                            new VoteAFK.Listener(), new VoteTallier())
                    .build();

            jda.updateCommands().addCommands(
                    Commands.slash("ping", "Calculates Ping of the bot"),
                    Commands.user(VoteMute.Listener.MUTE_COMMAND_STRING),
                    Commands.user(VoteAFK.Listener.AFK_COMMAND_STRING))
                    .complete();
        }
        return jda;

    }
}
