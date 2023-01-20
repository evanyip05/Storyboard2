package Storyboard2.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class TextFile {
    private final String dir;

    /** dir */
    public TextFile(String dir) {
        this.dir = dir;
    }

    /** returns string from a text file using a directory, returns an empty string if read fails*/
    public String readContent() {
        String res = "";
        try {Scanner reader = new Scanner(Path.of(dir));while (reader.hasNext()) {res = res + reader.nextLine()+"\n";}return res;}
        catch (IOException e) {System.out.println("file did not exist or could not read"); return res;}
    }

    /** overwrite a file with a string*/
    public void writeContent(String content) {
        try {FileWriter writer = new FileWriter(dir); writer.write(content); writer.close();}
        catch (IOException e) {System.out.println("file does not exist or could not write");}
    }
}
