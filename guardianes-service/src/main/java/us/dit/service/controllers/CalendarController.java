/**
 * 
 */
package us.dit.service.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.server.api.model.instance.TaskSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import us.dit.service.services.CalendarTaskService;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO: El controlador para manejar los calendarios
 */
@Controller
@RequestMapping("/guardianes")
public class CalendarController {
	
	private static final Logger logger = LogManager.getLogger();

	@Autowired
	CalendarTaskService calendarTaskService;
	@GetMapping()
	public String menu() {
	    return "calendar";
		}
	
	@GetMapping("/calendar")
	@ResponseBody
	public String iniciarTareaEstablecerFestivos(HttpSession session) {
		logger.info("Iniciando la seleccion de festivos");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetails principal = (UserDetails) auth.getPrincipal();
		logger.info("Datos del usuario principal" + principal);
		List<String> roles = principal.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		if(roles.contains("admin") || roles.contains("process-admin")) {
			//Que se inicie la tarea
			this.calendarTaskService.initCalendarTask(session, principal.getUsername());
		}

		return "calendar";
	}
	@PostMapping("/calendar")
	public String completarTareaEstablecerFestivos (HttpSession session, String festivosSeleccionados) {
		logger.info("El usuario ya ha seleccionado los festivos");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetails principal = (UserDetails) auth.getPrincipal();
		logger.info("Datos de usuario (principal)" + principal);
		//Obtenemos los festivos seleccionados
		Set<LocalDate> festivos = Arrays.stream(festivosSeleccionados.split(","))
				.filter(s -> !s.isEmpty())
				.map(LocalDate::parse)
				.collect(Collectors.toSet());
		logger.info("Los festivos son " + festivos);
		this.calendarTaskService.completeCalendarTask(principal.getUsername(), festivos, (TaskSummary) session.getAttribute("tarea"));

        return "redirect:/guardianes/calendar?success";
	}
}
