package com.liberty.votes;

import java.util.HashSet;

import com.liberty.VoteExecutor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@Slf4j
public abstract class Vote {

    public static abstract class Listener extends ListenerAdapter {

        @Override
        abstract public void onUserContextInteraction(UserContextInteractionEvent event);

        public void replyWithVoteMesssage(UserContextInteractionEvent event) {

        }
    }

    public static final String VOTE_INFAVOR_COMPONENTID = "yes";
    public static final String VOTE_AGAINST_COMPONENTID = "no";

    /**
     * Denotes if the request which spawned this object was valid.
     */
    boolean validRequest = false;

    /**
     * Number of Votes required for the proposition to pass.
     */
    int votesRequired;

    HashSet<Member> votesInFavor = new HashSet<>();
    HashSet<Member> votesAgainst = new HashSet<>();

    /**
     * Event which spawned the vote.
     */
    UserContextInteractionEvent parentEvent;

    /**
     * Guild which contains the vote.
     * All votes need to be performed inside of a guild.
     */
    Guild guild;

    /**
     * Channel which contains the vote.
     */
    @NonNull
    StandardGuildChannel channel;
    Member author;

    /**
     * Message containing the Vote Display
     */
    @NonNull
    Message message;

    public void setMessage(Message message) {
        this.message = message;
    }

    protected void composeMessage(InteractionHook hook) {
        hook.sendMessage(this.toString()).addActionRow(
                Button.primary(Vote.VOTE_INFAVOR_COMPONENTID, "Vote in Favor"),
                Button.secondary(Vote.VOTE_AGAINST_COMPONENTID, "Vote against"))
                .queue((msg) -> {
                    this.setMessage(msg);
                    VoteExecutor.getOngoingVoteSet().put(msg.getIdLong(), this);
                }, t -> {
                    log.error("Error while composing message for vote mute", t);
                });
    }

    public Message getMessage() {
        return message;
    }

    Vote() {
    }

    Vote(GenericContextInteractionEvent e) {
        if (e.getGuild() == null)
            throw new NullPointerException("Votes must contain a guild");
        this.guild = e.getGuild();

        if (e.getMember() == null)
            throw new NullPointerException("Votes must contain an author");
        this.author = e.getMember();

        if (e.getChannel() == null)
            throw new NullPointerException("Channel should never be nullable");
        this.channel = (@NonNull StandardGuildChannel) e.getGuildChannel();

    }

    public Channel getChannel() {
        return this.channel;
    }

    abstract public String toString();

    public String getVoteStatusString() {
        String ret = String.format("Votes Required = %d\nVotes for = %d\nVotes Against = %d",
                this.getVotesRequired(), this.votesInFavor.size(), this.votesAgainst.size());
        return ret;
    }

    public boolean isValidRequest() {
        return validRequest;
    }

    public int getVotesRequired() {
        return votesRequired;
    }

    public UserContextInteractionEvent getParentEvent() {
        return parentEvent;
    }

    public Guild getGuild() {
        return guild;
    }

    public boolean checkVoteCount() {
        if (votesInFavor.size() >= votesRequired) {

            return true;
        }

        if (votesAgainst.size() >= votesRequired) {
            // Delete the message

            return true;
        }

        return false;
    }

    public void registerVote(ButtonInteractionEvent e) {
        Member member = e.getMember();
        if (e.getComponentId().equalsIgnoreCase(VOTE_INFAVOR_COMPONENTID)) {
            if (this.votesInFavor.contains(member)) {
                this.votesInFavor.remove(member);
            } else {
                this.votesInFavor.add(member);
            }
            if (checkVoteCount()) {
                this.execute();
                return;
            }
        }
        if (e.getComponentId().equalsIgnoreCase(VOTE_AGAINST_COMPONENTID)) {
            if (this.votesAgainst.contains(member)) {
                this.votesAgainst.remove(member);
            } else {
                this.votesAgainst.add(member);
            }
            if (checkVoteCount()) {
                cleanUp();
                return;
            }
        }
        e.editMessage(toString()).queue();
    }

    /**
     * Delete Message
     */
    public void cleanUp() {
        VoteExecutor.getOngoingVoteSet().remove(message.getIdLong());
        message.reply("Vote Concluded").queue();
        message.delete().queue();
    }

    abstract protected void execute();

}
