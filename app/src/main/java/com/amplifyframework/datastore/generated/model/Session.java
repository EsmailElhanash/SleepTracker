package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Session type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Sessions", authRules = {
  @AuthRule(allow = AuthStrategy.PRIVATE, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "byTrackerPeriod", fields = {"trackerperiodID"})
public final class Session implements Model {
  public static final QueryField ID = field("Session", "id");
  public static final QueryField START = field("Session", "start");
  public static final QueryField END = field("Session", "end");
  public static final QueryField TRACKERPERIOD_ID = field("Session", "trackerperiodID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String start;
  private final @ModelField(targetType="String", isRequired = true) String end;
  private final @ModelField(targetType="ID", isRequired = true) String trackerperiodID;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getStart() {
      return start;
  }
  
  public String getEnd() {
      return end;
  }
  
  public String getTrackerperiodId() {
      return trackerperiodID;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Session(String id, String start, String end, String trackerperiodID) {
    this.id = id;
    this.start = start;
    this.end = end;
    this.trackerperiodID = trackerperiodID;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Session session = (Session) obj;
      return ObjectsCompat.equals(getId(), session.getId()) &&
              ObjectsCompat.equals(getStart(), session.getStart()) &&
              ObjectsCompat.equals(getEnd(), session.getEnd()) &&
              ObjectsCompat.equals(getTrackerperiodId(), session.getTrackerperiodId()) &&
              ObjectsCompat.equals(getCreatedAt(), session.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), session.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getStart())
      .append(getEnd())
      .append(getTrackerperiodId())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Session {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("start=" + String.valueOf(getStart()) + ", ")
      .append("end=" + String.valueOf(getEnd()) + ", ")
      .append("trackerperiodID=" + String.valueOf(getTrackerperiodId()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static StartStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static Session justId(String id) {
    return new Session(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      start,
      end,
      trackerperiodID);
  }
  public interface StartStep {
    EndStep start(String start);
  }
  

  public interface EndStep {
    TrackerperiodIdStep end(String end);
  }
  

  public interface TrackerperiodIdStep {
    BuildStep trackerperiodId(String trackerperiodId);
  }
  

  public interface BuildStep {
    Session build();
    BuildStep id(String id);
  }
  

  public static class Builder implements StartStep, EndStep, TrackerperiodIdStep, BuildStep {
    private String id;
    private String start;
    private String end;
    private String trackerperiodID;
    @Override
     public Session build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Session(
          id,
          start,
          end,
          trackerperiodID);
    }
    
    @Override
     public EndStep start(String start) {
        Objects.requireNonNull(start);
        this.start = start;
        return this;
    }
    
    @Override
     public TrackerperiodIdStep end(String end) {
        Objects.requireNonNull(end);
        this.end = end;
        return this;
    }
    
    @Override
     public BuildStep trackerperiodId(String trackerperiodId) {
        Objects.requireNonNull(trackerperiodId);
        this.trackerperiodID = trackerperiodId;
        return this;
    }
    
    /** 
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String start, String end, String trackerperiodId) {
      super.id(id);
      super.start(start)
        .end(end)
        .trackerperiodId(trackerperiodId);
    }
    
    @Override
     public CopyOfBuilder start(String start) {
      return (CopyOfBuilder) super.start(start);
    }
    
    @Override
     public CopyOfBuilder end(String end) {
      return (CopyOfBuilder) super.end(end);
    }
    
    @Override
     public CopyOfBuilder trackerperiodId(String trackerperiodId) {
      return (CopyOfBuilder) super.trackerperiodId(trackerperiodId);
    }
  }
  
}
