package commands;

import java.util.Properties;

public interface Command {
	
    String getCommandName();
    
    String getDisplayName();
    
    String execute(Properties params);
    
    void checkParameters(Properties params) throws IllegalArgumentException;
    
}
