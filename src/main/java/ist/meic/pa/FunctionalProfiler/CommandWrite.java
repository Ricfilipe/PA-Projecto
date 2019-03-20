package ist.meic.pa.FunctionalProfiler;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class CommandWrite extends Command {

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

    @Override
    public String totalText() {
        int totalwrites=0;
        for(Class c: Database.dictionary.keySet()){
            totalwrites = totalwrites + Database.dictionary.get(c).writeCounter;

        }
        return "Total writes: "+totalwrites;
    }

    @Override
    public String sumText(Class c) {
        return  " write: "+Database.dictionary.get(c).writeCounter;
    }
}
