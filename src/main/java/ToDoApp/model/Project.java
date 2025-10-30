package ToDoApp.model;

public class Project {
    private int id;
    private String name;
    private String description;
    private int tasksCount;

    public Project(int id, String name, String description, int tasksCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tasksCount = tasksCount;
    }


    public Project(String name, String description) {
        this.name = name;
        this.description = description;
        this.tasksCount = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTasksCount() {
        return tasksCount;
    }

    public void setTasksCount(int tasksCount) {
        this.tasksCount = tasksCount;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tasksCount=" + tasksCount +
                '}';
    }
}
