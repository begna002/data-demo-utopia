package org.msse.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.msse.demo.config.InitialLoadProperties;
import org.msse.demo.mockdata.customer.CustomerService;
import org.msse.demo.mockdata.music.MusicService;
import org.msse.demo.mockdata.util.Loader;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;

@Slf4j
@Component
@Profile("kafka")
public class KafkaDaemon {
  private final InitialLoadProperties initialLoadProperties;
  private final CustomerService customerService;
  private final MusicService musicService;

  public KafkaDaemon(InitialLoadProperties initialLoadProperties, CustomerService customerService, MusicService musicService) {
    this.initialLoadProperties = initialLoadProperties;
    this.customerService = customerService;
    this.musicService = musicService;
  }

  @PostConstruct
  public void init() {
    log.info("KAFKA DAEMON ENABLED - Beginning initial load...");
    loadCustomers();
    loadArtists();
    loadVenues();
    loadEvents();
    loadTickets();
    loadStreams();

    loadCustomerDefined();
    loadArtistsDefined();
    loadEventDefined();
    loadTicketDefined();
  }

  // every 10 seconds
  @Scheduled(cron = "*/2 * * * * *")
  public void createCustomers() {
    customerService.createCustomer();
  }

  // every 2 seconds
  @Scheduled(cron = "*/2 * * * * *")
  public void streamArtist() {
    musicService.streamArtist();
  }

  // every 5 seconds
  @Scheduled(cron = "*/5 * * * * *")
  public void bookTicket() {
    musicService.bookTicket();
  }

  // every 20 seconds
  @Scheduled(cron = "*/20 * * * * *")
  public void createEvent() {
    musicService.createEvent();
  }

  private void loadCustomerDefined() {
    for (int i = 0; i < 100; i++) {
      String custId = "customer-" + i;
      customerService.createCustomer(custId);
    }
  }
  private void loadArtistsDefined() {
    for (int i = 0; i < 100; i++) {
      String artistId = "artist-" + i;
      musicService.createArtist(artistId);
    }
  }
  private void loadEventDefined() {
    for (int i = 0; i < 200; i++ ) {
      Random random = new Random();
      int randomNumber = random.nextInt(99);

      String artistId = "artist-" + randomNumber;
      String eventId = "event-" + i;
      musicService.createEventDefined(artistId, eventId);
    }
  }

  private void loadTicketDefined() {
    for (int i = 0; i < 5000; i++ ) {
      Random random = new Random();
      int randomNumberCust = random.nextInt(99);
      int randomNumberEvent = random.nextInt(199) ;

      String custId = "customer-" + randomNumberCust;
      String eventId = "event-" + randomNumberEvent;
      musicService.bookTicket(eventId, custId);

    }
  }

  private void loadCustomers() {
    long desiredCustomers = initialLoadProperties.customers();
    long existingCustomers = customerService.customerCount();

    if (existingCustomers >= desiredCustomers) {
      log.info("{} customers already exist ({} desired). Skipping initial load.", existingCustomers, desiredCustomers);
    } else {
      long newCustomers = desiredCustomers - existingCustomers;
      log.info("Generating {} customers ({} already exist).", newCustomers, existingCustomers);
      for (long i = 0; i < newCustomers; i++) {
        customerService.createCustomer();
      }
    }
  }

  private void loadArtists() {
    long desiredArtists = initialLoadProperties.artists();
    long existingArtists = musicService.artistCount();

    if (existingArtists >= desiredArtists) {
      log.info("{} artists already exist ({} desired). Skipping initial load.", existingArtists, desiredArtists);
    } else {
      long newArtists = desiredArtists - existingArtists;
      log.info("Generating {} artists ({} already exist).", newArtists, existingArtists);
      for (long i = 0; i < newArtists; i++) {
        musicService.createArtist();
      }
    }
  }

  private void loadVenues() {
    // loads static set of venues from file
    Loader.loadVenues().forEach(musicService::createVenue);
  }

  private void loadEvents() {
    long desiredEvents = initialLoadProperties.events();
    long existingEvents = musicService.eventCount();

    if (existingEvents >= desiredEvents) {
      log.info("{} events already exist ({} desired). Skipping initial load.", existingEvents, desiredEvents);
    } else {
      long newEvents = desiredEvents - existingEvents;
      log.info("Generating {} events ({} already exist).", newEvents, existingEvents);
      for (long i = 0; i < newEvents; i++) {
        musicService.createEvent();
      }
    }
  }

  private void loadTickets() {
    long desiredTickets = initialLoadProperties.tickets();
    long existingTickets = musicService.ticketCount();

    if (existingTickets >= desiredTickets) {
      log.info("{} tickets already exist ({} desired). Skipping initial load.", existingTickets, desiredTickets);
    } else {
      long newTickets = desiredTickets - existingTickets;
      log.info("Generating {} tickets ({} already exist).", newTickets, existingTickets);
      for (long i = 0; i < newTickets; i++) {
        musicService.bookTicket();
      }
    }
  }

  private void loadStreams() {
    long desiredStreams = initialLoadProperties.streams();
    long existingStreams = musicService.streamCount();

    if (existingStreams >= desiredStreams) {
      log.info("{} streams already exist ({} desired). Skipping initial load.", existingStreams, desiredStreams);
    } else {
      long newStreams = desiredStreams - existingStreams;
      log.info("Generating {} streams ({} already exist).", newStreams, existingStreams);
      for (long i = 0; i < newStreams; i++) {
        musicService.streamArtist();
      }
    }
  }
}
