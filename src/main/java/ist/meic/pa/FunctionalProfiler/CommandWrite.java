/*
 * Class extending Command, responsible for adding the write counter function to the
 * CompileTime Class write calls and counting the writes for the class that called the write.
 */
package ist.meic.pa.FunctionalProfiler;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class CommandWrite extends Command {

    // Adds the code that increments the write counter for the class
    public void execute(CtClass ctClass)throws NotFoundException, CannotCompileException {
        final String template =
                "{" +
                        " Database.addWriter($0.getClass());" +
                        " $0.%s = $1;" +
                        "}";
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            ctMethod.instrument(new ExprEditor() {
                public void edit(FieldAccess fa)
                        throws CannotCompileException {
                    if (fa.isWriter()) {
                        String name = fa.getFieldName();
                        fa.replace(String.format(template,
                                name));
                    }
                }
            });
        }
    }

    // Returns string with program's total writes
    @Override
    public String totalText() {
        int totalwrites=0;
        for(Class c: Database.dictionary.keySet()){
            totalwrites = totalwrites + Database.dictionary.get(c).writeCounter;

        }
        return "Total writes: "+totalwrites;
    }

    // Returns string with class total writes
    @Override
    public String sumText(Class c) {
        return  " write: "+Database.dictionary.get(c).writeCounter;
    }
}
