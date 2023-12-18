package us.dit.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Calendario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idCalendario;

	@ElementCollection
	private Set<LocalDate> festivos;

	public Calendario(Long idCalendario, Set<LocalDate> festivos) {
		this.idCalendario = idCalendario;
		this.festivos = festivos;
	}

	public void agregarFestivo(int year, int month, int day) {
		LocalDate festivo = LocalDate.of(year, month, day);
		festivos.add(festivo);
	}
	
	public boolean esFestivo(LocalDate fecha) {
		return festivos.contains(fecha);
	}
	
	public void eliminarFestivo(LocalDate fecha) {
		festivos.remove(fecha);
	}
	
	public Set<LocalDate> obtenerFestivos() {
		return festivos;
	}

	public Long getIdCalendario() {
		return idCalendario;
	}

	public void setIdCalendario(Long idCalendario) {
		this.idCalendario = idCalendario;
	}

}
