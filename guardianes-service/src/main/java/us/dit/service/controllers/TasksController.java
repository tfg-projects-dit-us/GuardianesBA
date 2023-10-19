package us.dit.service.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;

import us.dit.service.config.ClearPasswordService;
import us.dit.service.services.TasksService;

/**
 * Controlador ejemplo para arrancar el proceso hola
 */
@Controller
@RequestMapping("/guardianes")
public class TasksController {
	private static final Logger logger = LogManager.getLogger();
	
	/**
	 * tasksService es equivalente a un taskDAO, permite buscar tareas y manejarlas
	 */
	@Autowired
	private TasksService tasksService;
	@Autowired
	private ClearPasswordService clear;

	@GetMapping("/tasks")
	public String getAllMyTasks(HttpSession session, Model model) {
		logger.info("buscando todas las tareas del usuario");
		List<TaskSummary> tasksList = null;

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetails principal = (UserDetails) auth.getPrincipal();
		logger.info("Datos de usuario (principal)" + principal);
		

		//Para no tener que usar el password siempre se crean los clientes con el mismo usuario y contraseña (el kieutil está configurado)
		tasksList = tasksService.findAll(principal.getUsername());
		model.addAttribute("tasks", tasksList);
		/**
		 * Ejemplo de datos de una taskSummary devuelta TaskSummary{ id=2,
		 * name='TareaDePrueba', description='', status='Reserved', actualOwner='user',
		 * createdBy='', createdOn=Sat Oct 07 13:23:42 CEST 2023, processInstanceId=2,
		 * processId='guardianes-kjar.prueba',
		 * containerId='guardianes-kjar-1.0-SNAPSHOT', correlationKey=null,
		 * processType=null}
		 */
		logger.info("vuelve de consultar tareas");
		return "myTasks";
	}

	@GetMapping("/tasks/{taskId}")
	public String getTaskById(@PathVariable Long taskId, Model model) {
		logger.info("buscando la tarea " + taskId);
		TaskInstance task;
		/**
		 * Ejemplo valores de una taskInstance TaskInstance{ id=2, name='TareaDePrueba',
		 * description='', status='Reserved', actualOwner='wbadmin',
		 * processInstanceId=2, processId='guardianes-kjar.prueba',
		 * containerId='guardianes-kjar-1.0-SNAPSHOT', workItemId=2, slaCompliance=null,
		 * slaDueDate=null, correlationKey=2, processType=1}
		 */
		
		task = tasksService.findById(taskId);
		//Map<String, Object> taskInputData = task.getInputData();
		logger.info("Tarea localizada " + task);
		//logger.info("Datos de entrada"+taskInputData);
		model.addAttribute("task", task);
		return "task";
	}
	@GetMapping("/posibletasks")
	public String getAllTasks(Model model) {
		logger.info("buscando todas las tareas");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetails principal = (UserDetails) auth.getPrincipal();
		List<TaskSummary> tasksList = null;
		/**
		 * Ejemplo valores de una taskInstance TaskInstance{ id=2, name='TareaDePrueba',
		 * description='', status='Reserved', actualOwner='wbadmin',
		 * processInstanceId=2, processId='guardianes-kjar.prueba',
		 * containerId='guardianes-kjar-1.0-SNAPSHOT', workItemId=2, slaCompliance=null,
		 * slaDueDate=null, correlationKey=2, processType=1}
		 */
	
		tasksList = tasksService.findPotential(principal.getUsername());
	
		model.addAttribute("tasks", tasksList);
		return "myTasks";
	}

}
