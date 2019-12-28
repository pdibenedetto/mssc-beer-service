package guru.springframework.msscbeerservice.services.brewing;

import guru.springframework.msscbeerservice.config.JmsConfig;
import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.events.BrewBeerEvent;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import guru.springframework.msscbeerservice.services.inventory.BeerInventoryService;
import guru.springframework.msscbeerservice.web.mappers.BeerMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrewingService
{
    private final BeerInventoryService beerInventoryService;
    private final BeerMapper beerMapper;
    private final BeerRepository beerRepository;
    private final JmsTemplate jmsTemplate;

    @Scheduled(fixedRate = 5000)
    public void checkForLowInventory()
    {
        List<Beer> beerList = beerRepository.findAll();
        beerList.forEach(beer ->
                         {
                             Integer invQOH = beerInventoryService.getOnhandInventory(beer.getId()); // Rest Cakk

                             log.debug(String.format("Minimum On Hand is: %s", beer.getMinOnHand()));
                             log.debug(String.format("Inventory is: %s", invQOH));

                             if(beer.getMinOnHand() >= invQOH)
                             {
                                 jmsTemplate.convertAndSend(JmsConfig.BREWING_REQUEST_QUEUE,
                                                            new BrewBeerEvent(beerMapper.beerToBeerDto(beer)));
                             }
                         });
    }
}
