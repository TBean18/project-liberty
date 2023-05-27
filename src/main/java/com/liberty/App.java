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
    public static void main(String[] args) {
        Dotenv dotenv = new DotenvBuilder().load();
        String token = dotenv.get("DISCORD_TOKEN");
        log.info(token);

        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new VoteMute.Listener(),
                        new VoteAFK.Listener())
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("ping", "Calculates Ping of the bot"),
                Commands.user(VoteMute.Listener.MUTE_COMMAND_STRING),
                Commands.user(VoteAFK.Listener.AFK_COMMAND_STRING))
                .complete();

        VoteExecutor.cleanerThread.start();

    }
}
