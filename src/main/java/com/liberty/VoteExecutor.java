package com.liberty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.liberty.votes.Vote;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class VoteExecutor extends ListenerAdapter {
    // Ongoing Votes
    static final HashMap<Long, Vote> ongoingVoteSet = new HashMap<>();

    TextChannel getChannelForVoteCreation(Vote vote) {

        List<TextChannel> textChannels = vote.getGuild().getTextChannels();
        for (TextChannel textChannel : textChannels) {
            if (textChannel.canTalk()) {
                return textChannel;
            }
        }

        throw new ArrayIndexOutOfBoundsException("No text channel available for Vote Creation");

    }

    public boolean createVoteMessage(Vote vote) {
        TextChannel voteChannel = getChannelForVoteCreation(vote);

        // Construct the message
        Message voteMessage = voteChannel.sendMessage(vote.toString()).addActionRow(
                Button.primary("yes", "Vote in Favor"),
                Button.secondary("no", "Vote against")).complete();

        VoteExecutor.ongoingVoteSet.put(voteMessage.getIdLong(), vote);

        return true;

    }

}
