package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
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

/** This is an auto generated class representing the Comment type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Comments")
@Index(name = "byTask", fields = {"taskID"})
public final class Comment implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField CONTENT = field("content");
  public static final QueryField CREATED_AT = field("createdAt");
  public static final QueryField AUTHOR = field("authorID");
  public static final QueryField TASK = field("taskID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String content;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime createdAt;
  private final @ModelField(targetType="User", isRequired = true) @BelongsTo(targetName = "authorID", type = User.class) User author;
  private final @ModelField(targetType="Task", isRequired = true) @BelongsTo(targetName = "taskID", type = Task.class) Task task;
  public String getId() {
      return id;
  }
  
  public String getContent() {
      return content;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public User getAuthor() {
      return author;
  }
  
  public Task getTask() {
      return task;
  }
  
  private Comment(String id, String content, Temporal.DateTime createdAt, User author, Task task) {
    this.id = id;
    this.content = content;
    this.createdAt = createdAt;
    this.author = author;
    this.task = task;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Comment comment = (Comment) obj;
      return ObjectsCompat.equals(getId(), comment.getId()) &&
              ObjectsCompat.equals(getContent(), comment.getContent()) &&
              ObjectsCompat.equals(getCreatedAt(), comment.getCreatedAt()) &&
              ObjectsCompat.equals(getAuthor(), comment.getAuthor()) &&
              ObjectsCompat.equals(getTask(), comment.getTask());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getContent())
      .append(getCreatedAt())
      .append(getAuthor())
      .append(getTask())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Comment {")
      .append("id=" + String.valueOf(getId()))
      .append("content=" + String.valueOf(getContent()))
      .append("createdAt=" + String.valueOf(getCreatedAt()))
      .append("author=" + String.valueOf(getAuthor()))
      .append("task=" + String.valueOf(getTask()))
      .append("}")
      .toString();
  }
  
  public static ContentStep builder() {
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
  public static Comment justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Comment(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      content,
      createdAt,
      author,
      task);
  }
  public interface ContentStep {
    AuthorStep content(String content);
  }
  

  public interface AuthorStep {
    TaskStep author(User author);
  }
  

  public interface TaskStep {
    BuildStep task(Task task);
  }
  

  public interface BuildStep {
    Comment build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep createdAt(Temporal.DateTime createdAt);
  }
  

  public static class Builder implements ContentStep, AuthorStep, TaskStep, BuildStep {
    private String id;
    private String content;
    private User author;
    private Task task;
    private Temporal.DateTime createdAt;
    @Override
     public Comment build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Comment(
          id,
          content,
          createdAt,
          author,
          task);
    }
    
    @Override
     public AuthorStep content(String content) {
        Objects.requireNonNull(content);
        this.content = content;
        return this;
    }
    
    @Override
     public TaskStep author(User author) {
        Objects.requireNonNull(author);
        this.author = author;
        return this;
    }
    
    @Override
     public BuildStep task(Task task) {
        Objects.requireNonNull(task);
        this.task = task;
        return this;
    }
    
    @Override
     public BuildStep createdAt(Temporal.DateTime createdAt) {
        this.createdAt = createdAt;
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
    private CopyOfBuilder(String id, String content, Temporal.DateTime createdAt, User author, Task task) {
      super.id(id);
      super.content(content)
        .author(author)
        .task(task)
        .createdAt(createdAt);
    }
    
    @Override
     public CopyOfBuilder content(String content) {
      return (CopyOfBuilder) super.content(content);
    }
    
    @Override
     public CopyOfBuilder author(User author) {
      return (CopyOfBuilder) super.author(author);
    }
    
    @Override
     public CopyOfBuilder task(Task task) {
      return (CopyOfBuilder) super.task(task);
    }
    
    @Override
     public CopyOfBuilder createdAt(Temporal.DateTime createdAt) {
      return (CopyOfBuilder) super.createdAt(createdAt);
    }
  }
  
}
