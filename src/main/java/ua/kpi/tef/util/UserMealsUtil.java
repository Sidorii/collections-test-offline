package ua.kpi.tef.util;

import ua.kpi.tef.model.UserMeal;
import ua.kpi.tef.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GKislin
 * 31.05.2015.
 */
public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 0, 0), "Ужин", 510)
        );
        List<UserMealWithExceed> result =
                getFilteredWithExceeded(mealList, LocalTime.of(0, 0), LocalTime.of(13, 0), 2000);

        result.forEach(System.out::println);
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime,
                                                                   LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> daysToCaloriesMap = mealList.stream()
                .filter((el) -> TimeUtil.isBetween(el.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.groupingBy((el) -> el.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)));

        Set<LocalDate> exceedDays = daysToCaloriesMap.entrySet()
                .stream()
                .filter((entry) -> entry.getValue() < caloriesPerDay)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        return mealList.stream()
                .filter((el) -> !exceedDays.contains(el.getDateTime().toLocalDate()))
                .map((el) -> createUserMealWithExceedFromUserMeal(el, caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static UserMealWithExceed createUserMealWithExceedFromUserMeal(UserMeal userMeal, int maxCalories) {
        boolean exceed = userMeal.getCalories() > maxCalories;

        return new UserMealWithExceed(userMeal.getDateTime(),
                userMeal.getDescription(),
                userMeal.getCalories(),
                exceed);
    }
}
