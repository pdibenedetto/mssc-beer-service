package guru.sfg.brewery.model.events;

import guru.springframework.msscbeerservice.web.model.BeerDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewInventoryEvent extends BeerEvent
{
    public NewInventoryEvent(BeerDto beerDto)
    {
        super(beerDto);
    }
}
