package us.dit.service.services;

import java.time.LocalDate;
import java.util.*;

import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.UserTaskServicesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import us.dit.model.Calendario;


@Service
public class CalendarTaskService {

	private static final Logger logger = LogManager.getLogger();

	private final String CalendarTaskName = "Establecer festivos";
	@Autowired
	private KieUtilService kieUtils;

	@Autowired
	private TasksService tasksService;
	@Value("${kieserver.containerId}")
	private String containerId;
	@Value("${kieserver.processId}")
	private String processId;

	public void obtainCalendarTask(HttpSession session, String principal) {
		logger.info("Obtenemos la tarea de calendario");
		List<TaskSummary> taskList = tasksService.findPotential(principal);
		logger.info("Las tareas son " + taskList);
		UserTaskServicesClient userClient = kieUtils.getUserTaskServicesClient();

		List<TaskSummary> tasksFilteredByName = taskList.stream()
				.filter(task -> task.getName().equals(CalendarTaskName)
						&& task.getProcessId().equals(processId)
						&& task.getStatus().equals("Ready"))
				.collect(Collectors.toList());

		logger.info("Las tareas filtradas son " + tasksFilteredByName);
		if(!tasksFilteredByName.isEmpty()) {
			logger.info("Hay tareas de calendario disponibles");
			long calendarTaskId = tasksFilteredByName.get(0).getId();
			logger.info("El id de la tarea es " + calendarTaskId);
			session.setAttribute("tareaId", calendarTaskId);
		}

	}

	public void initAndCompleteCalendarTask(String principal, Set<LocalDate> festivos, Long taskId) {
        UserTaskServicesClient userClient = kieUtils.getUserTaskServicesClient();
        logger.info("Reclamamos la tarea de calendario con id " + taskId);
		userClient.claimTask(containerId, taskId, principal);
		logger.info("Comenzamos el completado de la tarea de calendario con id " + taskId);
		userClient.startTask(containerId, taskId, principal);
		logger.info("Construimos el calendario");
		Calendario calendarioFestivos = new Calendario(festivos);
		logger.info("Construimos el mapa con los parametros de salida");
        Map<String, Object> params = new HashMap<>();
        params.put("Id_Calendario_Festivos", calendarioFestivos.getIdCalendario());
		logger.info("Persistimos el calendario " + calendarioFestivos);
		//JpaCalendarioDao jpaCalendarioDao = new JpaCalendarioDao();
		//jpaCalendarioDao.save(calendarioFestivos);
        userClient.completeTask(containerId, taskId, principal, params);
		logger.info("Tarea completada");
    }

	
	
	
}
