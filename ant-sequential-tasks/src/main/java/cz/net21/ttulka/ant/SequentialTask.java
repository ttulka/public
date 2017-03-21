package cz.net21.ttulka.ant;

import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.MacroDef;
import org.apache.tools.ant.taskdefs.MacroInstance;

/**
 * Created by ttulka, 2017, http://blog.net21.cz
 *
 * Abstract class for the tasks which work with nested sequences.
 */
abstract class SequentialTask extends Task {

    private MacroDef macroDef;
    private Target owningTarget;

    /**
     * Returns the name of the return attribute.
     * @return the name of the attribute
     */
    abstract String getAttributeName();

    /**
     * Sets the parent target for the nested sequence.
     * @param owningTarget the target
     */
    @Override
    public void setOwningTarget(Target owningTarget) {
        this.owningTarget = owningTarget;
    }

    /**
     * Creates the sequence of task to be executed.
     * @return the sequence
     */
    public Object createSequential() {
        macroDef = new MacroDef();
        macroDef.setProject(getProject());

        MacroDef.Attribute attribute = new MacroDef.Attribute();
        attribute.setName(getAttributeName());
        macroDef.addConfiguredAttribute(attribute);

        return macroDef.createSequential();
    }

    /**
     * Runs the sequence with the attribute.
     * @param attrValue the attribute value
     */
    void executeSequential(String attrValue) {
        MacroInstance instance = new MacroInstance();
        instance.setProject(getProject());
        instance.setOwningTarget(owningTarget);
        instance.setMacroDef(macroDef);
        instance.setDynamicAttribute(getAttributeName(), attrValue);
        instance.execute();
    }
}
