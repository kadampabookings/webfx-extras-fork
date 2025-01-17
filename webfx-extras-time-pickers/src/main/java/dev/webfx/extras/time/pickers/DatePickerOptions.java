package dev.webfx.extras.time.pickers;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public final class DatePickerOptions {

    private boolean applyBorderStyle = true;
    private boolean applyMaxSize = true;
    private YearMonth initialDisplayedYearMonth;
    private boolean multipleSelectionAllowed = false;
    private boolean pastDatesSelectionAllowed = true;
    private boolean sortSelectedDates = false;
    private Function<LocalDate, Boolean> isDateSelectableFunction = null;

    public boolean isApplyBorderStyle() {
        return applyBorderStyle;
    }

    public DatePickerOptions setApplyBorderStyle(boolean applyBorderStyle) {
        this.applyBorderStyle = applyBorderStyle;
        return this;
    }

    public boolean isApplyMaxSize() {
        return applyMaxSize;
    }

    public DatePickerOptions setApplyMaxSize(boolean applyMaxSize) {
        this.applyMaxSize = applyMaxSize;
        return this;
    }

    public YearMonth getInitialDisplayedYearMonth() {
        return initialDisplayedYearMonth;
    }

    public DatePickerOptions setInitialDisplayedYearMonth(YearMonth initialDisplayedYearMonth) {
        this.initialDisplayedYearMonth = initialDisplayedYearMonth;
        return this;
    }

    public boolean isMultipleSelectionAllowed() {
        return multipleSelectionAllowed;
    }

    public DatePickerOptions setMultipleSelectionAllowed(boolean multipleSelectionAllowed) {
        this.multipleSelectionAllowed = multipleSelectionAllowed;
        return this;
    }

    public boolean isPastDatesSelectionAllowed() {
        return pastDatesSelectionAllowed;
    }

    public DatePickerOptions setPastDatesSelectionAllowed(boolean pastDatesSelectionAllowed) {
        this.pastDatesSelectionAllowed = pastDatesSelectionAllowed;
        return this;
    }

    public boolean isSortSelectedDates() {
        return sortSelectedDates;
    }

    public DatePickerOptions setSortSelectedDates(boolean sortSelectedDates) {
        this.sortSelectedDates = sortSelectedDates;
        return this;
    }

    public DatePickerOptions setIsDateSelectableFunction(Function<LocalDate, Boolean> isDateSelectableFunction) {
        this.isDateSelectableFunction = isDateSelectableFunction;
        return this;
    }

    public Function<LocalDate, Boolean> getIsDateSelectableFunction() {
        return isDateSelectableFunction;
    }
}
