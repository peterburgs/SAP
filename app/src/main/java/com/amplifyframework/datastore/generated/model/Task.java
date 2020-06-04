package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Task type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Tasks")
@Index(name = "byProject", fields = {"projectID"})
@Index(name = "byAssignee", fields = {"assigneeID"})
@Index(name = "bySprint", fields = {"sprintID"})
public final class Task implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField NAME = field("name");
  public static final QueryField SUMMARY = field("summary");
  public static final QueryField LABEL = field("label");
  public static final QueryField DESCRIPTION = field("description");
  public static final QueryField PRIORITY = field("priority");
  public static final QueryField STATUS = field("status");
  public static final QueryField PROJECT = field("projectID");
  public static final QueryField ASSIGNEE = field("assigneeID");
  public static final QueryField SPRINT = field("sprintID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String", isRequired = true) String summary;
  private final @ModelField(targetType="String") String label;
  private final @ModelField(targetType="String") String description;
  private final @ModelField(targetType="Int") Integer priority;
  private final @ModelField(targetType="TaskStatus") TaskStatus status;
  private final @ModelField(targetType="Project", isRequired = true) @BelongsTo(targetName = "projectID", type = Project.class) Project project;
  private final @ModelField(targetType="User", isRequired = true) @BelongsTo(targetName = "assigneeID", type = User.class) User assignee;
  private final @ModelField(targetType="Sprint", isRequired = true) @BelongsTo(targetName = "sprintID", type = Sprint.class) Sprint sprint;
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getSummary() {
      return summary;
  }
  
  public String getLabel() {
      return label;
  }
  
  public String getDescription() {
      return description;
  }
  
  public Integer getPriority() {
      return priority;
  }
  
  public TaskStatus getStatus() {
      return status;
  }
  
  public Project getProject() {
      return project;
  }
  
  public User getAssignee() {
      return assignee;
  }
  
  public Sprint getSprint() {
      return sprint;
  }
  
  private Task(String id, String name, String summary, String label, String description, Integer priority, TaskStatus status, Project project, User assignee, Sprint sprint) {
    this.id = id;
    this.name = name;
    this.summary = summary;
    this.label = label;
    this.description = description;
    this.priority = priority;
    this.status = status;
    this.project = project;
    this.assignee = assignee;
    this.sprint = sprint;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Task task = (Task) obj;
      return ObjectsCompat.equals(getId(), task.getId()) &&
              ObjectsCompat.equals(getName(), task.getName()) &&
              ObjectsCompat.equals(getSummary(), task.getSummary()) &&
              ObjectsCompat.equals(getLabel(), task.getLabel()) &&
              ObjectsCompat.equals(getDescription(), task.getDescription()) &&
              ObjectsCompat.equals(getPriority(), task.getPriority()) &&
              ObjectsCompat.equals(getStatus(), task.getStatus()) &&
              ObjectsCompat.equals(getProject(), task.getProject()) &&
              ObjectsCompat.equals(getAssignee(), task.getAssignee()) &&
              ObjectsCompat.equals(getSprint(), task.getSprint());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getSummary())
      .append(getLabel())
      .append(getDescription())
      .append(getPriority())
      .append(getStatus())
      .append(getProject())
      .append(getAssignee())
      .append(getSprint())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Task {")
      .append("id=" + String.valueOf(getId()))
      .append("name=" + String.valueOf(getName()))
      .append("summary=" + String.valueOf(getSummary()))
      .append("label=" + String.valueOf(getLabel()))
      .append("description=" + String.valueOf(getDescription()))
      .append("priority=" + String.valueOf(getPriority()))
      .append("status=" + String.valueOf(getStatus()))
      .append("project=" + String.valueOf(getProject()))
      .append("assignee=" + String.valueOf(getAssignee()))
      .append("sprint=" + String.valueOf(getSprint()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static Task justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Task(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      summary,
      label,
      description,
      priority,
      status,
      project,
      assignee,
      sprint);
  }
  public interface NameStep {
    SummaryStep name(String name);
  }
  

  public interface SummaryStep {
    ProjectStep summary(String summary);
  }
  

  public interface ProjectStep {
    AssigneeStep project(Project project);
  }
  

  public interface AssigneeStep {
    SprintStep assignee(User assignee);
  }
  

  public interface SprintStep {
    BuildStep sprint(Sprint sprint);
  }
  

  public interface BuildStep {
    Task build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep label(String label);
    BuildStep description(String description);
    BuildStep priority(Integer priority);
    BuildStep status(TaskStatus status);
  }
  

  public static class Builder implements NameStep, SummaryStep, ProjectStep, AssigneeStep, SprintStep, BuildStep {
    private String id;
    private String name;
    private String summary;
    private Project project;
    private User assignee;
    private Sprint sprint;
    private String label;
    private String description;
    private Integer priority;
    private TaskStatus status;
    @Override
     public Task build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Task(
          id,
          name,
          summary,
          label,
          description,
          priority,
          status,
          project,
          assignee,
          sprint);
    }
    
    @Override
     public SummaryStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public ProjectStep summary(String summary) {
        Objects.requireNonNull(summary);
        this.summary = summary;
        return this;
    }
    
    @Override
     public AssigneeStep project(Project project) {
        Objects.requireNonNull(project);
        this.project = project;
        return this;
    }
    
    @Override
     public SprintStep assignee(User assignee) {
        Objects.requireNonNull(assignee);
        this.assignee = assignee;
        return this;
    }
    
    @Override
     public BuildStep sprint(Sprint sprint) {
        Objects.requireNonNull(sprint);
        this.sprint = sprint;
        return this;
    }
    
    @Override
     public BuildStep label(String label) {
        this.label = label;
        return this;
    }
    
    @Override
     public BuildStep description(String description) {
        this.description = description;
        return this;
    }
    
    @Override
     public BuildStep priority(Integer priority) {
        this.priority = priority;
        return this;
    }
    
    @Override
     public BuildStep status(TaskStatus status) {
        this.status = status;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String name, String summary, String label, String description, Integer priority, TaskStatus status, Project project, User assignee, Sprint sprint) {
      super.id(id);
      super.name(name)
        .summary(summary)
        .project(project)
        .assignee(assignee)
        .sprint(sprint)
        .label(label)
        .description(description)
        .priority(priority)
        .status(status);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder summary(String summary) {
      return (CopyOfBuilder) super.summary(summary);
    }
    
    @Override
     public CopyOfBuilder project(Project project) {
      return (CopyOfBuilder) super.project(project);
    }
    
    @Override
     public CopyOfBuilder assignee(User assignee) {
      return (CopyOfBuilder) super.assignee(assignee);
    }
    
    @Override
     public CopyOfBuilder sprint(Sprint sprint) {
      return (CopyOfBuilder) super.sprint(sprint);
    }
    
    @Override
     public CopyOfBuilder label(String label) {
      return (CopyOfBuilder) super.label(label);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
    
    @Override
     public CopyOfBuilder priority(Integer priority) {
      return (CopyOfBuilder) super.priority(priority);
    }
    
    @Override
     public CopyOfBuilder status(TaskStatus status) {
      return (CopyOfBuilder) super.status(status);
    }
  }
  
}
