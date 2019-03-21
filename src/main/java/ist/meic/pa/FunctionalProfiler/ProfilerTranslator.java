package ist.meic.pa.FunctionalProfiler;

import javassist.*;

public class ProfilerTranslator implements Translator {
    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        if(classname != Database.class.getName()) {

            CtClass ctClass = pool.get(classname);

            // Adds reader counter function for each read in DeclaredMethods and Constructor of CompileTime Class
            CommandRead cmdRead = new CommandRead();
            cmdRead.execute(ctClass);

            // Adds writer counter function for each write in DeclaredMethods of CompileTime Class
            CommandWrite cmdWrite = new CommandWrite();
            cmdWrite.execute(ctClass);

            // Adds a print function that prints the results of the writer and reader counter for the CompileTime Classes
            CommandPrint cmdPrint = new CommandPrint();
            cmdPrint.execute(ctClass);

        }
    }




}

