
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
package us.dit.service.calDavService;

import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpGetMethod;
import com.github.caldav4j.methods.HttpPutMethod;
import com.github.caldav4j.model.request.CalendarRequest;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.predicate.PeriodRule;
import net.fortuna.ical4j.filter.predicate.PropertyEqualToRule;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Esta clase se encarga de interactuar con el servidor CalDAV a través de la URI del servidor.
 * Este servidor está protegido por usuario y contraseña.
 *
 * @author carcohcal
 * @version 1.0
 * @date 12 feb. 2022
 */
@SuppressWarnings("deprecation")
@Slf4j
@Service
public class CalDav {
    @Value("${calendario.user}")
    private String username;
    @Value("${calendario.psw}")
    private String password;
    @Value("${calendario.uri}")
    private String uri;
    @Autowired
    private EmailService servicioEmail;

    /**
     * Método que se encarga de publicar el calendario @see Calendar generado del mes en el servidor de calendario
     *
     * @param calendar calendario generado con la planificación del mes @see Calendar
     * @throws ClientProtocolException
     * @throws IOException
     * @author carcohcal
     * @date 12 feb. 2022
     */
    public void publicarCalendario(Calendar calendar) throws ClientProtocolException, IOException {

        CalendarRequest cr = new CalendarRequest(calendar);
        CalDAV4JMethodFactory factory = new CalDAV4JMethodFactory();
        HttpPutMethod method = factory.createPutMethod(uri, cr);

        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password)
        );

        HttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .disableAuthCaching()
                .build();
        log.debug("Cliente HTTP PUT " + client);

        HttpResponse response = client.execute(method);
        log.debug("Respuesta peticion PUT: " + response);

    }

    /**
     * Este método contiene la lógica que permite recuperar el calendario ( @see Calendar) de un {@link  guardians.model.entities.Doctor} para un mes concreto através de su email
     *
     * @param mesAnio
     * @param email
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParserException
     * @throws CalDAV4JException
     * @throws AddressException
     * @throws MessagingException
     * @author carcohcal
     * @date 12 feb. 2022
     */

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void recuperarCalendario(YearMonth mesAnio, String email) throws IOException, URISyntaxException, ParserException, CalDAV4JException, AddressException, MessagingException {


        // Retrieve the Calendar from the response.
        Calendar calendar = getCalendarioServer();


        log.info("Fecha: " + mesAnio);
        DateTime start = new DateTime();
        start.setTime(conviertefecha(mesAnio.getYear(), mesAnio.getMonthValue(), 1).getTime());
        YearMonth yearMonthObject = YearMonth.of(mesAnio.getYear(), mesAnio.getMonthValue());
        int daysInMonth = yearMonthObject.lengthOfMonth();
        DateTime duration = new DateTime();
        duration.setTime(conviertefecha(mesAnio.getYear(), mesAnio.getMonthValue(), daysInMonth + 1).getTime());
        net.fortuna.ical4j.model.Period period = new net.fortuna.ical4j.model.Period(start, duration);
        Filter filter = new Filter(new PeriodRule(period));
        Collection eventsToday = filter.filter(calendar.getComponents(Component.VEVENT));


        Attendee a1 = new Attendee(new URI("mailto:" + email));
        Predicate<Component> attendee1RuleMatch = new PropertyEqualToRule<>(a1);
        Filter<CalendarComponent> filtro = new Filter<CalendarComponent>(new Predicate[]{attendee1RuleMatch}, Filter.MATCH_ALL);

        Collection eventosDoctor = filtro.filter(eventsToday);
        if (eventosDoctor.size() != 0) {


            String nomFich = "calendario" + mesAnio.toString() + ".ics";

            Calendar calendarioDoctor = new Calendar();
            Iterator iterador = eventosDoctor.iterator();
            while (iterador.hasNext()) {
                VEvent evento = (VEvent) iterador.next();
                PropertyList<Property> asistentes = evento.getProperties(Property.ATTENDEE);

                for (int i = 0; i < asistentes.size(); i++) {
                    evento.getProperties().remove(asistentes.get(i));

                }
                calendarioDoctor.getComponents().add(evento);
            }
            generaFichero.generarFichero(calendarioDoctor, nomFich);

            log.info("Fichero creado con nombre " + nomFich);

            //Se envia por email el calendario individual
            servicioEmail.enviarEmail(email, nomFich);

        } else {
            log.info("No hay eventos para ese doctor. Thorwing EventNotFoundException");
            throw new RuntimeException("No hay eventos para ese doctor." + "Mes:" + mesAnio.getMonth() + " Anio:" + mesAnio.getYear() + " Email:" + email);
        }

    }

    /**
     * Método que recupera el calendario del servidor através de la URI
     *
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws ParserException
     * @author carcohcal
     * @date 12 feb. 2022
     */
    public Calendar getCalendarioServer() throws ClientProtocolException, IOException, ParserException {
        CalDAV4JMethodFactory factory = new CalDAV4JMethodFactory();
        HttpGetMethod method = factory.createGetMethod(uri);

        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password)
        );

        HttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .disableAuthCaching()
                .build();

        log.info("Cliente HTTP creado: " + client);

        // Execute the method.
        HttpResponse response = client.execute(method);
        Calendar calendario = null;
        if (response.getStatusLine().getStatusCode() == 404) {
            calendario = new Calendar();
            calendario.getProperties().add(new ProdId("-//Calendario Guardias//"));
            calendario.getProperties().add(Version.VERSION_2_0);
            calendario.getProperties().add(CalScale.GREGORIAN);

            TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();


            VTimeZone tz = registry.getTimeZone("Europe/Madrid").getVTimeZone();
            calendario.getComponents().add(tz);


        } else {
            // Retrieve the Calendar from the response.

            HttpEntity entity = response.getEntity();


            // Read the contents of an entity and return it as a String.
            String content = EntityUtils.toString(entity);


            StringReader stream = new StringReader(content);

            CalendarBuilder builder = new CalendarBuilder();

            calendario = builder.build(stream);
        }
        log.info("Calendario recuperado");
        return calendario;
    }


    /**
     * metodo que devuelve la fecha en formato Date
     *
     * @param anio
     * @param mes
     * @param numDia
     * @return
     * @author carcohcal
     */
    public Date conviertefecha(Integer anio, Integer mes, Integer numDia) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(anio, mes - 1, numDia);

        return new Date(calendar.getTime());
    }
}
