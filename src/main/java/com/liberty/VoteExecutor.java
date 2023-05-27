package com.liberty;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Set;

import com.liberty.votes.Vote;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Slf4j
public class VoteExecutor {

    public static class Cleaner implements Runnable {

        @Override
        public void run() {
            Set<Entry<Long, Vote>> entrySet = ongoingVoteSet.entrySet();
            while (true) {
                Instant instant = Instant.now().minusSeconds(300);
                int numCleaned = 0;
                for (Entry<Long, Vote> e : entrySet) {

                    if (e.getValue().getMessage().getTimeCreated().toInstant().isBefore(instant)) {
                        e.getValue().cleanUp();
                        ongoingVoteSet.remove(e.getKey());
                        numCleaned++;
                    }
                }
                log.info("Cleaner process cleaned {} votes | {} remaining", numCleaned, ongoingVoteSet.size());

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e1) {
                    log.error("Cleaner Thread unable to sleep", e1);
                    e1.printStackTrace();
                }

            }
        }

    }

    static final Thread cleanerThread = new Thread(new VoteExecutor.Cleaner(), "CleanerThread");

    // Ongoing Votes
    @Getter
    /**
     * MessageId to Vote mapping
     */
    static final ConcurrentHashMap<Long, Vote> ongoingVoteSet = new ConcurrentHashMap<>();

    TextChannel getChannelForVoteCreation(Vote vote) {

        List<TextChannel> textChannels = vote.getGuild().getTextChannels();
        for (TextChannel textChannel : textChannels) {
            if (textChannel.canTalk()) {
                return textChannel;
            }
        }

        throw new ArrayIndexOutOfBoundsException("No text channel available for Vote Creation");

    }

}
