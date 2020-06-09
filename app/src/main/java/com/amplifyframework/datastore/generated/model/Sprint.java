package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.annotations.HasMany;

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

/** This is an auto generated class representing the Sprint type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Sprints")
@Index(name = "byProject", fields = {"projectID"})
public final class Sprint implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField START_DATE = field("startDate");
  public static final QueryField END_DATE = field("endDate");
  public static final QueryField GOAL = field("goal");
  public static final QueryField NAME = field("name");
  public static final QueryField IS_COMPLETED = field("isCompleted");
  public static final QueryField IS_STARTED = field("isStarted");
  public static final QueryField IS_BACKLOG = field("isBacklog");
  public static final QueryField LABEL = field("label");
  public static final QueryField PROJECT = field("projectID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="AWSDate") Temporal.Date startDate;
  private final @ModelField(targetType="AWSDate") Temporal.Date endDate;
  private final @ModelField(targetType="String", isRequired = true) String goal;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="Boolean") Boolean isCompleted;
  private final @ModelField(targetType="Boolean") Boolean isStarted;
  private final @ModelField(targetType="Boolean", isRequired = true) Boolean isBacklog;
  private final @ModelField(targetType="String") String label;
  private final @ModelField(targetType="Project", isRequired = true) @BelongsTo(targetName = "projectID", type = Project.class) Project project;
  private final @ModelField(targetType="Task") @HasMany(associatedWith = "sprint", type = Task.class) List<Task> tasks = null;
  public String getId() {
      return id;
  }
  
  public Temporal.Date getStartDate() {
      return startDate;
  }
  
  public Temporal.Date getEndDate() {
      return endDate;
  }
  
  public String getGoal() {
      return goal;
  }
  
  public String getName() {
      return name;
  }
  
  public Boolean getIsCompleted() {
      return isCompleted;
  }
  
  public Boolean getIsStarted() {
      return isStarted;
  }
  
  public Boolean getIsBacklog() {
      return isBacklog;
  }
  
  public String getLabel() {
      return label;
  }
  
  public Project getProject() {
      return project;
  }
  
  public List<Task> getTasks() {
      return tasks;
  }
  
  private Sprint(String id, Temporal.Date startDate, Temporal.Date endDate, String goal, String name, Boolean isCompleted, Boolean isStarted, Boolean isBacklog, String label, Project project) {
    this.id = id;
    this.startDate = startDate;
    this.endDate = endDate;
    this.goal = goal;
    this.name = name;
    this.isCompleted = isCompleted;
    this.isStarted = isStarted;
    this.isBacklog = isBacklog;
    this.label = label;
    this.project = project;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Sprint sprint = (Sprint) obj;
      return ObjectsCompat.equals(getId(), sprint.getId()) &&
              ObjectsCompat.equals(getStartDate(), sprint.getStartDate()) &&
              ObjectsCompat.equals(getEndDate(), sprint.getEndDate()) &&
              ObjectsCompat.equals(getGoal(), sprint.getGoal()) &&
              ObjectsCompat.equals(getName(), sprint.getName()) &&
              ObjectsCompat.equals(getIsCompleted(), sprint.getIsCompleted()) &&
              ObjectsCompat.equals(getIsStarted(), sprint.getIsStarted()) &&
              ObjectsCompat.equals(getIsBacklog(), sprint.getIsBacklog()) &&
              ObjectsCompat.equals(getLabel(), sprint.getLabel()) &&
              ObjectsCompat.equals(getProject(), sprint.getProject());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getStartDate())
      .append(getEndDate())
      .append(getGoal())
      .append(getName())
      .append(getIsCompleted())
      .append(getIsStarted())
      .append(getIsBacklog())
      .append(getLabel())
      .append(getProject())
      .toString()
      .hashCode();
  }
  
//  @Override
//   public String toString() {
//    return new StringBuilder()
//      .append("Sprint {")
//      .append("id=" + String.valueOf(getId()))
//      .append("startDate=" + String.valueOf(getStartDate()))
//      .append("endDate=" + String.valueOf(getEndDate()))
//      .append("goal=" + String.valueOf(getGoal()))
//      .append("name=" + String.valueOf(getName()))
//      .append("isCompleted=" + String.valueOf(getIsCompleted()))
//      .append("isStarted=" + String.valueOf(getIsStarted()))
//      .append("isBacklog=" + String.valueOf(getIsBacklog()))
//      .append("label=" + String.valueOf(getLabel()))
//      .append("project=" + String.valueOf(getProject()))
//      .append("}")
//      .toString();
//  }


    @Override
    public String toString() {
        return name;
    }

    public static GoalStep builder() {
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
  public static Sprint justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Sprint(
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
      startDate,
      endDate,
      goal,
      name,
      isCompleted,
      isStarted,
      isBacklog,
      label,
      project);
  }
  public interface GoalStep {
    NameStep goal(String goal);
  }
  

  public interface NameStep {
    IsBacklogStep name(String name);
  }
  

  public interface IsBacklogStep {
    ProjectStep isBacklog(Boolean isBacklog);
  }
  

  public interface ProjectStep {
    BuildStep project(Project project);
  }
  

  public interface BuildStep {
    Sprint build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep startDate(Temporal.Date startDate);
    BuildStep endDate(Temporal.Date endDate);
    BuildStep isCompleted(Boolean isCompleted);
    BuildStep isStarted(Boolean isStarted);
    BuildStep label(String label);
  }
  

  public static class Builder implements GoalStep, NameStep, IsBacklogStep, ProjectStep, BuildStep {
    private String id;
    private String goal;
    private String name;
    private Boolean isBacklog;
    private Project project;
    private Temporal.Date startDate;
    private Temporal.Date endDate;
    private Boolean isCompleted;
    private Boolean isStarted;
    private String label;
    @Override
     public Sprint build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Sprint(
          id,
          startDate,
          endDate,
          goal,
          name,
          isCompleted,
          isStarted,
          isBacklog,
          label,
          project);
    }
    
    @Override
     public NameStep goal(String goal) {
        Objects.requireNonNull(goal);
        this.goal = goal;
        return this;
    }
    
    @Override
     public IsBacklogStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public ProjectStep isBacklog(Boolean isBacklog) {
        Objects.requireNonNull(isBacklog);
        this.isBacklog = isBacklog;
        return this;
    }
    
    @Override
     public BuildStep project(Project project) {
        Objects.requireNonNull(project);
        this.project = project;
        return this;
    }
    
    @Override
     public BuildStep startDate(Temporal.Date startDate) {
        this.startDate = startDate;
        return this;
    }
    
    @Override
     public BuildStep endDate(Temporal.Date endDate) {
        this.endDate = endDate;
        return this;
    }
    
    @Override
     public BuildStep isCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
        return this;
    }
    
    @Override
     public BuildStep isStarted(Boolean isStarted) {
        this.isStarted = isStarted;
        return this;
    }
    
    @Override
     public BuildStep label(String label) {
        this.label = label;
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
    private CopyOfBuilder(String id, Temporal.Date startDate, Temporal.Date endDate, String goal, String name, Boolean isCompleted, Boolean isStarted, Boolean isBacklog, String label, Project project) {
      super.id(id);
      super.goal(goal)
        .name(name)
        .isBacklog(isBacklog)
        .project(project)
        .startDate(startDate)
        .endDate(endDate)
        .isCompleted(isCompleted)
        .isStarted(isStarted)
        .label(label);
    }
    
    @Override
     public CopyOfBuilder goal(String goal) {
      return (CopyOfBuilder) super.goal(goal);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder isBacklog(Boolean isBacklog) {
      return (CopyOfBuilder) super.isBacklog(isBacklog);
    }
    
    @Override
     public CopyOfBuilder project(Project project) {
      return (CopyOfBuilder) super.project(project);
    }
    
    @Override
     public CopyOfBuilder startDate(Temporal.Date startDate) {
      return (CopyOfBuilder) super.startDate(startDate);
    }
    
    @Override
     public CopyOfBuilder endDate(Temporal.Date endDate) {
      return (CopyOfBuilder) super.endDate(endDate);
    }
    
    @Override
     public CopyOfBuilder isCompleted(Boolean isCompleted) {
      return (CopyOfBuilder) super.isCompleted(isCompleted);
    }
    
    @Override
     public CopyOfBuilder isStarted(Boolean isStarted) {
      return (CopyOfBuilder) super.isStarted(isStarted);
    }
    
    @Override
     public CopyOfBuilder label(String label) {
      return (CopyOfBuilder) super.label(label);
    }
  }
  
}
