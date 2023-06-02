package com.liberty.votes;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public class VoteAFK extends TargetedVote {

    public static class Listener extends Vote.Listener {

        public static final String AFK_COMMAND_STRING = "Vote AFK";

        @Override
        public void onUserContextInteraction(UserContextInteractionEvent event) {
            if (!event.getName().equalsIgnoreCase(AFK_COMMAND_STRING)) {
                return;
            }
            event.deferReply().queue();

            VoteAFK pv = new VoteAFK(event);
            pv.composeMessage(event.getHook());

        }

    }

    private AudioChannel AFKChannel;

    VoteAFK(UserContextInteractionEvent e) {
        super(e);
        // AFK can only happen in a VC
        if (e.getChannel().getType().isAudio()) {
            channel = (AudioChannelUnion) e.getChannel();
        }

        AFKChannel = guild.getAfkChannel();
    }

    @Override
    protected void execute() {
        cleanUp();
        guild.moveVoiceMember(getTarget(), AFKChannel).queue();
    }

    @Override
    public String toString() {
        String ret = String.format("Vote to AFK: %s\n%s",
                getTarget().getNickname(), this.getVoteStatusString());
        return ret;
    }

}
