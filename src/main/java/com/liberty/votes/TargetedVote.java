package com.liberty.votes;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public abstract class TargetedVote extends Vote {

    @Getter
    private Member target;

    TargetedVote(UserContextInteractionEvent e) {
        super(e);
        target = e.getTargetMember();
        if (target == null) {
            throw new NullPointerException("TargetedVotes require a Target");
        }

        // Calculate Number of required votes

    }

    @Override
    public int getVotesRequired() {
        this.votesRequired = (this.channel.getMembers().size() - 2) / 2 + 1;
        return this.votesRequired;
    }

    @Override
    abstract protected void execute();

    @Override
    abstract public String toString();
}
