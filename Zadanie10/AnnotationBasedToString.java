import java.lang.reflect.*;
import java.util.*;

public class AnnotationBasedToString implements ToStringGenerator {

    @Override
    public String toString(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> annotatedFields = new ArrayList<>();

        // Collect all fields including inherited ones
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ToString.class) &&
                        !Modifier.isPrivate(field.getModifiers())) {
                    annotatedFields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }

        // Sort fields by priority (higher priority first) and order
        annotatedFields.sort((f1, f2) -> {
            ToString a1 = f1.getAnnotation(ToString.class);
            ToString a2 = f2.getAnnotation(ToString.class);

            // Compare by priority (higher priority first)
            int priorityComparison = Integer.compare(a2.priority(), a1.priority());
            if (priorityComparison != 0) {
                return priorityComparison;
            }

            // If same priority, all fields should use the same ordering (use first field's order)
            Order orderToUse = a1.order();
            return orderToUse == Order.ASCENDING ?
                    f1.getName().compareTo(f2.getName()) :
                    f2.getName().compareTo(f1.getName());
        });

        // Build the toString output according to the specified format
        StringBuilder str = new StringBuilder();
        str.append(object.getClass().getSimpleName());

        for (Field field : annotatedFields) {
            field.setAccessible(true);
            str.append(" ").append(field.getName()).append(":");
            try {
                str.append(field.get(object));
            } catch (IllegalAccessException e) {
                str.append("ERROR");
            }
        }

        return str.toString();
    }
}
