package com.gmail.trentech.pjp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class HelpElement extends CommandElement {

    public HelpElement(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        final StringBuilder ret = new StringBuilder(args.next());
        
        while (args.hasNext()) {
            ret.append(' ').append(args.next());
        }
        String rawCommand = ret.toString();

        System.out.println(rawCommand);
        
        Optional<Help> optionalHelp = Help.get(rawCommand);

        if(optionalHelp.isPresent()) {
        	return optionalHelp.get();
        }
        
		return args.createError(Text.of(TextColors.RED, "Command not found"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    	List<String> list = new ArrayList<>();

        for(Help help : Help.getAll()) {
        	list.add(help.getRawCommand());
        }

        return list;
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of(getKey().getColor(), "<" + getKey(), ">");
    }
}
