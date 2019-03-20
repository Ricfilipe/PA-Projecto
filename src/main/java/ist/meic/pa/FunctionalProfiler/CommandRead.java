package ist.meic.pa.FunctionalProfiler;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import javax.xml.crypto.Data;

public class CommandRead  extends Command{
    public void  execute(CtClass ctClass) throws NotFoundException, CannotCompileException {
            final String template =
                    "{" +
                            " Database.addReader($0.getClass());" +
                            " $_=$0.%s;" +
                            "}";
            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                ctMethod.instrument(new ExprEditor() {
                    public void edit(FieldAccess fa)
                            throws CannotCompileException {
                        if (fa.isReader()) {
                            String name = fa.getFieldName();
                            fa.replace(String.format(template,
                                    name));
                        }
                    }
                });
            }
        }

    @Override
    public  String totalText() {
        int totalreads=0;
        for(Class c: Database.dictionary.keySet()){
            totalreads = totalreads + Database.dictionary.get(c).readerCounter;

        }
        return "Total reads: "+totalreads;
    }

    @Override
    public  String sumText(Class c) {
        return  " reads: "+Database.dictionary.get(c).readerCounter;
    }
}
