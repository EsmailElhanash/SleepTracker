package com.amplifyframework.datastore.generated.model;


import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the SubPeriod type in your schema. */
public final class SubPeriod {
  private final String Range;
  private final Integer MovementCount;
  public String getRange() {
      return Range;
  }
  
  public Integer getMovementCount() {
      return MovementCount;
  }
  
  private SubPeriod(String Range, Integer MovementCount) {
    this.Range = Range;
    this.MovementCount = MovementCount;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      SubPeriod subPeriod = (SubPeriod) obj;
      return ObjectsCompat.equals(getRange(), subPeriod.getRange()) &&
              ObjectsCompat.equals(getMovementCount(), subPeriod.getMovementCount());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getRange())
      .append(getMovementCount())
      .toString()
      .hashCode();
  }
  
  public static RangeStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(Range,
      MovementCount);
  }
  public interface RangeStep {
    MovementCountStep range(String range);
  }
  

  public interface MovementCountStep {
    BuildStep movementCount(Integer movementCount);
  }
  

  public interface BuildStep {
    SubPeriod build();
  }
  

  public static class Builder implements RangeStep, MovementCountStep, BuildStep {
    private String Range;
    private Integer MovementCount;
    @Override
     public SubPeriod build() {
        
        return new SubPeriod(
          Range,
          MovementCount);
    }
    
    @Override
     public MovementCountStep range(String range) {
        Objects.requireNonNull(range);
        this.Range = range;
        return this;
    }
    
    @Override
     public BuildStep movementCount(Integer movementCount) {
        Objects.requireNonNull(movementCount);
        this.MovementCount = movementCount;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String range, Integer movementCount) {
      super.range(range)
        .movementCount(movementCount);
    }
    
    @Override
     public CopyOfBuilder range(String range) {
      return (CopyOfBuilder) super.range(range);
    }
    
    @Override
     public CopyOfBuilder movementCount(Integer movementCount) {
      return (CopyOfBuilder) super.movementCount(movementCount);
    }
  }
  
}
