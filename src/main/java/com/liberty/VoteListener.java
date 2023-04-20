package com.liberty;

import java.util.function.Consumer;

import com.liberty.votes.Vote;
import com.liberty.votes.VoteMute;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class VoteListener extends ListenerAdapter {

    @Override
    public void onUserContextInteraction(UserContextInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("mute (vote)")) {
            VoteMute vm = new VoteMute(event);
            if (vm.isValidRequest()) {
                // Reply to the interaction
                event.deferReply(true).queue((t) -> {
                    t.sendMessage(vm.toString()).addActionRow(
                            Button.primary(Vote.VOTE_INFAVOR_COMPONENTID, "Vote in Favor"),
                            Button.secondary(Vote.VOTE_AGAINST_COMPONENTID, "Vote against"))
                            .queue((msg) -> {
                                vm.setMessage(msg);
                                VoteExecutor.ongoingVoteSet.put(msg.getIdLong(), vm);
                            });

                });
            } else {
                event.reply("Not Valid").queue();

            }

        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Vote potentialVote = VoteExecutor.ongoingVoteSet.get(event.getMessageIdLong());
        if (potentialVote != null) {
            potentialVote.registerVote(event);
        }
    }

}
