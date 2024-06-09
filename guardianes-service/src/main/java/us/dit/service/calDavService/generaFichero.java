package us.dit.service.calDavService;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.validate.ValidationException;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Clase que crea el fichero
 *
 * @author carcohcal
 * @date 12 feb. 2022
 */
@Service
@Slf4j
public class generaFichero {

    /**
     * @param calendario
     * @param nomFich
     * @throws ValidationException
     * @throws IOException
     * @author
     * @version
     */
    public static void generarFichero(Calendar calendario, String nomFich) throws ValidationException, IOException {

        FileOutputStream fout;
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.setValidating(false);
        fout = new FileOutputStream(nomFich);
        outputter.output(calendario, fout);
        log.info("Fichero " + nomFich + " creado");
        fout.close();


    }


}
