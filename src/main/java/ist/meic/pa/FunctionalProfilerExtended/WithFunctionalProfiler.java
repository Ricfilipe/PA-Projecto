/*
 * Main class of our program, responsible for receiving the classes we want to check the
 * number of reads and writes, creates the necessary tools for the program to work.
 */
package ist.meic.pa.FunctionalProfilerExtended;

import javassist.*;

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
            } else {
                String strCmd =((Profiler)an).value();
                if(strCmd.contains(".")){
                    cmd = (Command) Class.forName(strCmd).newInstance();
                }else {
                    cmd = (Command) Class.forName(Command.class.getName() + strCmd).newInstance();
                }
                translator = new ProfilerTranslator(cmd);
            }
            CtClass database = ClassPool.getDefault().get(Database.class.getName());
            CtClass entry = ClassPool.getDefault().get(Entry.class.getName());
            for(CtField field :cmd.addFields(entry)){
                entry.addField(field);
            }
            for(CtMethod method :cmd.addMethods(database, Entry.class.getName())){
                database.addMethod(method);
            }
            pool.importPackage("ist.meic.pa.FunctionalProfilerExtended");
            Loader classLoader = new Loader();
            classLoader.addTranslator(pool, translator);
            String[] restArgs = new String[realArgs.length - 1];
            System.arraycopy(realArgs, 1, restArgs, 0, restArgs.length);
            classLoader.run(realArgs[0], restArgs);
        }
    }
}

