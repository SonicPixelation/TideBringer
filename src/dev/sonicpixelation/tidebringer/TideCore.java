package dev.sonicpixelation.tidebringer;

import java.io.File;

public class TideCore{
    private String[] args;
    private CommandManager manager;

    public TideCore(String[] args){
        this.args = args;
        manager = new CommandManager();
        if(!manager.handleCommand(args)){
            manager.printUsage();
        }
    }


    public static void main(String[] args){
        new TideCore(args);
    }
}
