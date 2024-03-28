package edu.umkc.cs461.hw2.rules;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.umkc.cs461.hw2.model.Assignment;
import edu.umkc.cs461.hw2.model.Room;
import edu.umkc.cs461.hw2.model.Assignment;

public class RuleExecutor{
    public static double executeJSRule(final String rule, final Assignment a, final Assignment b) throws ScriptException{
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        Bindings bindings = engine.createBindings();
        bindings.put("Math", Math.class);
        bindings.put("Assignment", Assignment.class);
        bindings.put("Room", Room.class);
        bindings.put("a", a);
        if(null != b) {
            bindings.put("b", b);
        }

        return (double) engine.eval(rule, bindings);
    }
}
