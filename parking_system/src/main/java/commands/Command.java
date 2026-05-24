package commands;

import java.util.Properties;

/*
 * Default Command interface for all commands.
 */
public interface Command {
    String getCommandName();
    
    String getDisplayName();
    
    String execute(Properties params);
    
    void checkParameters(Properties params) throws IllegalArgumentException;
    
}
