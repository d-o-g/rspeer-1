package org.rspeer.injector.hook;

public final class MethodHook extends Hook {

    private final String owner, internalName, desc, expectedDesc; //expected desc = desc but without dummy param
    private final int predicate; //valid number to pass if the method has dummy parameter
    private final boolean isStatic, isInterface;

    public MethodHook(String definedName, String owner, String internalName, String desc, String expectedDesc, int predicate, boolean isStatic, boolean isInterface) {
        super(definedName);
        this.owner = owner;
        this.internalName = internalName;
        this.desc = desc;
        this.expectedDesc = expectedDesc;
        this.predicate = predicate;
        this.isStatic = isStatic;
        this.isInterface = isInterface;
    }

    public String getOwner() {
        return owner;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getDesc() {
        return desc;
    }

    public String getExpectedDesc() {
        return expectedDesc;
    }

    public int getPredicate() {
        return predicate;
    }

    public boolean hasPredicate() {
        return predicate != Integer.MAX_VALUE;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isLocal() {
        return !isStatic;
    }

    public boolean isInterface() {
        return isInterface;
    }
}
