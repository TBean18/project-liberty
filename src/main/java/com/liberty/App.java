package com.liberty;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

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
                .addEventListeners(new VoteListener())
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("ping", "Calculates Ping of the bot"),
                Commands.user("mute (vote)"))
                .queue();

    }
}
