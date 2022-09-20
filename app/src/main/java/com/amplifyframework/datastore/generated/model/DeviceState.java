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

/** This is an auto generated class representing the DeviceState type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "DeviceStates", authRules = {
  @AuthRule(allow = AuthStrategy.PRIVATE, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "byTrackerPeriod", fields = {"trackerperiodID"})
public final class DeviceState implements Model {
  public static final QueryField ID = field("DeviceState", "id");
  public static final QueryField TIME = field("DeviceState", "Time");
  public static final QueryField STATE = field("DeviceState", "State");
  public static final QueryField TRACKERPERIOD_ID = field("DeviceState", "trackerperiodID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String Time;
  private final @ModelField(targetType="String", isRequired = true) String State;
  private final @ModelField(targetType="ID", isRequired = true) String trackerperiodID;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getTime() {
      return Time;
  }
  
  public String getState() {
      return State;
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
  
  private DeviceState(String id, String Time, String State, String trackerperiodID) {
    this.id = id;
    this.Time = Time;
    this.State = State;
    this.trackerperiodID = trackerperiodID;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      DeviceState deviceState = (DeviceState) obj;
      return ObjectsCompat.equals(getId(), deviceState.getId()) &&
              ObjectsCompat.equals(getTime(), deviceState.getTime()) &&
              ObjectsCompat.equals(getState(), deviceState.getState()) &&
              ObjectsCompat.equals(getTrackerperiodId(), deviceState.getTrackerperiodId()) &&
              ObjectsCompat.equals(getCreatedAt(), deviceState.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), deviceState.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getTime())
      .append(getState())
      .append(getTrackerperiodId())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("DeviceState {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("Time=" + String.valueOf(getTime()) + ", ")
      .append("State=" + String.valueOf(getState()) + ", ")
      .append("trackerperiodID=" + String.valueOf(getTrackerperiodId()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static TimeStep builder() {
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
  public static DeviceState justId(String id) {
    return new DeviceState(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      Time,
      State,
      trackerperiodID);
  }
  public interface TimeStep {
    StateStep time(String time);
  }
  

  public interface StateStep {
    TrackerperiodIdStep state(String state);
  }
  

  public interface TrackerperiodIdStep {
    BuildStep trackerperiodId(String trackerperiodId);
  }
  

  public interface BuildStep {
    DeviceState build();
    BuildStep id(String id);
  }
  

  public static class Builder implements TimeStep, StateStep, TrackerperiodIdStep, BuildStep {
    private String id;
    private String Time;
    private String State;
    private String trackerperiodID;
    @Override
     public DeviceState build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new DeviceState(
          id,
          Time,
          State,
          trackerperiodID);
    }
    
    @Override
     public StateStep time(String time) {
        Objects.requireNonNull(time);
        this.Time = time;
        return this;
    }
    
    @Override
     public TrackerperiodIdStep state(String state) {
        Objects.requireNonNull(state);
        this.State = state;
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
    private CopyOfBuilder(String id, String time, String state, String trackerperiodId) {
      super.id(id);
      super.time(time)
        .state(state)
        .trackerperiodId(trackerperiodId);
    }
    
    @Override
     public CopyOfBuilder time(String time) {
      return (CopyOfBuilder) super.time(time);
    }
    
    @Override
     public CopyOfBuilder state(String state) {
      return (CopyOfBuilder) super.state(state);
    }
    
    @Override
     public CopyOfBuilder trackerperiodId(String trackerperiodId) {
      return (CopyOfBuilder) super.trackerperiodId(trackerperiodId);
    }
  }
  
}
