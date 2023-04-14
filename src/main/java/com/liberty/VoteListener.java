package com.liberty;

import com.liberty.votes.VoteMute;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class VoteListener extends ListenerAdapter {

    @Override
    public void onUserContextInteraction(UserContextInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("mute (vote)")) {
            VoteMute vm = new VoteMute(event);
            if (vm.isValid()) {
                event.reply("Valid").queue();
                vm.execute();
            } else {
                event.reply("Not Valid").queue();

            }

        }
    }

}
