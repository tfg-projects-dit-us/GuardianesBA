/**
 * 
 */
package us.dit.service.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import us.dit.service.services.CalendarTaskService;
import us.dit.service.services.JsonParserFestivos;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
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
	private CalendarTaskService calendarTaskService;
	@GetMapping("/calendars")
	public String menu(HttpSession session) {
		this.obtenerTareaEstablecerFestivos(session);
		logger.info("Devolvemos el html del calendario");
	    return "calendar";
		}


	public void obtenerTareaEstablecerFestivos(HttpSession session) {
		logger.info("Iniciando la seleccion de festivos");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetails principal = (UserDetails) auth.getPrincipal();
		logger.info("Datos del usuario principal" + principal);
		List<String> roles = principal.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		if(roles.contains("ROLE_admin") || roles.contains("ROLE_process-admin")) {
			//Que se inicie la tarea
			this.calendarTaskService.obtainCalendarTask(session, principal.getUsername());
			logger.info("Tarea iniciada");
		}
	}
	@PostMapping("/calendars")
	@ResponseBody
	public String inciaryCompletarTareaEstablecerFestivos(HttpSession session, @RequestBody String festivosResponse) throws JsonProcessingException {
		logger.info("El usuario ya ha seleccionado los festivos");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetails principal = (UserDetails) auth.getPrincipal();
		logger.info("Datos de usuario (principal)" + principal);

		Set<LocalDate> festivos = JsonParserFestivos.parseFestivos(festivosResponse);
		logger.info("Los festivos son " + festivos);

		this.calendarTaskService.initAndCompleteCalendarTask(principal.getUsername(), festivos, (Long) session.getAttribute("tareaId"));

        return "redirect:/guardianes/calendar?success";
	}
}
