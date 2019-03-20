package ist.meic.pa.FunctionalProfiler;

import java.util.HashMap;
import java.util.Map;

public class Database {
    public static Map<Class,Entry> dictionary = new HashMap<>();

    public static String toText(){
        int totalreads=0;
        int totalwrites=0;
        String buffer= "";
        for(Class c: dictionary.keySet()){
            totalreads = totalreads + dictionary.get(c).readerCounter;
            totalwrites = totalwrites + dictionary.get(c).writeCounter;

            buffer= buffer + c+" -> reads: "+dictionary.get(c).readerCounter+ " writes: "+
                    dictionary.get(c).writeCounter+"\n";
        }

        return "Total reads: "+ totalreads+" Total writes: "+totalwrites+"\n"
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
