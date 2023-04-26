package com.liberty.votes;

import java.util.HashSet;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

@Slf4j
public abstract class Vote {
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
    Channel channel;
    Member author;

    /**
     * Message containing the Vote Display
     */
    @NonNull
    Message message;

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
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
        this.channel = e.getChannel();

    }

    public Channel getChannel() {
        return this.channel;
    }

    public String toString() {
        return "Generic Vote Text";
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
    void cleanUp() {
        message.reply("Vote Concluded").queue();
        message.delete().queue();
    }

    protected void execute() {
        log.error("Default vote execution invoked for {}", this.toString());
    }

}
