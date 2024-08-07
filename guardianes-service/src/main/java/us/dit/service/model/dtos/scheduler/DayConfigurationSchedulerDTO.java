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
package us.dit.service.model.dtos.scheduler;

import us.dit.service.model.entities.CycleChange;
import us.dit.service.model.entities.DayConfiguration;
import us.dit.service.model.entities.Doctor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This DTO represents the information related to a {@link DayConfiguration}
 * exposed to the scheduler
 * 
 * @author miggoncan
 */
@Data
@Slf4j
public class DayConfigurationSchedulerDTO implements Comparable<DayConfigurationSchedulerDTO>{
	private Integer day;
	private Boolean isWorkingDay;
	private Integer numShifts;
	private Integer numConsultations;
	private Set<DoctorSchedulerDTO> unwantedShifts;
	private Set<DoctorSchedulerDTO> unavailableShifts;
	private Set<DoctorSchedulerDTO> wantedShifts;
	private Set<DoctorSchedulerDTO> mandatoryShifts;
	private List<CycleChangeSchedulerDTO> cycleChanges;
	
	@Override
	public int compareTo(DayConfigurationSchedulerDTO dayConf) {
		if (dayConf == null) {
			return -1;
		}
		
		int result = 0;
		Integer dayConfday = dayConf.getDay();
		if (dayConfday == null) {
			if (this.day == null) {
				result = 0;
			} else {
				result = -1;
			}
		} else if (this.day == null) {
			result = 1;
		} else {
			result = this.day - dayConfday;
		}
		
		return result;
	}

	public DayConfigurationSchedulerDTO(DayConfiguration dayConfig) {
		log.info("Creating a DayConfigurationSchedulerDTO from the DayConfiguration: " + dayConfig);
		if (dayConfig != null) {
			this.day = dayConfig.getDay();
			log.debug("The day is: " + this.day);
			this.isWorkingDay = dayConfig.getIsWorkingDay();
			log.debug("Is working day: " + this.isWorkingDay);
			this.numShifts = dayConfig.getNumShifts();
			log.debug("Num shifts is: " + this.numShifts);
			this.numConsultations = dayConfig.getNumConsultations();
			this.unwantedShifts = this.toSetDoctorsDTO(dayConfig.getUnwantedShifts());
			log.debug("unwantedShifts are: " + this.unwantedShifts);
			this.unavailableShifts = this.toSetDoctorsDTO(dayConfig.getUnavailableShifts());
			log.debug("unavailableShifts are: " + this.unavailableShifts);
			this.wantedShifts = this.toSetDoctorsDTO(dayConfig.getWantedShifts());
			log.debug("wantedShifts are: " + this.wantedShifts);
			this.mandatoryShifts = this.toSetDoctorsDTO(dayConfig.getMandatoryShifts());
			log.debug("mandatoryShifts are: " + this.mandatoryShifts);
			
			List<CycleChangeSchedulerDTO> cycleChangeDTOs = new LinkedList<>();
			List<CycleChange> cycleChanges = dayConfig.getCycleChanges();
			for (CycleChange cycleChange : cycleChanges) {
				cycleChangeDTOs.add(new CycleChangeSchedulerDTO(cycleChange));
			}
			this.cycleChanges = cycleChangeDTOs;
			log.debug("cycleChanges are: " + this.cycleChanges);
		}
		log.info("The created DayConfigurationSchedulerDTO is: " + this);
	}
	
	public DayConfigurationSchedulerDTO() {
	}
	
	private Set<DoctorSchedulerDTO> toSetDoctorsDTO(Set<Doctor> doctors) {
		log.info("Converting to a Set of DoctorSchedulerDTO the Set: " + doctors);
		Set<DoctorSchedulerDTO> doctorDTOs = new HashSet<>();
		if (doctors != null) {
			for (Doctor doctor : doctors) {
				doctorDTOs.add(new DoctorSchedulerDTO(doctor));
			}
		}
		log.info("The converted set is: " + doctorDTOs);
		return doctorDTOs;
	}
}
