/*
 * Class responsible for changing the CompileTime Classes
 */
package ist.meic.pa.FunctionalProfiler;

import javassist.*;

public class ProfilerTranslator implements Translator {
    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
                          CtClass ctClass = pool.get(classname);
                try {
                    ctClass.getAnnotation(NotIntersect.class);
                    return;
                }catch (Exception e){}
                CommandRead cmdRead = new CommandRead();
                CommandWrite cmdWrite = new CommandWrite();
                CommandPrint cmdPrint = new CommandPrint();

                // Adds reader counter function for each read in DeclaredMethods and Constructor of CompileTime Class
                cmdRead.execute(ctClass);

                // Adds writer counter function for each write in DeclaredMethods of CompileTime Class
                cmdWrite.execute(ctClass);

                // Adds a print function that prints the results of the writer and reader counter for the CompileTime Classes
                cmdPrint.execute(ctClass);

        }
    }


