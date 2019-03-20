package ist.meic.pa.FunctionalProfiler;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import javax.xml.crypto.Data;

public class ProfilerTranslator implements Translator {
    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        if(classname != Database.class.getName()) {

            CtClass ctClass = pool.get(classname);
            makeCountable(ctClass);
            makePrint(ctClass);
        }
    }

    void makeCountable(CtClass ctClass)throws NotFoundException, CannotCompileException {
        makeReadCounter(ctClass);
        makeWriteCounter(ctClass);
    }

    void makeReadCounter(CtClass ctClass) throws NotFoundException, CannotCompileException {
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

    void makeWriteCounter(CtClass ctClass) throws NotFoundException, CannotCompileException {
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

    void makePrint(CtClass ctClass){
        try{
            CtMethod ctMethod = ctClass.getDeclaredMethods("main")[0];
            ctMethod.insertAfter("System.out.println(Database.toText());");
        }catch (Exception e){
            //ignore
        }
    }

}

