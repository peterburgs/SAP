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

/** This is an auto generated class representing the ProjectParticipant type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "ProjectParticipants")
@Index(name = "byProject", fields = {"projectID","memberID"})
@Index(name = "byMember", fields = {"memberID","projectID"})
public final class ProjectParticipant implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField ROLE = field("role");
  public static final QueryField PROJECT = field("projectID");
  public static final QueryField MEMBER = field("memberID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Role", isRequired = true) Role role;
  private final @ModelField(targetType="Project", isRequired = true) @BelongsTo(targetName = "projectID", type = Project.class) Project project;
  private final @ModelField(targetType="User", isRequired = true) @BelongsTo(targetName = "memberID", type = User.class) User member;
  public String getId() {
      return id;
  }
  
  public Role getRole() {
      return role;
  }
  
  public Project getProject() {
      return project;
  }
  
  public User getMember() {
      return member;
  }
  
  private ProjectParticipant(String id, Role role, Project project, User member) {
    this.id = id;
    this.role = role;
    this.project = project;
    this.member = member;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      ProjectParticipant projectParticipant = (ProjectParticipant) obj;
      return ObjectsCompat.equals(getId(), projectParticipant.getId()) &&
              ObjectsCompat.equals(getRole(), projectParticipant.getRole()) &&
              ObjectsCompat.equals(getProject(), projectParticipant.getProject()) &&
              ObjectsCompat.equals(getMember(), projectParticipant.getMember());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getRole())
      .append(getProject())
      .append(getMember())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("ProjectParticipant {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("role=" + String.valueOf(getRole()) + ", ")
      .append("project=" + String.valueOf(getProject()) + ", ")
      .append("member=" + String.valueOf(getMember()))
      .append("}")
      .toString();
  }
  
  public static RoleStep builder() {
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
  public static ProjectParticipant justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new ProjectParticipant(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      role,
      project,
      member);
  }
  public interface RoleStep {
    ProjectStep role(Role role);
  }
  

  public interface ProjectStep {
    MemberStep project(Project project);
  }
  

  public interface MemberStep {
    BuildStep member(User member);
  }
  

  public interface BuildStep {
    ProjectParticipant build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements RoleStep, ProjectStep, MemberStep, BuildStep {
    private String id;
    private Role role;
    private Project project;
    private User member;
    @Override
     public ProjectParticipant build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new ProjectParticipant(
          id,
          role,
          project,
          member);
    }
    
    @Override
     public ProjectStep role(Role role) {
        Objects.requireNonNull(role);
        this.role = role;
        return this;
    }
    
    @Override
     public MemberStep project(Project project) {
        Objects.requireNonNull(project);
        this.project = project;
        return this;
    }
    
    @Override
     public BuildStep member(User member) {
        Objects.requireNonNull(member);
        this.member = member;
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
    private CopyOfBuilder(String id, Role role, Project project, User member) {
      super.id(id);
      super.role(role)
        .project(project)
        .member(member);
    }
    
    @Override
     public CopyOfBuilder role(Role role) {
      return (CopyOfBuilder) super.role(role);
    }
    
    @Override
     public CopyOfBuilder project(Project project) {
      return (CopyOfBuilder) super.project(project);
    }
    
    @Override
     public CopyOfBuilder member(User member) {
      return (CopyOfBuilder) super.member(member);
    }
  }
  
}
