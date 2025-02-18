package at.ftmahringer.tabsystem.utils;

public enum GlobalEnumVariables {
    DEBUG_MODE(false),
    ;

    boolean value;

    GlobalEnumVariables(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public static void setValue(GlobalEnumVariables variable, boolean value) {
        variable.value = value;
    }
}
