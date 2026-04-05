package commands;

public interface Command {
    String getCommandName();
    
    String getDisplayName();
    
    String execute(String[] params);
    
    void checkParameters(String[] params) throws IllegalArgumentException;
    
}
