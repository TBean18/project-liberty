package com.liberty.votes;

import com.liberty.VoteExecutor;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class VoteMute extends Vote {

    public static class Listener extends Vote.Listener {

        public static final String MUTE_COMMAND_STRING = "toggle mute";

        @Override
        public void onUserContextInteraction(UserContextInteractionEvent event) {
            if (event.getName().equalsIgnoreCase(MUTE_COMMAND_STRING)) {
                event.deferReply().queue();
                VoteMute vm;
                // Check if its Mute or unmute
                if (event.getTargetMember().getVoiceState().isGuildMuted()) {
                    vm = new VoteUnmute(event);
                } else {
                    vm = new VoteMute(event);
                }

                if (vm.isValidRequest()) {
                    // Reply to the interaction
                    vm.composeMessage(event.getHook());
                } else {
                    event.reply("Not Valid").queue();

                }

            }
        }

    }

    Member target;
    private AudioChannelUnion channel;

    public VoteMute(UserContextInteractionEvent e) {
        super(e);
        target = e.getTargetMember();
        if (target == null)
            throw new NullPointerException("VoteMute requires a Target to mute");
        // Mute can only happen in a VC
        if (e.getChannel().getType().isAudio()) {
            channel = (AudioChannelUnion) e.getChannel();
        }
        // Both Target and Author must be in the same channel
        if (!(channel.getMembers().contains(target) && channel.getMembers().contains(author))) {
            return;
        }

        // Calculate the required Votes
        votesRequired = (channel.getMembers().size() - 2) / 2 + 1;
        this.validRequest = true;
        return;
    }

    @Override
    public String toString() {
        String ret = String.format("Vote to Mute: %s\n%s",
                target.getNickname(), this.getVoteStatusString());
        return ret;
    }

    @Override
    /**
     * Execute for Mute Votes
     */
    protected void execute() {
        boolean muteStatus = target.getVoiceState().isGuildMuted();
        cleanUp();
        target.mute(!muteStatus).queue();
    }

    public boolean isValidRequest() {
        return validRequest;
    }

    public Member getTarget() {
        return target;
    }

    public Member getAuthor() {
        return author;
    }

    public AudioChannelUnion getChannel() {
        return channel;
    }

    public int getVotesRequired() {
        return votesRequired;
    }

}
