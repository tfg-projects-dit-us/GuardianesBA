package us.dit.service.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.springframework.beans.factory.annotation.Value;
/**
 * Esta clase debe contener los servicios que proporcionan capacidades para interaccionar con el kie server
 * Debe ser el único responsable de estos aspectos, liberando a los demás de esta necesidad
 * Debería ser un Bean, un componente spring, para poder inyectarlo en todos aquellos que lo necesiten, especialmente servicios
 */
public class KieUtil implements KieUtilService {

	@Value("${kieserver.location}")
	private String URL;
	private static final Logger logger = LogManager.getLogger();
	private String USERNAME;
	private String PASSWORD;

	private KieServicesConfiguration config;

	public KieUtil(String uRL, String uSERNAME, String pASSWORD) {
		URL = uRL;
		USERNAME = uSERNAME;
		PASSWORD = pASSWORD;
	}

	public KieUtil(String uSERNAME, String pASSWORD) {
		logger.info("creando kieutil");
		USERNAME = uSERNAME;
		PASSWORD = pASSWORD;
	}

	@Override
	public ProcessServicesClient getProcessServicesClient() {
		logger.info("getprocessservicesclient");
		KieServicesClient kieServicesClient = getKieServicesClient();
		ProcessServicesClient processServicesClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
		logger.info("salgo de getprocessservicesclient");
		return processServicesClient;
	}

	@Override
	public UserTaskServicesClient getUserTaskServicesClient() {
		KieServicesClient kieServicesClient = getKieServicesClient();
		UserTaskServicesClient userClient = kieServicesClient.getServicesClient(UserTaskServicesClient.class);

		return userClient;
	}

	@Override
	public QueryServicesClient getQueryServicesClient() {
		KieServicesClient kieServicesClient = getKieServicesClient();
		QueryServicesClient queryClient = kieServicesClient.getServicesClient(QueryServicesClient.class);

		return queryClient;
	}

	private KieServicesClient getKieServicesClient() {
		logger.info("entro en getkieservicesclient con url " + URL);
		config = KieServicesFactory.newRestConfiguration(URL, USERNAME, PASSWORD);
		logger.info("salgo de newrestconfigurarion");
		config.setMarshallingFormat(MarshallingFormat.JSON);

		return KieServicesFactory.newKieServicesClient(config);
	}

}
