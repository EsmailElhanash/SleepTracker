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

/** This is an auto generated class representing the SubPeriod type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "SubPeriods", authRules = {
  @AuthRule(allow = AuthStrategy.PRIVATE, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "byTrackerPeriod", fields = {"trackerperiodID"})
public final class SubPeriod implements Model {
  public static final QueryField ID = field("SubPeriod", "id");
  public static final QueryField RANGE = field("SubPeriod", "Range");
  public static final QueryField MOVEMENT_COUNT = field("SubPeriod", "MovementCount");
  public static final QueryField TRACKERPERIOD_ID = field("SubPeriod", "trackerperiodID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String Range;
  private final @ModelField(targetType="Int", isRequired = true) Integer MovementCount;
  private final @ModelField(targetType="ID", isRequired = true) String trackerperiodID;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getRange() {
      return Range;
  }
  
  public Integer getMovementCount() {
      return MovementCount;
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
  
  private SubPeriod(String id, String Range, Integer MovementCount, String trackerperiodID) {
    this.id = id;
    this.Range = Range;
    this.MovementCount = MovementCount;
    this.trackerperiodID = trackerperiodID;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      SubPeriod subPeriod = (SubPeriod) obj;
      return ObjectsCompat.equals(getId(), subPeriod.getId()) &&
              ObjectsCompat.equals(getRange(), subPeriod.getRange()) &&
              ObjectsCompat.equals(getMovementCount(), subPeriod.getMovementCount()) &&
              ObjectsCompat.equals(getTrackerperiodId(), subPeriod.getTrackerperiodId()) &&
              ObjectsCompat.equals(getCreatedAt(), subPeriod.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), subPeriod.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getRange())
      .append(getMovementCount())
      .append(getTrackerperiodId())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("SubPeriod {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("Range=" + String.valueOf(getRange()) + ", ")
      .append("MovementCount=" + String.valueOf(getMovementCount()) + ", ")
      .append("trackerperiodID=" + String.valueOf(getTrackerperiodId()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static RangeStep builder() {
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
  public static SubPeriod justId(String id) {
    return new SubPeriod(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      Range,
      MovementCount,
      trackerperiodID);
  }
  public interface RangeStep {
    MovementCountStep range(String range);
  }
  

  public interface MovementCountStep {
    TrackerperiodIdStep movementCount(Integer movementCount);
  }
  

  public interface TrackerperiodIdStep {
    BuildStep trackerperiodId(String trackerperiodId);
  }
  

  public interface BuildStep {
    SubPeriod build();
    BuildStep id(String id);
  }
  

  public static class Builder implements RangeStep, MovementCountStep, TrackerperiodIdStep, BuildStep {
    private String id;
    private String Range;
    private Integer MovementCount;
    private String trackerperiodID;
    @Override
     public SubPeriod build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new SubPeriod(
          id,
          Range,
          MovementCount,
          trackerperiodID);
    }
    
    @Override
     public MovementCountStep range(String range) {
        Objects.requireNonNull(range);
        this.Range = range;
        return this;
    }
    
    @Override
     public TrackerperiodIdStep movementCount(Integer movementCount) {
        Objects.requireNonNull(movementCount);
        this.MovementCount = movementCount;
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
    private CopyOfBuilder(String id, String range, Integer movementCount, String trackerperiodId) {
      super.id(id);
      super.range(range)
        .movementCount(movementCount)
        .trackerperiodId(trackerperiodId);
    }
    
    @Override
     public CopyOfBuilder range(String range) {
      return (CopyOfBuilder) super.range(range);
    }
    
    @Override
     public CopyOfBuilder movementCount(Integer movementCount) {
      return (CopyOfBuilder) super.movementCount(movementCount);
    }
    
    @Override
     public CopyOfBuilder trackerperiodId(String trackerperiodId) {
      return (CopyOfBuilder) super.trackerperiodId(trackerperiodId);
    }
  }
  
}
