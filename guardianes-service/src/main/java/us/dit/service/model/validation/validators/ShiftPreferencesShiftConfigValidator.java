package us.dit.service.model.validation.validators;

import us.dit.service.model.entities.AllowedShift;
import us.dit.service.model.entities.ShiftConfiguration;
import us.dit.service.model.validation.annotations.ValidShiftPreferences;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This validator applies the algorithm in
 * {@link ShiftPreferencesValidator} to the shift preferences of a
 * {@link ShiftConfiguration}
 * 
 * @author miggoncan
 */
public class ShiftPreferencesShiftConfigValidator
		implements ConstraintValidator<ValidShiftPreferences, ShiftConfiguration> {
	@Override
	public boolean isValid(ShiftConfiguration value, ConstraintValidatorContext context) {
		ShiftPreferencesValidator<AllowedShift> validator = new ShiftPreferencesValidator<>();
		return validator.isValid(value.getUnwantedShifts(), value.getUnavailableShifts(), value.getWantedShifts(),
				value.getMandatoryShifts());
	}
}
