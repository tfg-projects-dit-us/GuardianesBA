package us.dit.service.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;
import us.dit.service.model.entities.primarykeys.DayMonthYearPK;
import us.dit.service.model.validation.annotations.ValidDayMonthYear;
import us.dit.service.model.validation.annotations.ValidScheduleDay;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author miggoncan
 */
@Data
//This annotations are used instead of @Data as the default hashcode() method
//would case an infinite loop between schedule.hashcode() and
//scheduleDay.hashcode()
@EqualsAndHashCode(exclude = "schedule", callSuper = false)
@Entity
@IdClass(DayMonthYearPK.class)
@ValidDayMonthYear
@ValidScheduleDay
public class ScheduleDay extends AbstractDay {
    @Id
    @Range(min = 1, max = 31)
    @NotNull
    private Integer day;
    @Id
    @Range(min = 1, max = 12)
    @NotNull
    @Column(name = "schedule_calendar_month")
    private Integer month;
    @Id
    @Range(min = 1970)
    @NotNull
    @Column(name = "schedule_calendar_year")
    private Integer year;
    @MapsId
    @ManyToOne
    private Schedule schedule;

    @NotNull
    private Boolean isWorkingDay;

    @ManyToMany
    private List<Doctor> cycle;

    @ManyToMany
    private List<Doctor> shifts;

    @ManyToMany
    private List<Doctor> consultations;


    public ScheduleDay(Integer day, Boolean isWorkingDay) {
        this.day = day;
        this.isWorkingDay = isWorkingDay;
    }

    public ScheduleDay() {
    }

    @Override
    public String toString() {
        return ScheduleDay.class.getSimpleName()
                + "("
                + "day=" + day + ", "
                + "month=" + month + ", "
                + "year=" + year + ", "
                + "isWorkingDay=" + isWorkingDay + ", "
                + "cycle=" + cycle + ", "
                + "shifts=" + shifts + ", "
                + "consultations=" + consultations
                + ")";
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
        if (schedule != null) {
            this.month = schedule.getMonth();
            this.year = schedule.getYear();
        }
    }
}