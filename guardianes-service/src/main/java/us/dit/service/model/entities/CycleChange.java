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
import us.dit.service.model.validation.annotations.ValidCycleChange;
import us.dit.service.model.validation.annotations.ValidDayMonthYear;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;

/**
 * The CycleChange {@link Entity} is used to represent that two {@link Doctor}
 * have changed their cycle shift a certain day of a certain
 * {@link Calendar}
 * 
 * Note the primary key used for this {@link Entity} is composite. Hence, the
 * {@link IdClass} annotation is used. Moreover, CycleChange is a weak entity,
 * so it receives its primary key from the corresponding
 * {@link DayConfiguration}
 * 
 * @see DayMonthYearPK
 * 
 * @author miggoncan
 */
@Data
@EqualsAndHashCode(exclude = "dayConfiguration")
@Entity
@IdClass(DayMonthYearPK.class)
@ValidDayMonthYear
@ValidCycleChange
public class CycleChange {
	@Id
	@Range(min = 1, max = 31)
	@Column(name = "day_configuration_day")
	private Integer day;
	@Id
	@Range(min = 1, max = 12)
	@Column(name = "day_configuration_calendar_month")
	private Integer month;
	@Id
	@Range(min = 1970)
	@Column(name = "day_configuration_calendar_year")
	private Integer year;
	@ManyToOne
	@MapsId
	private DayConfiguration dayConfiguration;

	/**
	 * The cycle giver is the {@link Doctor} that gives their cycle shift to
	 * another {@link Doctor}. Hence, the cycle giver will not have a cycle-shift
	 * this {@link CycleChange#day}
	 */
	@ManyToOne
	private Doctor cycleGiver;

	/**
	 * The cycle receiver is the {@link Doctor} that will take the
	 * cycle shift. Hence, although the cycleReceiver would not have had a
	 * cycle-shift this {@link CycleChange#day}, they will have it.
	 */
	@ManyToOne
	private Doctor cycleReceiver;
	
	public CycleChange(Doctor cycleGiver, Doctor cycleReceiver) {
		this.cycleGiver = cycleGiver;
		this.cycleReceiver = cycleReceiver;
	}
	
	public CycleChange() {}

	public void setDayConfiguration(DayConfiguration dayConfiguration) {
		this.dayConfiguration = dayConfiguration;
		if (dayConfiguration != null) {
			this.day = dayConfiguration.getDay();
			this.month = dayConfiguration.getMonth();
			this.year = dayConfiguration.getYear();
		}
	}
	
	@Override
	public String toString() {
		return CycleChange.class.getSimpleName()
				+ "("
					+ "day=" + day + ", "
					+ "cycleGiver=" + cycleGiver + ", "
					+ "cycleReceiver=" + cycleReceiver
				+ ")";
	}
}
