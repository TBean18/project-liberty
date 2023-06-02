package com.liberty;

import com.liberty.votes.Vote;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
public class VoteTallier extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Vote potentialVote = VoteExecutor.getOngoingVoteSet().get(event.getMessageIdLong());
        if (potentialVote != null) {
            log.info("Button Captured for {}", potentialVote);
            potentialVote.registerVote(event);
        }
    }

}
