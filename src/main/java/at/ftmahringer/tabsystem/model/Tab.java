package at.ftmahringer.tabsystem.model;

public class Tab {
    private String name;
    private String icon;
    private int priorityOrder;
    private boolean isActive;
    // Getters and setters

    public Tab(String name, String icon, int priorityOrder, boolean isActive) {
        this.name = name;
        this.icon = icon;
        this.priorityOrder = priorityOrder;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getPriorityOrder() {
        return priorityOrder;
    }

    public void setPriorityOrder(int priorityOrder) {
        this.priorityOrder = priorityOrder;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
