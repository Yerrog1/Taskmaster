package es.iesteis.proyectoud4bugstars.service;

import es.iesteis.proyectoud4bugstars.AppSettings;
import es.iesteis.proyectoud4bugstars.actions.commands.ProfileLinkInput;
import es.iesteis.proyectoud4bugstars.entity.Member;
import es.iesteis.proyectoud4bugstars.entity.ProfileLink;
import es.iesteis.proyectoud4bugstars.entity.ProfileLinkRepository;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Arrays;

@Component
public class LinkService {
	public static final String LINK_VERIFICATION_QUEUE = "link-verification";
	private final ProfileLinkRepository profileLinkRepository;
	private final AppSettings appSettings;
	private final Logger logger = LoggerFactory.getLogger(LinkService.class);
	private final TaskExecutor taskExecutor;

	public LinkService(ProfileLinkRepository profileLinkRepository,
					   AppSettings appSettings, TaskExecutor taskExecutor) {
		this.profileLinkRepository = profileLinkRepository;
		this.appSettings = appSettings;
		this.taskExecutor = taskExecutor;
	}

	public void setProfileLinks(
		Member actor,
		ProfileLinkInput[] links
	) {
		if (links.length > 4) {
			links = Arrays.copyOf(links, 4);
		}

		var memberLinks = profileLinkRepository.findByMember(actor);
		profileLinkRepository.deleteAll(memberLinks);

		for (ProfileLinkInput l : links) {
			var profileLink = new ProfileLink()
				.setMember(actor)
				.setName(l.name())
				.setUrl(l.url())
				.setVerified(false);
			taskExecutor.execute(new LinkVerificationTask(profileLink));
		}
	}

	private class LinkVerificationTask implements Runnable {
		private final ProfileLink link;

		public LinkVerificationTask(ProfileLink link) {
			this.link = link;
		}

		@Override
		public void run() {
			link.setVerified(queryUrlAndCheckIfExists(
				link.getUrl(),
				appSettings.getFrontendBaseUrl() + "/u/" + link.getMember().getId()
			));

			profileLinkRepository.save(link);

		}

		private boolean queryUrlAndCheckIfExists(String link, String expectedUrl) {
			HttpClient client = HttpClient.newHttpClient();
			try {
				var request = HttpRequest.newBuilder()
					.uri(URI.create(link))
					.build();

				Instant start = Instant.now();
				var response = client.send(request, HttpResponse.BodyHandlers.ofString());
				Instant end = Instant.now();

				logger.info("Queried URL {} (took {}ms)", link, end.toEpochMilli() - start.toEpochMilli());

				var foundLink = Jsoup
					.parse(response.body())
					.getElementsByAttributeValueMatching("rel", "me")
					.stream()
					.filter(e -> e.is("a"))
					.filter(e -> e.hasAttr("href"))
					.filter(e -> e.attr("href").equals(expectedUrl))
					.findFirst();

				logger.info("Found link: {}", foundLink.isPresent());

				return foundLink.isPresent();
			} catch (RuntimeException e) {
				logger.error("Invalid URL: " + link);
				return false;
			} catch (IOException | InterruptedException e) {
				logger.error("Error while querying URL: " + link);
				return false;
			}
		}
	}
}
