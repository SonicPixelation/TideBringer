package dev.sonicpixelation.tidebringer;



public class CommandManager {




    public void printUsage(){
        System.out.println("TideBringer a easy rss feed torrent creator");
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("TideBringer new <channelTitle> <filename>");
        System.out.println("TideBringer new <channelTitle> <websiteURL> <filename>");
        System.out.println("TideBringer add <itemId> <title> <torrentUrl> <filename>" );
        System.out.println("TideBringer add <itemId> <title> <torrentUrl> <description> <filename>" );
        System.out.println("TideBringer update <itemId> <title> <torrentUrl> <filename>" );
        System.out.println("TideBringer update <itemId> <title> <torrentUrl> <description> <filename>" );
        System.out.println("TideBringer remove <itemId> <filename>");
        System.out.println("TidBringer version");
        System.out.println("TideBringer help");
}

    public boolean handleCommand(String[] args){
        if(args.length < 3) {
            return false;
        }
        if(args[0].equals("new")){
            if(args.length == 3) {
                try {
                    DocumentManager.createNewFeed(args[1], args[2]);
                    System.out.println(args[2] + " torrent feed file created!");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }else if(args.length == 4){
                try{
                    DocumentManager.createNewFeed(args[1], args[2], args[3]);
                    System.out.println(args[3] + " torrent feed file created!");
                    return true;
                }catch(Exception e){
                    return false;
                }
            }else{
                return false;
            }
        }else if(args[0].equals("add")){
            if(args.length == 5){
                try{
                    DocumentManager.addNewItem(args[1],args[2], args[3], args[4]);
                    System.out.println("Added " + args[2] + " to  feed " + args[4]);
                    return true;
                }catch(Exception e){
                    return false;
                }
            }else if(args.length == 6){
                try{
                    DocumentManager.addNewItem(args[1], args[2], args[3], args[4], args[5]);
                    System.out.println("Added " + args[2] + " to feed " + args[5]);
                    return true;
                }catch(Exception e){
                    return false;
                }
            }else{
                return false;
            }
        }else if(args[0].equals("update")) {
            if(args.length == 5){
                try {
                    DocumentManager.updateItem(args[1], args[2], args[3], args[4]);
                    System.out.println("Updated to " + args[2] + " in feed " + args[4]);
                    return true;
                }catch(Exception e){
                    return false;
                }
            }else if(args.length == 6){
                try {
                    DocumentManager.updateItem(args[1], args[2], args[3], args[4], args[5]);
                    System.out.println("Updated to " + args[2] + " in feed " + args[5]);
                    return true;
                }catch(Exception e){
                    return false;
                }
            }
        }else if(args[0].equals("remove")){
            try{
                DocumentManager.removeItem(args[1], args[2]);
                System.out.println("Removed " + args[1] + " item from feed " + args[2]);
                return true;
            }catch(Exception e){
                return false;
            }
        }else if(args[0].equals("help")){
            return false;
        }else if(args[0].equals("version")){
            System.out.println(TideVersion.VERSION);
            return true;
        }
        return false;
    }
}
