package com.amplifyframework.datastore.generated.model;


import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the AccelerometerEntry type in your schema. */
public final class AccelerometerEntry {
  private final Integer TotalMovements;
  private final Double CurrentReading;
  public Integer getTotalMovements() {
      return TotalMovements;
  }
  
  public Double getCurrentReading() {
      return CurrentReading;
  }
  
  private AccelerometerEntry(Integer TotalMovements, Double CurrentReading) {
    this.TotalMovements = TotalMovements;
    this.CurrentReading = CurrentReading;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      AccelerometerEntry accelerometerEntry = (AccelerometerEntry) obj;
      return ObjectsCompat.equals(getTotalMovements(), accelerometerEntry.getTotalMovements()) &&
              ObjectsCompat.equals(getCurrentReading(), accelerometerEntry.getCurrentReading());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getTotalMovements())
      .append(getCurrentReading())
      .toString()
      .hashCode();
  }
  
  public static BuildStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(TotalMovements,
      CurrentReading);
  }
  public interface BuildStep {
    AccelerometerEntry build();
    BuildStep totalMovements(Integer totalMovements);
    BuildStep currentReading(Double currentReading);
  }
  

  public static class Builder implements BuildStep {
    private Integer TotalMovements;
    private Double CurrentReading;
    @Override
     public AccelerometerEntry build() {
        
        return new AccelerometerEntry(
          TotalMovements,
          CurrentReading);
    }
    
    @Override
     public BuildStep totalMovements(Integer totalMovements) {
        this.TotalMovements = totalMovements;
        return this;
    }
    
    @Override
     public BuildStep currentReading(Double currentReading) {
        this.CurrentReading = currentReading;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(Integer totalMovements, Double currentReading) {
      super.totalMovements(totalMovements)
        .currentReading(currentReading);
    }
    
    @Override
     public CopyOfBuilder totalMovements(Integer totalMovements) {
      return (CopyOfBuilder) super.totalMovements(totalMovements);
    }
    
    @Override
     public CopyOfBuilder currentReading(Double currentReading) {
      return (CopyOfBuilder) super.currentReading(currentReading);
    }
  }
  
}
