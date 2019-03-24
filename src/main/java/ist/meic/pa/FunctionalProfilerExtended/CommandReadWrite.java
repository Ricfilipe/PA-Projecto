/*
 * Class extending Command, responsible for adding the read/write counter function to the
 * CompileTime Class read/write calls and counting the reads/writes for the class that called the read/write.
 */
package ist.meic.pa.FunctionalProfilerExtended;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class CommandReadWrite extends Command {

    // Adds the code that increments the write/read counter for the class
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
            ctConstructor.instrument(addCodeIfReader(templateRead));
        }
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            ctMethod.instrument(addCodeIfReader(templateRead));
        }
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            ctMethod.instrument(addCodeIfWriter(templateWrite));
        }
        for (CtConstructor ctConstructor : ctClass.getDeclaredConstructors()) {
            ctConstructor.instrument(addCodeIfWriter(templateWriteOnConstructor));
        }
    }

    // Returns the Expression Editor with the information if the accessed field was a read or not
    // If it was add code, otherwise do nothing
    public ExprEditor addCodeIfReader(String template) {
        return new ExprEditor() {
            public void edit(FieldAccess fa)
                    throws CannotCompileException {
                if (fa.isReader()) {
                    String name = fa.getFieldName();
                    fa.replace(String.format(template,
                            name));
                }
            }
        };
    }

    // Returns the Expression Editor with the information if the accessed field was a write or not
    // If it was and was not a self write in constructor add code, otherwise do nothing
    public ExprEditor addCodeIfWriter(String template) {
        return new ExprEditor() {
            public void edit(FieldAccess fa)
                    throws CannotCompileException {
                if (fa.isWriter() ) {
                    String name = fa.getFieldName();
                    fa.replace(String.format(template,
                            name));
                }
            }
        };
    }

    // Returns string with program's total writes/reads
    @Override
    public String totalText() {
        int totalwrites=0;
        for(Class c: Database.dictionary.keySet()){
            totalwrites = totalwrites + Database.dictionary.get(c).writeCounter;
        }

        int totalreads=0;
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

    @Override
    public void addFields() { return; }
}
