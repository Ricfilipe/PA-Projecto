/*
 * Class extending Command, responsible for adding the read/write counter function to the
 * CompileTime Class read/write calls and counting the reads/writes for the class that called the read/write.
 */
package ist.meic.pa.FunctionalProfiler;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

@SuppressWarnings("Duplicates")
public class CommandReadWrite extends Command{

    // Adds the code that increments the write/read counter for the class
    @Override
    public void execute(CtClass ctClass)throws NotFoundException, CannotCompileException {
        final String templateWrite =
            "{" +
                " Database.addWriter($0.getClass());" +
                " $0.%s = $1;" +
            "}";
        final String templateWriteOnConstructor =
            "{" +
                "if($0 != this){"+
                " Database.addWriter($0.getClass());" +
                "}" +
                " $0.%s = $1;" +
            "}";
        final String templateRead =
            "{" +
                " Database.addReader($0.getClass());" +
                " $_=$0.%s;" +
            "}";
        for (CtConstructor ctConstructor : ctClass.getDeclaredConstructors()) {
            ctConstructor.instrument(addCodeIfReaderWriter(templateRead,templateWriteOnConstructor));
        }
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            ctMethod.instrument(addCodeIfReaderWriter(templateRead,templateWrite));
        }

    }

    // Returns the Expression Editor with the information if the accessed field was a read or write
    // If it was add code, otherwise do nothing
    public ExprEditor addCodeIfReaderWriter(String template1, String template2) {
        return new ExprEditor() {
            public void edit(FieldAccess fa)
                    throws CannotCompileException {
            if (fa.isReader()) {
                String name = fa.getFieldName();
                fa.replace(String.format(template1,
                        name));
            } else if (fa.isWriter()) {
                String name = fa.getFieldName();
                fa.replace(String.format(template2,
                        name));
            }
            }
        };
    }

    // Returns string with program's total writes/reads
    @Override
    public String totalText() {
        int totalwrites = 0;
        for(Class c: Database.dictionary.keySet()){
            totalwrites = totalwrites + Database.dictionary.get(c).writeCounter;
        }

        int totalreads = 0;
        for (Class c : Database.dictionary.keySet()) {
            totalreads = totalreads + Database.dictionary.get(c).readerCounter;
        }
        
        return "Total reads: " + totalreads + " Total writes: " + totalwrites;
    }

    // Returns string with class total writes/reads
    @Override
    public String sumText(Class c) {
        return  " reads: " + Database.dictionary.get(c).readerCounter + " write: " + Database.dictionary.get(c).writeCounter;
    }
}
