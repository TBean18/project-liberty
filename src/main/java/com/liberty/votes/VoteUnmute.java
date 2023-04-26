package com.liberty.votes;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public class VoteUnmute extends VoteMute {

    public VoteUnmute(UserContextInteractionEvent e) {
        super(e);
    }

    @Override
    public String toString() {
        String ret = String.format("Vote to UnMute: %s\nVotes Required = %d\nVotes for = %d\nVotes Against = %d",
                target.getNickname(), this.getVotesRequired(), this.votesInFavor.size(), this.votesAgainst.size());
        return ret;
    }

}
