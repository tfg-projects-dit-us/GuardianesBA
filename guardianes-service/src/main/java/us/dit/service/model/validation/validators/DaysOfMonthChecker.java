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
package us.dit.service.model.validation.validators;

import us.dit.service.model.entities.AbstractDay;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This class will check that all days of a given {@link YearMonth} exist in a
 * given list of {@link AbstractDay}.
 * 
 * Moreover, it will make sure there are no duplicate days.
 * 
 * This is, for example, if we had a list of {@link AbstractDay} that represent
 * the days of April-2020, this class will check that all days from 1 to 30
 * exist in the list.
 * 
 * @author miggoncan
 */
@Slf4j
public class DaysOfMonthChecker<Day extends AbstractDay> {
	private YearMonth yearMonth;

	private Validator validator;

	public DaysOfMonthChecker(YearMonth yearMonth) {
		this.yearMonth = yearMonth;

		this.validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	public boolean areAllDaysPresent(Set<Day> days) {
		log.debug("Request to validate days: " + days);
		if (days == null) {
			log.debug("The days list is null. The days are invalid");
			return false;
		}

		boolean isValid = true;

		int lengthOfMonth = this.yearMonth.lengthOfMonth();
		int numDays = days.size();

		if (lengthOfMonth != numDays) {
			log.debug("The month " + this.yearMonth + " has " + lengthOfMonth + " days. However, only " + numDays
					+ " days where provided. The days are invalid");
			isValid = false;
		} else if (days.stream().anyMatch((day) -> { // If any of the days is invalid
			log.debug("Validating day: " + day);
			if (day == null) {
				log.debug("The day is null, so it is not valid");
				return false;
			} else {
				log.debug("Checking if the day violates any constraint");
				Set<ConstraintViolation<Day>> violations = validator.validate(day);
				log.debug("The constraint violations are: " + violations);
				return !violations.isEmpty();
			}
		})) {
			log.debug("At least one of the provided days is invalid. The days are invalid");
			isValid = false;
		} else {
			List<Day> daysAsList = new ArrayList<>(days);
			// Sorting the days by the day number allows to easily check all days within the
			// month are present
			Collections.sort(daysAsList);
			log.debug("The days list after being sorted is: " + days);

			Day currentDay;
			for (int i = 0; i < lengthOfMonth; i++) {
				currentDay = daysAsList.get(i);
				// As soon as one of the required days is not present, the Schedule is invalid
				if (i + 1 != currentDay.getDay()) {
					log.debug("The " + (i + 1) + " day of " + this.yearMonth + " is missing. The days are invalid");
					isValid = false;
					break;
				}
			}
		}

		log.debug("The days given are valid: " + isValid);
		return isValid;
	}
}
