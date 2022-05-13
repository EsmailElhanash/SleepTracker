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
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class SubPeriod implements Model {
  public static final QueryField ID = field("SubPeriod", "id");
  public static final QueryField RANGE = field("SubPeriod", "Range");
  public static final QueryField MOVEMENT_COUNT = field("SubPeriod", "MovementCount");
  public static final QueryField TRACKER_PERIOD_SUB_PERIODS_ID = field("SubPeriod", "trackerPeriodSubPeriodsId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String Range;
  private final @ModelField(targetType="Int") Integer MovementCount;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  private final @ModelField(targetType="ID") String trackerPeriodSubPeriodsId;
  public String getId() {
      return id;
  }
  
  public String getRange() {
      return Range;
  }
  
  public Integer getMovementCount() {
      return MovementCount;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  public String getTrackerPeriodSubPeriodsId() {
      return trackerPeriodSubPeriodsId;
  }
  
  private SubPeriod(String id, String Range, Integer MovementCount, String trackerPeriodSubPeriodsId) {
    this.id = id;
    this.Range = Range;
    this.MovementCount = MovementCount;
    this.trackerPeriodSubPeriodsId = trackerPeriodSubPeriodsId;
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
              ObjectsCompat.equals(getCreatedAt(), subPeriod.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), subPeriod.getUpdatedAt()) &&
              ObjectsCompat.equals(getTrackerPeriodSubPeriodsId(), subPeriod.getTrackerPeriodSubPeriodsId());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getRange())
      .append(getMovementCount())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .append(getTrackerPeriodSubPeriodsId())
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
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()) + ", ")
      .append("trackerPeriodSubPeriodsId=" + String.valueOf(getTrackerPeriodSubPeriodsId()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
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
      trackerPeriodSubPeriodsId);
  }
  public interface BuildStep {
    SubPeriod build();
    BuildStep id(String id);
    BuildStep range(String range);
    BuildStep movementCount(Integer movementCount);
    BuildStep trackerPeriodSubPeriodsId(String trackerPeriodSubPeriodsId);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String Range;
    private Integer MovementCount;
    private String trackerPeriodSubPeriodsId;
    @Override
     public SubPeriod build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new SubPeriod(
          id,
          Range,
          MovementCount,
          trackerPeriodSubPeriodsId);
    }
    
    @Override
     public BuildStep range(String range) {
        this.Range = range;
        return this;
    }
    
    @Override
     public BuildStep movementCount(Integer movementCount) {
        this.MovementCount = movementCount;
        return this;
    }
    
    @Override
     public BuildStep trackerPeriodSubPeriodsId(String trackerPeriodSubPeriodsId) {
        this.trackerPeriodSubPeriodsId = trackerPeriodSubPeriodsId;
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
    private CopyOfBuilder(String id, String range, Integer movementCount, String trackerPeriodSubPeriodsId) {
      super.id(id);
      super.range(range)
        .movementCount(movementCount)
        .trackerPeriodSubPeriodsId(trackerPeriodSubPeriodsId);
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
     public CopyOfBuilder trackerPeriodSubPeriodsId(String trackerPeriodSubPeriodsId) {
      return (CopyOfBuilder) super.trackerPeriodSubPeriodsId(trackerPeriodSubPeriodsId);
    }
  }
  
}
