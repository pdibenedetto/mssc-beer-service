package guru.springframework.msscbeerservice.services.brewing;

//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.jms.core.JmsTemplate;

///**
// * Created by jt on 2019-07-21.
// */
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class BrewBeerListener {
//
//    private final BeerRepository beerRepository;
////    private final JmsTemplate jmsTemplate;
//
//    @Transactional
//    //@JmsListener(destination = JmsConfig.BREWING_REQUEST_QUEUE)
//    public void listen(BrewBeerEvent event){
//        BeerDto beerDto = event.getBeerDto();
//
//        Beer beer = beerRepository.getOne(beerDto.getId());
//
//        beerDto.setQuantityOnHand(beer.getQuantityToBrew());
//
//        NewInventoryEvent newInventoryEvent = new NewInventoryEvent(beerDto);
//
//        log.debug("Brewed beer " + beer.getMinOnHand() + " : QOH: " + beerDto.getQuantityOnHand());
//
//     //   jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE, newInventoryEvent);
//    }
//}

import guru.springframework.msscbeerservice.config.JmsConfig;
import guru.springframework.msscbeerservice.domain.Beer;
import guru.sfg.brewery.model.events.BrewBeerEvent;
import guru.sfg.brewery.model.events.NewInventoryEvent;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import guru.sfg.brewery.model.BeerDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service    // Could be a component here
@RequiredArgsConstructor
@Slf4j
public class BrewBeerListener
{
    private final BeerRepository beerRepository;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.BREWING_REQUEST_QUEUE)
    public void listen(BrewBeerEvent brewBeerEvent)
    {
        // The quantity to brew on the Dto was not exposed to the public.
        // A DB call is needed to get that value. This could be a valid argument to add
        // this to the Dto and expose it.
        BeerDto beerDto = brewBeerEvent.getBeerDto();

        Optional<Beer> beer = beerRepository.findById(beerDto.getId());
        beer.ifPresent(b ->
                       {
                         beerDto.setQuantityOnHand(b.getQuantityToBrew());
                         NewInventoryEvent newInventoryEvent = new NewInventoryEvent(beerDto);

//                         log.debug(String.format("Brewed beer %s : QOH: %s"),
//                                                    b.getMinOnHand(),
//                                                    beerDto.getQuantityOnHand());

                         jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE, newInventoryEvent);
                       });




    }
}
