package com.liberty.votes;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public class VoteMute extends Vote {

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
        String ret = String.format("Vote to Mute: %s\nVotes Required = %d\nVotes for = %d\nVotes Against = %d",
                target.getNickname(), this.getVotesRequired(), this.votesInFavor.size(), this.votesAgainst.size());
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
