package javapoptools;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author gef
 */
public class FileFilterFactory {

    public static FileFilter getFilter(final String description, final String[]  extensions)
    {
     return new FileFilter() {

         HashSet<String> exts = new HashSet<String>(Arrays.asList(extensions));
            @Override
            public boolean accept(File f) {
                return (exts.contains(getExtension(f)));
            }

            @Override
            public String getDescription() {
                return description;
            }
        };
    } 

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
