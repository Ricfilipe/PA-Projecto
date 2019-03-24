/*
 * Class extending Command, responsible for adding the read counter function to the
 * CompileTime Class read calls and counting the reads for the class that called the read.
 */
package ist.meic.pa.FunctionalProfiler;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import java.util.Set;


public class CommandRead  extends Command{

    // Adds the code that increments the read counter for the class
    public void  execute(CtClass ctClass) throws NotFoundException, CannotCompileException {
        final String template =
                "{" +
                        " Database.addReader($0.getClass());" +
                        " $_=$0.%s;" +
                        "}";
        for (CtConstructor ctConstructor : ctClass.getDeclaredConstructors()) {
            ctConstructor.instrument(addCodeIfReader(template));
        }
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            ctMethod.instrument(addCodeIfReader(template));
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

    // Returns string with program's total reads
    @Override
    public  String totalText() {
        int totalreads=0;


            for (Class c : Database.dictionary.keySet()) {
                totalreads = totalreads + Database.dictionary.get(c).readerCounter;

            }

        return "Total reads: "+totalreads;
    }

    // Returns string with class total writes
    @Override
    public  String sumText(Class c) {
        return  " reads: "+Database.dictionary.get(c).readerCounter;
    }
}
