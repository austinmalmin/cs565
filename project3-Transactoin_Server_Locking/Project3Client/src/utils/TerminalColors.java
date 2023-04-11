package utils;

/**
 *
 * @author wolfdieterotte
 */
public interface TerminalColors {
    
    /*
    First digit color
    black   - 0
    red     - 1
    green   - 2
    yellow  - 3
    blue    - 4
    magenta - 5
    cyan    - 6
    white   - 7
    
    Second digit color
    foreground        - 3
    bright foreground - 9
    
    background        - 4
    bright background - 10
    
    
    reset - 0
    bold  - 1
    
    Also see: https://stackoverflow.com/questions/4842424/list-of-ansi-color-escape-sequences
    */
    
    public static final String                   RESET_COLOR = "\033[0m";
    public static final String                    BOLD_COLOR = "\033[1m";
    public static final String          RED_FOREGROUND_COLOR = "\033[31m";
    public static final String   BRIGHT_RED_FOREGROUND_COLOR = "\033[91m";
    public static final String        GREEN_BACKGROUND_COLOR = "\033[42m";
    public static final String BRIGHT_GREEN_BACKGROUND_COLOR = "\033[102m";
    public static final String OPEN_COLOR                    = "\033[30m\033[43m\033[1m";
    public static final String COMMIT_COLOR                  = "\033[30m\033[42m\033[1m";
    public static final String ABORT_COLOR                   = "\033[30m\033[41m\033[1m";
    public static final String READ_COLOR                    = "\033[30m\033[47m\033[1m";
    public static final String WRITE_COLOR                   = "\033[30m\033[47m\033[1m";
    public static final String RESTARTED_COLOR               = "\033[30m\033[47m\033[1m";
}
