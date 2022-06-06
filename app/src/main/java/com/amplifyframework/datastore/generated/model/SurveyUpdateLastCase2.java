package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;

import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the SurveyUpdateLastCase2 type in your schema. */
public final class SurveyUpdateLastCase2 {
  private final Temporal.DateTime time;
  private final String took_survey;
  public Temporal.DateTime getTime() {
      return time;
  }
  
  public String getTookSurvey() {
      return took_survey;
  }
  
  private SurveyUpdateLastCase2(Temporal.DateTime time, String took_survey) {
    this.time = time;
    this.took_survey = took_survey;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      SurveyUpdateLastCase2 surveyUpdateLastCase2 = (SurveyUpdateLastCase2) obj;
      return ObjectsCompat.equals(getTime(), surveyUpdateLastCase2.getTime()) &&
              ObjectsCompat.equals(getTookSurvey(), surveyUpdateLastCase2.getTookSurvey());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getTime())
      .append(getTookSurvey())
      .toString()
      .hashCode();
  }
  
  public static TimeStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(time,
      took_survey);
  }
  public interface TimeStep {
    TookSurveyStep time(Temporal.DateTime time);
  }
  

  public interface TookSurveyStep {
    BuildStep tookSurvey(String tookSurvey);
  }
  

  public interface BuildStep {
    SurveyUpdateLastCase2 build();
  }
  

  public static class Builder implements TimeStep, TookSurveyStep, BuildStep {
    private Temporal.DateTime time;
    private String took_survey;
    @Override
     public SurveyUpdateLastCase2 build() {
        
        return new SurveyUpdateLastCase2(
          time,
          took_survey);
    }
    
    @Override
     public TookSurveyStep time(Temporal.DateTime time) {
        Objects.requireNonNull(time);
        this.time = time;
        return this;
    }
    
    @Override
     public BuildStep tookSurvey(String tookSurvey) {
        Objects.requireNonNull(tookSurvey);
        this.took_survey = tookSurvey;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(Temporal.DateTime time, String tookSurvey) {
      super.time(time)
        .tookSurvey(tookSurvey);
    }
    
    @Override
     public CopyOfBuilder time(Temporal.DateTime time) {
      return (CopyOfBuilder) super.time(time);
    }
    
    @Override
     public CopyOfBuilder tookSurvey(String tookSurvey) {
      return (CopyOfBuilder) super.tookSurvey(tookSurvey);
    }
  }
  
}
