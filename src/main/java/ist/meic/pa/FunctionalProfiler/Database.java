/*
 * Class that keeps the records of CompileTime Classes,
 * responsible for incrementing counters and printing the results.
 */
package ist.meic.pa.FunctionalProfiler;

import java.util.HashMap;
import java.util.Map;
@NotIntersect
public class Database {
    public static Map<Class,Entry> dictionary = new HashMap<>();

    public static String toText(){
        CommandRead cmdRead= new CommandRead();
        CommandWrite cmdWrite= new CommandWrite();

        String buffer= "";
        for(Class c: dictionary.keySet()){
            buffer= buffer + c+" ->" + cmdRead.sumText(c)+cmdWrite.sumText(c)+"\n";
        }

        return cmdRead.totalText()+" "+cmdWrite.totalText()+"\n"
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
