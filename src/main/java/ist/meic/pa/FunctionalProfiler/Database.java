/*
 * Class that keeps the records of CompileTime Classes,
 * responsible for incrementing counters and printing the results.
 */
package ist.meic.pa.FunctionalProfiler;

import java.util.*;

@NotIntersect
public class Database {

    public static class ClassComparator  implements Comparator<Class> {
        public int compare(Class obj1, Class obj2) {
            String s1 = obj1.getName();
            String s2 = obj2.getName();

            return s1.compareTo(s2);
        }
    }

    public static Map<Class,Entry> dictionary = new HashMap<>();

    public static String toText(){
        CommandReadWrite cmd= new CommandReadWrite();

        String buffer= "";

        List<Class> sorted = new ArrayList<>(dictionary.keySet());
        Collections.sort(sorted, new ist.meic.pa.FunctionalProfilerExtended.Database.ClassComparator());

        for(Class c: sorted){
            buffer=  buffer + "\n"+ c+" ->" + cmd.sumText(c) ;
        }

        return cmd.totalText()
                + buffer;
    }

    public static void addWriter(Class c){
        if(dictionary.get(c)==null){
            addClass(c);
        }
        dictionary.get(c).writeCounter++;
    }

    public static void addReader(Class c){
        if(dictionary.get(c)==null){
            addClass(c);
        }
        dictionary.get(c).readerCounter++;
    }

    public static void addClass(Class c){
        Database.dictionary.put(c,new Entry());
    }
}
