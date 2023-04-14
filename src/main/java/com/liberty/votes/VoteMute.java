package com.liberty.votes;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.internal.entities.UserImpl;

public class VoteMute {
    /**
     * Denotes if the request which spawned this object was valid.
     */
    boolean validRequest = false;
    Member target, author;

    public VoteMute(UserContextInteractionEvent e) {
        // User must be in a guild voice channel
        if (e.getGuild() == null)
            return;
        author = e.getGuild().retrieveMemberById(e.getUser().getIdLong()).complete();
        if (author == null)
            return;
        if (author.getVoiceState() == null)
            return;

        this.validRequest = true;
        return;
    }

    public void execute() {
        // TODO
        return;
    }

    public boolean isValid() {
        return this.validRequest;
    }

}
