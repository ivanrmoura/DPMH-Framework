package br.ufma.lsdi.bpad.component;


import com.google.common.eventbus.EventBus;

public class EventBusAdmin {

    private EventBus eventBus;

    public EventBusAdmin(){
        eventBus = new EventBus();
    }

    public void postEvent(Object object){
        eventBus.post(object);
    }

    public void registerListenner(Object object){
        eventBus.register(object);
    }

    public void unregisterListenner(Object object){
        eventBus.unregister(object);
    }



}
