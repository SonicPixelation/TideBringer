package dev.sonicpixelation.tidebringer;

import org.w3c.dom.*;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;

public class DocumentManager{

    private DocumentManager(){}

    //
    public static void createNewFeed(String channelTitle, String filename) throws Exception{
        createNewFeed(channelTitle, null, filename);
    }
    public static void createNewFeed(String channelTitle, String websiteURL, String filename)throws Exception{
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();

        //creates the root rss tag
        Element rootElement = doc.createElement("rss");
        doc.appendChild(rootElement);

        //adds version tag to the rss tag
        Attr attrVersion = doc.createAttribute("version");
        attrVersion.setValue("2.0");
        rootElement.setAttributeNode(attrVersion);

        //create the core channel for all of the torrents
        Element channelE = doc.createElement("channel");
        rootElement.appendChild(channelE);

        //sets the title for the rss feed channel
        Element channelTitleE = doc.createElement("title");
        channelTitleE.appendChild(doc.createTextNode(channelTitle));
        channelE.appendChild(channelTitleE);

        if(websiteURL != null) {
            Element linkE = doc.createElement("link");
            linkE.appendChild(doc.createTextNode(websiteURL));
            channelE.appendChild(linkE);
        }

        //write the xml document
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filename));

        transformer.transform(source, result);
    }


    public static void addNewItem(String itemId, String itemTitle, String torrentURL, String filename)throws Exception{
        addNewItem(itemId, itemTitle, null, torrentURL, filename);
    }
    public static void addNewItem(String itemId, String itemTitle, String description, String torrentURL, String filename) throws Exception{
        File file = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);



        NodeList nodeList = doc.getElementsByTagName("item");

        for(int i = 0; i < nodeList.getLength(); i++){
            NamedNodeMap attr = nodeList.item(i).getAttributes();
            Node itemNodeId = attr.getNamedItem("id");
                if (itemNodeId.getNodeValue().equals(itemId)){
                System.err.println("Id tag " + itemId + " already exists\n");
                throw new RuntimeException();
            }
        }

        Node channel = doc.getElementsByTagName("channel").item(0);

        //adding item
        Element newItem = doc.createElement("item");
        channel.appendChild(newItem);

        //adding itemId
        Attr attrItem = doc.createAttribute("id");
        attrItem.setValue(itemId);
        newItem.setAttributeNode(attrItem);

        //item title
        Element itemTtl = doc.createElement("title");
        itemTtl.appendChild(doc.createTextNode(itemTitle));
        newItem.appendChild(itemTtl);

        if(description != null){
            Element desc = doc.createElement("description");
            desc.appendChild(doc.createTextNode(description));
            newItem.appendChild(desc);
        }

        String hashCode = getTorrentHash(torrentURL);
        Element guid = doc.createElement("guid");
        guid.appendChild(doc.createTextNode(hashCode));
        newItem.appendChild(guid);

        //creates the enclosure tag
        Element enclosure = doc.createElement("enclosure");
        newItem.appendChild(enclosure);

        //sets url attribute
        Attr attrUrl = doc.createAttribute("url");
        attrUrl.setValue(torrentURL);
        enclosure.setAttributeNode(attrUrl);

        //sets the type attribute
        Attr attrType = doc.createAttribute("type");
        attrType.setValue("application/x-bittorrent");
        enclosure.setAttributeNode(attrType);


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filename));

        transformer.transform(source, result);
    }

    public static void updateItem(String itemId, String itemTitle, String torrentURL, String filename) throws Exception{
        updateItem(itemId, itemTitle, null, torrentURL, filename);
    }
    public static void updateItem(String itemId, String itemTitle, String description, String torrentUrl, String filename) throws Exception{
        File file = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);


        NodeList nodeList = doc.getElementsByTagName("item");
        Node targetNode = null;
        for(int i = 0; i < nodeList.getLength(); i++){
            NamedNodeMap attr = nodeList.item(i).getAttributes();
            Node itemNode = attr.getNamedItem("id");
            if (itemNode.getNodeValue().equals(itemId)){
                targetNode = nodeList.item(i);
            }
        }
        if(targetNode == null){
            System.err.println("there is no item with that Id\n");
            throw new RuntimeException();
        }

        NodeList childList = targetNode.getChildNodes();
        for(int j = 0; j < childList.getLength(); j++){
            Node node = childList.item(j);
            String tempName = node.getNodeName();

            switch(tempName){

                //basic cases
                case "title"    : node.setTextContent(itemTitle);break;
                case "guid"     : node.setTextContent(getTorrentHash(torrentUrl)); break;

                //facny case with at
                case "enclosure":
                    NamedNodeMap nodeMap = node.getAttributes();
                    //url node
                    Node urlNode = nodeMap.getNamedItem("url");
                    urlNode.setNodeValue(torrentUrl);
                    //the type node doesn't need to be changed
                    break;
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filename));

        transformer.transform(source, result);
    }

    public static void removeItem(String itemId, String filename) throws Exception{
        File file = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);


        NodeList nodeList = doc.getElementsByTagName("item");
        Node targetNode = null;
        for(int i = 0; i < nodeList.getLength(); i++){
            NamedNodeMap attr = nodeList.item(i).getAttributes();
            Node itemNode = attr.getNamedItem("id");
            if (itemNode.getNodeValue().equals(itemId)){
                targetNode = nodeList.item(i);
            }
        }
        if(targetNode == null){
            System.err.println("there is no item with the Id " + itemId + "\n");
            throw new RuntimeException();
        }

        targetNode.getParentNode().removeChild(targetNode);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filename));

        transformer.transform(source, result);
    }

    //hashes the torrent file
    public static String getTorrentHash(String url){
        String[] urlSplit  = url.replaceAll("//","").split("/");
        String filename = urlSplit[urlSplit.length - 1];
        System.out.println("Filename: "  + filename);
        try{
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(filename);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();

            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            InputStream fis = new FileInputStream(new File(filename));
            int n = 0;
            byte[] buffer = new byte[8192];
            while(n != -1){
                n = fis.read(buffer);
                if(n > 0){
                    digest.update(buffer, 0, n);
                }
            }
            fis.close();
            String hash = new HexBinaryAdapter().marshal(digest.digest());
            File file = new File(filename);
            file.delete();
            System.out.println("Hash: " + hash);
            return hash;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
