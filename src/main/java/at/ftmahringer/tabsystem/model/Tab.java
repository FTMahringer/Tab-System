package at.ftmahringer.tabsystem.model;

import jakarta.persistence.*;

@Entity
public class Tab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tabId;

    @Column(nullable = false)
    private String name;

    private String icon;

    @Column(nullable = false)
    private int priorityOrder;

    @Column(nullable = false)
    private boolean isActive;

    // Default constructor (required by JPA)
    public Tab() {
    }

    // Parameterized constructor
    public Tab(String name, String icon, int priorityOrder, boolean isActive) {
        this.name = name;
        this.icon = icon;
        this.priorityOrder = priorityOrder;
        this.isActive = isActive;
    }

    // Getters and Setters

    public Long getTabId() {
        return tabId;
    }

    public void setTabId(Long tabId) {
        this.tabId = tabId;
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

    public boolean getIsActive() { // Corrected getter name
        return isActive;
    }

    public void setIsActive(boolean isActive) { // Corrected setter name
        this.isActive = isActive;
    }
}
