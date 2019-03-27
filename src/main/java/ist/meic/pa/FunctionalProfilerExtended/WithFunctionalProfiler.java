/*
 * Main class of our program, responsible for receiving the classes we want to check the
 * number of reads and writes, creates the necessary tools for the program to work.
 */
package ist.meic.pa.FunctionalProfilerExtended;

import javassist.ClassPool;
import javassist.Loader;
import javassist.Translator;
import org.omg.CORBA.COMM_FAILURE;

import java.text.Annotation;

public class WithFunctionalProfiler {
    public static void main(String[] args) throws Throwable {
        if (args.length < 1) { }
        else {
            String[] realArgs;

            if (args.length==1) {
                realArgs=args[0].split(" ");
            } else {
                realArgs=args;
            }

            Object an = Class.forName(realArgs[0]).getAnnotation(Profiler.class);
            Translator translator;
            ClassPool pool = ClassPool.getDefault();
            Command cmd;

            if (an == null) {
                translator = new ProfilerTranslator();
                cmd = new CommandReadWrite();
                System.out.println("teste2");
            } else {
                String strCmd =((Profiler)an).value();
                if(strCmd.contains(".")){
                    cmd = (Command) Class.forName(strCmd).newInstance();
                }else {
                    cmd = (Command) Class.forName(Command.class.getCanonicalName() + strCmd).newInstance();
                }
                translator = new ProfilerTranslator(cmd);

            }

            cmd.addFields(pool);
            cmd.addMethods(pool);
            pool.importPackage("ist.meic.pa.FunctionalProfilerExtended");
            Loader classLoader = new Loader();
            classLoader.addTranslator(pool, translator);
            String[] restArgs = new String[realArgs.length - 1];
            System.arraycopy(realArgs, 1, restArgs, 0, restArgs.length);
            classLoader.run(realArgs[0], restArgs);
        }
    }
}

