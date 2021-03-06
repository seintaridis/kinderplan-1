package com;

import com.webapplication.dao.elasticRepository.ElasticEventRepository;
import com.webapplication.dao.jpaRepository.*;
import com.webapplication.elasticEntity.ElasticEventEntity;
import com.webapplication.entity.EventEntity;
import com.webapplication.entity.ProviderEntity;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import java.util.Map;

@SpringBootApplication
@EnableJpaRepositories("com.webapplication.dao.jpaRepository")
@EnableElasticsearchRepositories("com.webapplication.dao.elasticRepository")
@Component
public class Application  implements CommandLineRunner{

	@Autowired
	private ElasticsearchOperations es;


	@Autowired
	private UserRepository user;



	@Autowired
	private ProviderRepository providerRepository;

	@Autowired
	private EventRepository eventRepository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/*EventEntity eventEntity = new EventEntity();
		ProviderEntity provider = providerRepository.findProviderById(1);
		eventEntity.setProvider(provider);
		eventEntity.setName("baleto");
		eventEntity.setAge_from(10);
		eventEntity.setAge_to(30);
		eventRepository.saveAndFlush(eventEntity);*/


	//	elasticEventRepository.save(new ElasticEventEntity("11","football","football for small kids","Nikolis","Olimpiakos"));
	//	elasticEventRepository.save(new ElasticEventEntity("12","football","panos"));
	//	elasticEventRepository.save(new ElasticEventEntity("13","baleto","yiota"));
	//	elasticEventRepository.save(new ElasticEventEntity("14","tenis","maria"));





		//bookService.save(new Book("100", "Elasticsearch Basics", "Rambabu Posa", "23-FEB-2017"));
		//bookRepository.saveAndFlush(new Book("1001", "Elasticsearch Basics", "Rambabu Posa", "23-FEB-2017"));


	//	bookService.save(new Book(1002, "Apache Lucene Basics", "Rambabu Posa", "13-MAR-2017"));
		//bookRepository.save(new Book(1002, "Apache Lucene Basics", "Rambabu Posa", "13-MAR-2017"));


	//	bookService.save(new Book(1003, "Apache Solr Basics", "Rambabu Posa", "21-MAR-2017"));
		//bookRepository.save(new Book(1003, "Apache Solr Basics", "Rambabu Posa", "21-MAR-2017"));

		//fuzzey search
		//Page<Book> books = bookService.findByAuthor("Posa", new PageRequest(0, 10));
//System.out.println(23232);
		//List<Book> books = bookService.findByTitle("Elasticsearch Basics");

//		books.forEach(x -> System.out.println(x));


	}



	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};

		tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
		return tomcat;
	}

	private Connector initiateHttpConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		connector.setPort(8080);
		connector.setSecure(false);
		connector.setRedirectPort(8443);

		return connector;
	}



	@Bean
	public CommandLineRunner loadData(UserRepository repository, ParentRepository parentRepository, ProviderRepository providerRepository) {
		return (args) -> {
//			// save a couple of customers

		/*	UserEntity user = new UserEntity("a@a.com","Jack", "Bauer","123","parent",true);
		elasticRepository.save(user);
=======
//		UserEntity user = new UserEntity("a@a.com","Jack", "Bauer","123","parent",true);
//		repository.save(user);
>>>>>>> 36516b37b53da92ce62fc5713a3f3532c9147e06

		/*	ParentEntity parentEntity = new ParentEntity();
			parentEntity.setPoints(20);
			parentEntity.setUser(user);
			parentRepository.save(parentEntity);

			user = new UserEntity("b@b.com","Nathan", "Drake","123","provider",true);
			elasticRepository.save(user);

			ProviderEntity providerEntity = new ProviderEntity();
			providerEntity.setCompanyName("Hercules");
			providerEntity.setVatNumber(42123875);
			providerEntity.setUser(user);
			providerRepository.save(providerEntity);*/
		};
	}

}
