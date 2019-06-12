package gateway;

import reactor.core.publisher.Mono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// tag::code[]
@SpringBootApplication
@EnableConfigurationProperties(UriConfiguration.class)
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // tag::route-locator[]
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
        return builder.routes()
            .route(p -> p
                .path("/api/v1/artists/**")
                .filters(f -> f
                        .hystrix(config -> config
                            .setName("mycmd")
                            .setFallbackUri("forward:/fallback")))
                .uri(uriConfiguration.getArtistsUri()))
            .route(p -> p
                    .path("/api/v1/songs/**")
                    .filters(f -> f
                            .hystrix(config -> config
                                .setName("mycmd")
                                .setFallbackUri("forward:/fallback")))
                    .uri(uriConfiguration.getSongsUri()))
            .route(p -> p
                    .path("/api/v1/playlists/**")
                    .filters(f -> f
                            .hystrix(config -> config
                                .setName("mycmd")
                                .setFallbackUri("forward:/fallback")))
                    .uri(uriConfiguration.getPlaylistsUri()))
            .build();
    }
    // end::route-locator[]

    // tag::fallback[]
    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("This service is not available at the moment.");
    }
    // end::fallback[]
}

// tag::uri-configuration[]
@ConfigurationProperties
class UriConfiguration {

    private String artistsUri = "http://localhost:3000";
    private String songsUri = "http://localhost:3001";
    private String playlistsUri = "http://localhost:3002";
    
	public String getArtistsUri() {
		return artistsUri;
	}

	public String getSongsUri() {
		return songsUri;
	}

	public String getPlaylistsUri() {
		return playlistsUri;
	}
}
// end::uri-configuration[]
// end::code[]