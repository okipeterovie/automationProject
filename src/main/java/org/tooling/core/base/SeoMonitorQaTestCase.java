package org.tooling.core.base;

import lombok.Builder;
import lombok.Getter;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.Set;

@Builder(toBuilder=true)
@Getter
public class SeoMonitorQaTestCase {
  private String testId;
  private String testCategory;
  private Set<String> groups;
  private ITestResult result;
  
  // additional info that can be used for reflection
  private Class<?> testClass;  
  private Method testMethod;
  
}
