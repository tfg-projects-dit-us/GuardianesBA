/**
*  This file is part of GuardianesBA - Business Application for processes managing healthcare tasks planning and supervision.
*  Copyright (C) 2024  Universidad de Sevilla/Departamento de Ingeniería Telemática
*
*  GuardianesBA is free software: you can redistribute it and/or
*  modify it under the terms of the GNU General Public License as published
*  by the Free Software Foundation, either version 3 of the License, or (at
*  your option) any later version.
*
*  GuardianesBA is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
*  Public License for more details.
*
*  You should have received a copy of the GNU General Public License along
*  with GuardianesBA. If not, see <https://www.gnu.org/licenses/>.
**/
package us.dit.service.model.entities;

import us.dit.service.model.entities.primarykeys.DayMonthYearPK;
import us.dit.service.model.validation.annotations.ValidDayMonthYear;
import us.dit.service.model.validation.annotations.ValidShiftPreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;

/**
 * This {@link Entity} represents a specific conditions to take into account
 * when creating the schedule for a certain day of a {@link Calendar}
 * 
 * Moreover, a DayConfiguration can be used to express one-time additions to the
 * current {@link ShiftConfiguration}. For example, a DayConfiguration can
 * specify a certain {@link Doctor} would like to take their shift this day.
 * Another example of the use of this entity would be to specify a
 * {@link Doctor} is not available this day and, therefore, no shift should be
 * assigned to them this day
 * 
 * This DayConfiguration will have higher priority than the current
 * {@link ShiftConfiguration}. For example, if a {@link Doctor} is has the
 * "Monday" shift configured as unavailable, but one certain day they would like
 * to take this shift, the {@link DayConfiguration#wantedShifts} can be used to
 * indicate it.
 * 
 * Important: A DayConfiguration should not be used as a substitute to
 * {@link ShiftConfiguration}, but as a way to express a one-off change
 * 
 * Note the primary key of DayConfiguration is composite, as indicated by the
 * {@link IdClass} annotation. Moreover a DayConfiguration receives the month
 * and year of its primary key from its corresponding associated
 * {@link Calendar}
 * 
 * @author miggoncan
 */
@Data
//This annotations are used instead of @Data as the default hashcode() method 
// would case an infinite loop between calendar.hashcode() and 
// dayConfiguration.hashcode()
@EqualsAndHashCode(exclude = "calendar", callSuper = false)
@Entity
@IdClass(DayMonthYearPK.class)
@ValidDayMonthYear
@ValidShiftPreferences
public class DayConfiguration extends AbstractDay {
	@Id
	@Range(min = 1, max = 31)
	private Integer day;
	@Id
	@Range(min = 1, max = 12)
	@Column(name = "calendar_month")
	private Integer month;
	@Id
	@Range(min = 1970)
	@Column(name = "calendar_year")
	private Integer year;
	@MapsId
	@ManyToOne
	private Calendar calendar;

	/**
	 * isWorkingDay should be set to false not only for weekends, but for holidays
	 * as well
	 */
	@Column(nullable = false)
	@NotNull
	private Boolean isWorkingDay;

	/**
	 * numShifts indicates the number of non-cycle-shifts that will be scheduled for
	 * this {@link #day} Note {@link DayConfiguration#numConsultations} is counted
	 * separetly
	 */
	@Column(nullable = false)
	@PositiveOrZero
	@NotNull
	private Integer numShifts;

	/**
	 * numConsultations indicates the number of consultations that will be scheduled
	 * for this {@link #day}
	 */
	@Column(nullable = false)
	@PositiveOrZero
	@NotNull
	private Integer numConsultations;

	/**
	 * unwantedShifts indicates the {@link Doctor}s that would rather not have a
	 * shift this {@link #day}
	 */
	@ManyToMany
	private Set<Doctor> unwantedShifts;

	/**
	 * unavailableShifts indicates the {@link Doctor}s that cannot, in any way, have
	 * a shift this {@link #day}
	 */
	@ManyToMany
	private Set<Doctor> unavailableShifts;

	/**
	 * wantedShifts indicates the {@link Doctor}s that would like to have one of
	 * their corresponding shifts this {@link #day}
	 */
	@ManyToMany
	private Set<Doctor> wantedShifts;

	/**
	 * mandatoryShifts indicates the {@link Doctor}s that HAVE to have a shift this
	 * {@link #day}
	 */
	@ManyToMany
	private Set<Doctor> mandatoryShifts;

	/**
	 * cycleChanges indicates a change that should be done to the cycle-shifts of
	 * this {@link #day} when creating its schedule
	 */
	@OneToMany(mappedBy = "dayConfiguration", cascade = CascadeType.ALL)
	private List<CycleChange> cycleChanges;

	public DayConfiguration(Integer day, Boolean isWorkingDay, Integer numShifts, Integer numConsultations) {
		this.day = day;
		this.isWorkingDay = isWorkingDay;
		this.numShifts = numShifts;
		this.numConsultations = numConsultations;
	}

	public DayConfiguration() {
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
		if (calendar != null) {
			this.month = calendar.getMonth();
			this.year = calendar.getYear();
			// If not updated yet, this will update the references in cycleChanges
			this.setCycleChanges(this.getCycleChanges());
		}
	}
	
	public void setCycleChanges(List<CycleChange> cycleChanges) {
		this.cycleChanges = cycleChanges;
		if (cycleChanges != null) {
			for (CycleChange cycleChange : cycleChanges) {
				cycleChange.setDayConfiguration(this);
			}
		}
	}

	// the toString method of the @Data annotation is not used as it can cause an infinite loop between the Calendar#toString method and this method
	@Override
	public String toString() {
		return DayConfiguration.class.getSimpleName()
				+ "("
					+ "day=" + day + ", "
					+ "month=" + month + ", "
					+ "year=" + year + ", "
					+ "isWorkingDay=" + isWorkingDay + ", "
					+ "numShifts=" + numShifts + ", "
					+ "numConsultations=" + numConsultations + ", "
					+ "unwantedShifts=" + unwantedShifts + ", "
					+ "unavailableShifts=" + unavailableShifts + ", "
					+ "wantedShifts=" + wantedShifts + ", "
					+ "mandatoryShifts=" + mandatoryShifts + ", "
					+ "cycleChanges=" + cycleChanges
				+ ")";
	}
}
