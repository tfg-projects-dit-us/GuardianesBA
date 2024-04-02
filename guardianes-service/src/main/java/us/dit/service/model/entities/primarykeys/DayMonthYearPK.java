package us.dit.service.model.entities.primarykeys;

import us.dit.service.model.entities.DayConfiguration;
import lombok.Data;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * This class represents the primary key of the {@link Entity}
 * {@link DayConfiguration}
 * 
 * A {@link DayConfiguration} is uniquely identified with a day, a month and a
 * year
 * 
 * @author miggoncan
 */
@Data
public class DayMonthYearPK implements Serializable {
	private static final long serialVersionUID = 6732382410268399527L;

	private Integer day;
	private Integer month;
	private Integer year;

	public DayMonthYearPK(Integer day, Integer month, Integer year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}

	public DayMonthYearPK() {
	}
}