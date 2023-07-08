package org.tooling.core.base;

import com.google.common.reflect.ClassPath;
import org.apache.commons.lang3.ObjectUtils;
import org.testng.annotations.CustomAttribute;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchingTestId {

    private static final String[] EMPTY_STRING_ARRAY = new String[]{};

    private SearchingTestId() {

    }

    public static List<SeoMonitorQaTestCase> findQaTestCase(Set<String> testCategories, Set<String> testIds)
            throws IOException {
        return findQaTestCase("org.tooling", testCategories, testIds);
    }

    public static List<SeoMonitorQaTestCase> findQaTestCase(String packageName, Set<String> testCategories, Set<String> testIds)
            throws IOException {

        return ClassPath.from(ClassLoader.getSystemClassLoader()).getAllClasses().stream()
                .filter(clazz -> clazz.getPackageName().startsWith(packageName))
                .flatMap(clazz -> getMethodsAnnotatedWith(clazz.load(), Test.class).stream())
                .map(SearchingTestId::automationQaTestCase)
                .collect(Collectors.toList());

    }

    private static List<Method> getMethodsAnnotatedWith(final Class<?> clazz,
                                                        final Class<? extends Annotation> annotation) {


        final List<Method> methods = new ArrayList<>();
        Class<?> klass = clazz;
        while (klass != Object.class) {
            if (klass == null) {
                break;
            }
            for (final Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    methods.add(method);
                }
            }
            klass = klass.getSuperclass();
        }
        return methods;
    }

    private static SeoMonitorQaTestCase automationQaTestCase(Method method) {
        Test testAnnotation = method.getAnnotation(Test.class);

        return getTestCategoryAndId(testAnnotation.attributes()).toBuilder()
                .groups(ObjectUtils.isEmpty(testAnnotation.groups()) ? Collections.emptySet()
                        : new HashSet<String>(Arrays.asList(testAnnotation.groups())))
                .testClass(method.getDeclaringClass()).testMethod(method).build();
    }

    public static SeoMonitorQaTestCase getTestCategoryAndId(CustomAttribute[] customAttributes) {
        if (ObjectUtils.isEmpty(customAttributes)) {
            return SeoMonitorQaTestCase.builder().build();
        }

        Map<String, String[]> attributes =
                Stream.of(customAttributes).filter(attribute -> !ObjectUtils.isEmpty(attribute.values()))
                        .collect(Collectors.toMap(CustomAttribute::name, CustomAttribute::values,
                                (oldValue, newValue) -> newValue));

        String[] values = attributes.getOrDefault("Category", EMPTY_STRING_ARRAY);
        String testCategory = values.length < 1 ? null : values[0];

        values = attributes.getOrDefault("ID", EMPTY_STRING_ARRAY);
        String testId = values.length < 1 ? null : values[0];

        return SeoMonitorQaTestCase.builder().testCategory(testCategory).testId(testId).build();

    }

}
